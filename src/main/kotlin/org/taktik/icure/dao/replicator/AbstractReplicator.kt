package org.taktik.icure.dao.replicator

import com.google.common.collect.ImmutableList
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.taktik.couchdb.Change
import org.taktik.couchdb.CouchDbInstance
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.base.StoredDocument

import javax.annotation.PostConstruct
import java.net.URI
import java.util.concurrent.CompletableFuture

/**
 * @author Bernard Paulus - 14/03/2017
 */
abstract class AbstractReplicator<T : StoredDocument>(private val hazelcast: HazelcastInstance) : FilteredReplicator {
    private val log = LoggerFactory.getLogger(AbstractReplicator::class.java)

    var replicatorJobsStatusesByGroupId: IMap<String, ReplicatorJobStatus>? = null
        private set

    @Value("\${icure.couchdb.username}")
    protected var couchDbUsername: String? = null
    @Value("\${icure.couchdb.password}")
    protected var couchDbPassword: String? = null
    @Value("\${icure.couchdb.prefix}")
    protected var couchDbPrefix: String? = null
    @Value("\${icure.couchdb.url}")
    protected var couchDbUrl: String? = null

    protected var httpClient: HttpClient? = null

    protected abstract val entityType: Class<T>

    protected abstract fun getAllIds(groupId: String): List<String>

    protected abstract fun prepareReplication(group: Group)

    protected abstract fun replicate(group: Group, entityIds: List<String>) : List<String>

    @PostConstruct
    fun init() {
        replicatorJobsStatusesByGroupId = hazelcast.getMap(javaClass.canonicalName + ".replicatorJobsStatusesByGroupId")
    }

    override fun startReplication(replicatedGroup: Group, sslContextFactory: SslContextFactory): CompletableFuture<Boolean> {
        // should check if a replication hasn't been started or if group has been updated
        val groupDb = GroupDBUrl(couchDbUrl!!)
        val jobStatus = tryToGetJob(replicatedGroup)
        if (jobStatus == null) {
            log.info("Someone else is busy with " + replicatedGroup.id)
            return CompletableFuture.completedFuture(true) //It is a success because someone else is taking care of this replication... We do not need to mark it as pending
        }
        synchronized(this) {
            if (httpClient == null) {
                log.info("Init http client from " + replicatedGroup.id)
                httpClient = HttpClient(sslContextFactory)
                try {
                    httpClient!!.maxConnectionsPerDestination = 65535
                    httpClient!!.start()
                } catch (e: Exception) {
                    log.error("Cannot start HTTP client")
                    try {
                        httpClient!!.stop()
                    } catch (ignored: Exception) {
                        return CompletableFuture.completedFuture(false) //retry
                    } finally {
                        httpClient = null
                    }
                    return CompletableFuture.completedFuture(false) //retry
                }

            }
        }
        val groupDbInstance = CouchDbInstance(httpClient!!, URI.create(groupDb.getInstanceUrl(replicatedGroup)), groupDb.getDbName(replicatedGroup), replicatedGroup.id, replicatedGroup.password)
        log.info("Create instance for " + replicatedGroup.id)
        return groupDbInstance.exists().thenApply { result: Boolean ->
            log.info("Db exists for " + replicatedGroup.id)
            if (result) {
                prepareReplication(replicatedGroup)
                if (replicateExistingData(replicatedGroup)) {
                    observeChanges(replicatedGroup, jobStatus, groupDbInstance)
                    true
                } else false
            } else false
        }
    }

    private fun observeChanges(replicatedGroup: Group, jobStatus: ReplicatorJobStatus, groupDbInstance: CouchDbInstance) {
        groupDbInstance.changes(if (jobStatus.seq != null) jobStatus.seq else "now", { c: Change ->
            observe(c, replicatedGroup)
        }, {
            replicatorJobsStatusesByGroupId!![replicatedGroup.id] = replicatorJobsStatusesByGroupId!![replicatedGroup.id]?.timestamp(System.currentTimeMillis()) ?: ReplicatorJobStatus(System.currentTimeMillis())
        }).whenComplete { unit, throwable -> observeChanges(replicatedGroup, jobStatus, groupDbInstance) }
    }

    private fun replicateExistingData(group: Group): Boolean {
        val startTime = System.currentTimeMillis()
        try {
            val allIds = this.getAllIds(group.id)
            replicate(group, allIds)
        } catch (e: Exception) {
            log.error("Exception during initial replication : {} for group {} ({}) ", e.localizedMessage, group.id, group.name)
            return false
        }

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        log.info("Initial replication completed for {} in {} ms", group.id, duration)
        return true
    }

    fun observe(change: Change, group: Group) {
        if (change.doc == null) {
            return
        }
        if (entityType.canonicalName == change.doc!!["java_type"]) {
            val entityIds = ImmutableList.of(change.doc!!["_id"] as String)
            try {
                val ids = replicate(group, entityIds)
                replicatorJobsStatusesByGroupId!![group.id] = replicatorJobsStatusesByGroupId!![group.id]?.update(System.currentTimeMillis(), change.seq, ids) ?: ReplicatorJobStatus(System.currentTimeMillis())
            } catch (e: Exception) {
                log.error("replication failed: {}", e.localizedMessage)
                return
            }

            ReplicatorJobStatus(System.currentTimeMillis(), change.seq)
        }
    }

    private fun tryToGetJob(group: Group): ReplicatorJobStatus? {
        val jobStatus = ReplicatorJobStatus(System.currentTimeMillis(), null)
        val jobStatusInCluster = (replicatorJobsStatusesByGroupId as java.util.Map<String, ReplicatorJobStatus>).putIfAbsent(group.id, jobStatus)
        return if (jobStatusInCluster == null) {
            jobStatus
        } else {
            if (jobStatus.timestamp - jobStatusInCluster.timestamp <= NewGroupObserver.IS_SYNC_DEAD_TIMEOUT /* the current job is not dead */ || !(replicatorJobsStatusesByGroupId as java.util.Map<String, ReplicatorJobStatus>).replace(group.id, jobStatusInCluster, jobStatus))
            /* It has already been acquired in the meantime */ {
                null
            } else {
                jobStatus.seq(jobStatusInCluster.seq)
            }
        }
    }


}
