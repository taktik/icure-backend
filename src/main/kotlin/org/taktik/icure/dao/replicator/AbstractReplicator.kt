package org.taktik.icure.dao.replicator

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.ektorp.http.URI
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.taktik.couchdb.Client
import org.taktik.couchdb.ClientImpl
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.base.StoredDocument

abstract class AbstractReplicator<T : StoredDocument>(private val sslContextFactory: SslContextFactory) : Replicator {
    private val log = LoggerFactory.getLogger(AbstractReplicator::class.java)

    @Value("\${icure.couchdb.username}")
    protected var couchDbUsername: String? = null
    @Value("\${icure.couchdb.password}")
    protected var couchDbPassword: String? = null
    @Value("\${icure.couchdb.prefix}")
    protected var couchDbPrefix: String? = null
    @Value("\${icure.couchdb.url}")
    protected lateinit var couchDbUrl: String

    protected val httpClient: HttpClient by lazy {
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

    protected abstract val entityType: Class<T>

    protected abstract suspend fun getAllIds(groupId: String, dbInstanceUrl: String?): List<String>

    protected abstract suspend fun prepareReplication(group: Group)

    protected abstract suspend fun replicate(group: Group, entityIds: List<String>): List<String>

    @FlowPreview
    override suspend fun startReplication(group: Group): Job {
        // should check if a replication hasn't been started or if group has been updated
        val groupDb = GroupDBUrl(couchDbUrl)
        val dbURI = URI.of(groupDb.getInstanceUrl(group)).append(groupDb.getDbName(group))
        val client = ClientImpl(httpClient, dbURI, group.id, group.password)

        require(client.exists()) { "Cannot start replication : the group db doesnt exist for ${group.id}" }
        log.info("Db exists for ${group.id}")
        prepareReplication(group)
        replicateExistingData(group)
        return GlobalScope.launch { observeChanges(group, client) }
    }

    @FlowPreview
    private suspend fun observeChanges(group: Group, dbClient: Client) {
        val changes = dbClient.subscribeForChanges(entityType)
        // Replicate
        changes.collect { change ->
            val entityIds = listOf(change.doc.id as String)
            replicate(group, entityIds)
        }
    }

    private suspend fun replicateExistingData(group: Group): Boolean {
        val startTime = System.currentTimeMillis()
        try {
            val allIds = this.getAllIds(group.id, group.dbInstanceUrl())
            replicate(group, allIds)
        } catch (e: Exception) {
            log.error("Exception during initial replication : ${e.localizedMessage} for group ${group.id} (${group.name})", e)
            return false
        }

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        log.info("Initial replication completed for {} in {} ms", group.id, duration)
        return true
    }
}