package org.taktik.icure.dao.replicator

import com.hazelcast.core.HazelcastInstance
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.ektorp.http.URI
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.taktik.couchdb.ClientImpl
import org.taktik.couchdb.subscribeForChanges
import org.taktik.icure.concurrency.doPeriodicallyOnOneReplicaForever
import org.taktik.icure.dao.GenericDAO
import org.taktik.icure.dao.GroupDAO
import org.taktik.icure.entities.Group
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct

class ReplicationManager(private val hazelcast: HazelcastInstance, private val sslContextFactory: SslContextFactory, private val groupDAO: GroupDAO, private val replicators: List<Replicator>, private val allDaos: List<GenericDAO<*>>) {

    @Value("\${icure.couchdb.username}")
    private lateinit var couchDbUsername: String
    @Value("\${icure.couchdb.password}")
    private lateinit var couchDbPassword: String
    @Value("\${icure.couchdb.prefix}")
    private lateinit var couchDbPrefix: String
    @Value("\${icure.couchdb.url}")
    private lateinit var couchDbUrl: String

    private val globalCheckIntervalMillis: Long = 60_000
    private val delayAfterErrorMillis: Long = 10_000
    private val replicationStartConcurrency: Int = 32
    private val replicationSemaphore = Semaphore(replicationStartConcurrency)


    private val httpClient: HttpClient by lazy {
        HttpClient(this.sslContextFactory).apply {
            try {
                maxConnectionsPerDestination = 65535
                maxRequestsQueuedPerDestination = 4096
                start()
            } catch (e: Exception) {
                log.error("Cannot start HTTP client", e)
                try {
                    stop()
                } catch (ignored: Exception) {
                }
                throw e
            }
        }
    }

    private class ReplicatorStatus(val job: Job)

    private class GroupReplicationStatus(private val mutex: Mutex = Mutex(), var standardDocumentsInitialized: Boolean = false, val replicatorStatuses: MutableMap<Replicator, ReplicatorStatus> = HashMap()) : Mutex by mutex {
        fun failedReplicators(): Map<Replicator, ReplicatorStatus> = replicatorStatuses.filterValues { it.job.isCompleted }
        fun runningReplicators(): Map<Replicator, ReplicatorStatus> = replicatorStatuses.filterValues { it.job.isActive }
    }

    private var groupObserver: Job? = null
    private val groupReplicationStatuses: MutableMap<String, GroupReplicationStatus> = ConcurrentHashMap()

    private fun groupReplicationStatus(group: Group): GroupReplicationStatus = groupReplicationStatuses.computeIfAbsent(group.id) {
        GroupReplicationStatus()
    }

    @FlowPreview
    @PostConstruct
    fun init() {
        GlobalScope.launch {
            val lockName = "${ReplicationManager::class.java.canonicalName}.lock"
            log.debug("Using distributed lock $lockName")
            val lock = hazelcast.getLock(lockName)
            // This should block forever
            doPeriodicallyOnOneReplicaForever(lock, globalCheckIntervalMillis, delayAfterErrorMillis, {
                ensureGroupObserverStarted()
                ensureReplicationStartedForAllGroups()
            }, {
                // On lock lost
                // Cancel all active jobs
                val cause = CancellationException("Replication lock lost")
                groupObserver?.cancel(cause)
                groupReplicationStatuses.values
                        .flatMap { groupReplicationStatus ->
                            groupReplicationStatus.replicatorStatuses.values.map { it.job }
                        }
                        .onEach {
                            it.cancel(cause)
                        }
                        .plus(groupObserver)
                        .filterNotNull()
                        .joinAll()
            })
        }
    }

    private suspend fun ensureReplicationStartedForAllGroups() {
        coroutineScope {
            val allGroups = withContext(IO) { groupDAO.all.sortedBy { it.id } }
            log.debug("Ensuring all replications started for ${allGroups.size} groups")
            val ensureReplicationStartedJobs = allGroups.map { group ->
                async {
                    replicationSemaphore.withPermit {
                        ensureGroupReplicationStarted(group)
                    }
                }
            }
            ensureReplicationStartedJobs.joinAll()
            log.debug("Done ensuring all replications started for ${allGroups.size} groups")
        }
    }

    @FlowPreview
    private fun CoroutineScope.ensureGroupObserverStarted() {
        if (groupObserver?.isActive != true) {
            groupObserver = launch {
                subscribeForNewGroups()
            }.also {
                it.invokeOnCompletion { error ->
                    if (error != null) {
                        log.warn("Group observer error", error)
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    private suspend fun subscribeForNewGroups() {
        log.info("Starting group observer")
        val client = ClientImpl(httpClient, URI.of(couchDbUrl).append("$couchDbPrefix-config"), couchDbUsername, couchDbPassword)
        val changes = client.subscribeForChanges<Group>()
        changes.collect { change ->
            val groupId = change.id
            log.info("New group detected : $groupId")
            val group = groupDAO.get(groupId)
            ensureGroupReplicationStarted(group)
        }
    }

    private suspend fun ensureGroupReplicationStarted(group: Group) {
        log.debug("Ensuring all replications started for group ${group.id}")
        coroutineScope {
            ensureStandardDesignDocumentInitialized(group)
            ensureAllReplicatorsStarted(group)
        }
        log.debug("Done starting all replications for group ${group.id}")
    }

    private suspend fun ensureStandardDesignDocumentInitialized(group: Group) {
        val groupReplicationStatus = groupReplicationStatus(group)
        // Mutex access to groupReplicationStatus object
        groupReplicationStatus.withLock {
            if (!groupReplicationStatus.standardDocumentsInitialized) {
                log.info("Initializing Standard docs for ${group.id}")
                withContext(IO) { allDaos.forEach { dao -> dao.initStandardDesignDocument(group) } }
                groupReplicationStatus.standardDocumentsInitialized = true
                log.info("Standard docs initialised for ${group.id}")
            }
        }
    }

    private suspend fun ensureAllReplicatorsStarted(group: Group) {
        val groupReplicationStatus = groupReplicationStatus(group)
        // Mutex access to groupReplicationStatus object
        groupReplicationStatus.withLock {
            val failedReplicators = groupReplicationStatus.failedReplicators()

            failedReplicators.forEach { entry ->
                log.info("Replicator ${entry.key} completed, relaunching")
            }

            // Launch completed or not-yet-launched replicators
            val replicatorsToLaunch = replicators - groupReplicationStatus.runningReplicators().keys

            val launchedReplicators: MutableMap<Replicator, ReplicatorStatus> = replicatorsToLaunch.fold(HashMap(), { acc, replicator ->
                acc.also {
                    log.info("Starting $replicator for group ${group.id}")
                    try {
                        val replicationJob = replicator.startReplication(group)
                        it[replicator] = ReplicatorStatus(replicationJob)
                    } catch (e: Exception) {
                        log.error("Error while starting replicator $replicator for group ${group.id}", e)
                    }
                }
            })
            groupReplicationStatus.replicatorStatuses.putAll(launchedReplicators)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ReplicationManager::class.java)
    }
}
