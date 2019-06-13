package org.taktik.icure.dao.replicator

import com.hazelcast.core.HazelcastInstance
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.ektorp.http.URI
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.taktik.couchdb.ClientImpl
import org.taktik.couchdb.subscribeForChanges
import org.taktik.icure.dao.GenericDAO
import org.taktik.icure.dao.GroupDAO
import org.taktik.icure.entities.Group
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
                } catch (ignored: Exception) {}
                throw e
            }
        }
    }

    private var groupObserver:Job? = null

    @FlowPreview
    @PostConstruct
    fun init() {
        GlobalScope.launch {
            val lock = hazelcast.getLock(javaClass.canonicalName + ".lock")
            while (true) {
                try {
                    if (lock.tryLock()) {
                        try {
                            log.info("Captured lock and starting group observer")
                            groupObserver?.cancelAndJoin()
                            groupObserver = launch { startGroupObserver() }
                            startReplicatorsForAllGroups()
                            while (true) {
                                delay(1000)
                            }
                        } finally {
                            lock.unlock()
                        }
                    } else {
                        //ensureObserverStopped()
                        // Wait a bit then try to acquire lock to launch Group Observer
                        delay(10000)
                    }
                } catch (e: Exception) {
                    log.warn("Exception in GroupObserver starter", e)
                }
            }
        }
    }

    private suspend fun startReplicatorsForAllGroups() {
        val allGroups = withContext(IO) { groupDAO.all.sortedBy { it.id } }
        allGroups.forEach { group ->
            //Compose them so they do not start all at the same time
            prepareDesignDocumentsAndStartReplications(group)
        }
    }

    @FlowPreview
    private suspend fun startGroupObserver() {
        log.info("Start group observer")
        val client = ClientImpl(httpClient, URI.of(couchDbUrl).append("$couchDbPrefix-config"), couchDbUsername, couchDbPassword)
        val changes = client.subscribeForChanges<Group>()

        changes.collect { change ->
            val docId = change.id
            log.info("Start observing : $docId")
            prepareDesignDocumentsAndStartReplications(groupDAO.get(docId))
        }
    }

    private suspend fun prepareDesignDocumentsAndStartReplications(group: Group) {
        log.info("Starting replications for {}", group.id)

        withContext(IO) { allDaos.forEach { dao -> dao.initStandardDesignDocument(group) } }
        log.info("Standard docs initialised for {} ", group.id)

        for (replicator in replicators) {
            log.info("Starting replicator $replicator")
            replicator.startReplication(group)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ReplicationManager::class.java)
    }

}
