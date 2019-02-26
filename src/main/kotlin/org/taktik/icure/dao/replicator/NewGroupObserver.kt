package org.taktik.icure.dao.replicator

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IAtomicLong
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.taktik.couchdb.CouchDbInstance
import org.taktik.icure.dao.GenericDAO
import org.taktik.icure.dao.GroupDAO
import org.taktik.icure.entities.Group

import javax.annotation.PostConstruct
import java.net.URI
import java.util.HashMap
import java.util.HashSet
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * starts wired replicators on new group
 *
 * @author Bernard Paulus - 13/03/2017
 */
class NewGroupObserver(private val hazelcast: HazelcastInstance, private val sslContextFactory: SslContextFactory, private val groupDAO: GroupDAO, private val replicators: List<FilteredReplicator>, private val allDaos: List<GenericDAO<*>>) {

    @Value("\${icure.couchdb.username}")
    private val couchDbUsername: String? = null
    @Value("\${icure.couchdb.password}")
    private val couchDbPassword: String? = null
    @Value("\${icure.couchdb.prefix}")
    private val couchDbPrefix: String? = null
    @Value("\${icure.couchdb.url}")
    private val couchDbUrl: String? = null
    private val pendingReplications = HashMap<String, PendingReplication>()

    private var httpClient: HttpClient? = null

    private val lastHeartBeat: IAtomicLong
        get() = hazelcast.getAtomicLong(javaClass.canonicalName + ".lastHeartBeat")

    @PostConstruct
    fun init() {
        val lock = hazelcast.getLock(javaClass.canonicalName + ".lock")
        lock.lock()
        try {
            val isExecutorStarted = hazelcast.getAtomicReference<Boolean>(javaClass.canonicalName + ".isExecutorStarted")
            if (java.lang.Boolean.TRUE != isExecutorStarted.get()) {
                val scheduledExecutor = hazelcast.getScheduledExecutorService(javaClass.canonicalName + ".scheduledExecutor")
                scheduledExecutor.scheduleAtFixedRate(ObserverStarter(), 1, 1, TimeUnit.MINUTES)
                isExecutorStarted.set(java.lang.Boolean.TRUE)

                log.info("Captured lock and starting group observer")
                this.ensureObserverStarted()
            }
        } finally {
            lock.unlock()
        }
    }

    fun ensureObserverStarted() {
        val lastHeartBeat = lastHeartBeat
        val time = System.currentTimeMillis()
        if (lastHeartBeat.get() == 0L) {
            lastHeartBeat.set(time)
            // init
            startObserver()
            val groups = groupDAO.all.sortedBy { it.id }
            var f = CompletableFuture.completedFuture(true)
            groups.forEach { group ->
                //Compose them so they do not start all at the same time
                f = f.thenCompose { _ -> prepareDesignDocumentsAndStartReplications(group) }.exceptionally { t ->
                    log.error("An error occurred during Observers start", t)
                    false
                }
            }
        } else if (lastHeartBeat.get() < time - IS_SYNC_DEAD_TIMEOUT) {
            startObserver()
        }
    }

    private fun startObserver() {
        log.info("Start group observer")
        startHttpClient()

        val dbInstance = CouchDbInstance(httpClient!!, URI.create(couchDbUrl!!), couchDbPrefix!! + "-config", couchDbUsername, couchDbPassword)
        log.info("Start group observer changes")
        dbInstance.changes("now",
                           { change ->
                               log.info("Detected new group : ${change.doc?.get("_id") ?: ""}")
                               lastHeartBeat.set(System.currentTimeMillis())
                               change.doc?.let { doc ->
                                   if (Group::class.java.canonicalName == doc["java_type"]) {
                                       log.info("Start observing : ${change.doc?.get("_id") ?: ""}")
                                       prepareDesignDocumentsAndStartReplications(groupDAO.get(doc["_id"] as String))
                                   }
                               }
                           },
                           {
                               lastHeartBeat.set(System.currentTimeMillis())
                               log.debug("GO hb")
                               val entries = synchronized(this.pendingReplications) {
                                   HashSet(this.pendingReplications.entries)
                               }

                               entries.forEach { entry ->
                                   entry.value.let { p ->
                                       p.replicator.startReplication(p.group, this.sslContextFactory)
                                           .thenAccept { b ->
                                               if (b == true) {
                                                   synchronized(this.pendingReplications) {
                                                       this.pendingReplications.remove(entry.key)
                                                   }
                                               }
                                           }
                                   }
                               }
                           }).whenComplete { _, _ -> this.startObserver() }
    }

    private fun startHttpClient() {
        synchronized(this) {
            if (httpClient == null) {
                httpClient = HttpClient(this.sslContextFactory)

                try {
                    httpClient!!.maxConnectionsPerDestination = 65535
                    httpClient!!.maxRequestsQueuedPerDestination = 4096
                        httpClient!!.start()
                } catch (e: Exception) {
                    log.error("Cannot start HTTP client")
                    try {
                        httpClient!!.stop()
                    } catch (ignored: Exception) {
                        throw RuntimeException()
                    } finally {
                        httpClient = null
                    }
                    throw RuntimeException()
                }

            }
        }
    }

    private fun prepareDesignDocumentsAndStartReplications(group: Group): CompletableFuture<Boolean> {
        log.info("Starting replications for " + group.id)

        allDaos.forEach { d -> d.initStandardDesignDocument(group) }
        log.info("Standard docs initialised for " + group.id)

        var f = CompletableFuture.completedFuture<FilteredReplicator>(null)
        for (r in replicators) {
            f = f.thenCompose { previousReplicator ->
                queueReplicatorIfNeeded(group, previousReplicator)
                r.startReplication(group, this.sslContextFactory).thenApply<FilteredReplicator> { succeeded -> if (succeeded) null else r }
            }.exceptionally { t ->
                log.error("Cannot start replicator ", t)
                r
            }
        }
        return f.thenApply { lastReplicator ->
            queueReplicatorIfNeeded(group, lastReplicator)
            true
        }
    }

    private fun queueReplicatorIfNeeded(group: Group, previousReplicator: FilteredReplicator?) {
        try {
            if (previousReplicator != null) {
                synchronized(this.pendingReplications) {
                    log.info("Queuing pending replication for " + group.id)
                    this.pendingReplications.put(group.id, PendingReplication(group, previousReplicator))
                }
            }
        } catch (e: Exception) {
            log.error("Error while queuing replication", e)
        }

    }

    private inner class PendingReplication(var group: Group, var replicator: FilteredReplicator)

    companion object {
        private val log = LoggerFactory.getLogger(NewGroupObserver::class.java)
        val IS_SYNC_DEAD_TIMEOUT = 2 * 60 * 1000 // ms
    }

}
