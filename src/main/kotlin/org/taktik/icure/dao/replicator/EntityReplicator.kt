package org.taktik.icure.dao.replicator

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.ektorp.ViewQuery
import org.ektorp.http.URI
import org.ektorp.impl.NameConventions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.taktik.couchdb.Client
import org.taktik.couchdb.ClientImpl
import org.taktik.couchdb.queryView
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.base.StoredDocument
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@ExperimentalCoroutinesApi
@FlowPreview
abstract class EntityReplicator<T : StoredDocument>(private val sslContextFactory: SslContextFactory) : Replicator {
    private val log = LoggerFactory.getLogger(EntityReplicator::class.java)

    @Value("\${icure.couchdb.username}")
    protected var couchDbUsername: String? = null
    @Value("\${icure.couchdb.password}")
    protected var couchDbPassword: String? = null
    @Value("\${icure.couchdb.prefix}")
    protected var couchDbPrefix: String? = null
    @Value("\${icure.couchdb.url}")
    protected lateinit var couchDbUrl: String

    private data class SyncKey(val groupId:String, val id:String)
    protected data class IdAndRev(val id:String, val rev:String)

    private val syncStatus: ConcurrentMap<SyncKey, String> = ConcurrentHashMap()

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

    protected fun getAllIdsAndRevs(client:Client): Flow<IdAndRev> {
        val viewQuery = ViewQuery().designDocId(NameConventions.designDocName(entityType)).viewName("all").includeDocs(false)
        return client.queryView<String,String>(viewQuery).map {
            IdAndRev(it.id, checkNotNull(it.value))
        }
    }

    protected abstract suspend fun prepareReplication(client:Client, group: Group)

    protected abstract suspend fun replicate(client:Client, group: Group, entityIds: Flow<IdAndRev>): Flow<IdAndRev>


    private fun client(group: Group):Client {
        val groupDb = GroupDBUrl(couchDbUrl)
        val dbURI = URI.of(groupDb.getInstanceUrl(group)).append(groupDb.getDbName(group))
        return ClientImpl(httpClient, dbURI, group.id, group.password)
    }

    override suspend fun startReplication(group: Group): Job {
        val client = client(group)
        require(client.exists()) { "Cannot start replication : the group db doesnt exist for ${group.id}" }
        log.info("Db exists for ${group.id}")
        prepareReplication(client, group)
        doReplicate(client, group)
        return GlobalScope.launch {
            launch { observeChanges(client, group) }
            launch {
                while (true) {
                    doReplicate(client, group)
                    delay(60000)
                }
            }
        }
    }

    @FlowPreview
    private suspend fun observeChanges(client:Client, group: Group) {
        val changes = client.subscribeForChanges(entityType)
        // Replicate
        changes.collect { change ->
            log.debug("Detected new object : ${change.id} in group ${group.id}")
            val entityIds = listOf(IdAndRev(change.doc.id, change.doc.rev))
            replicate(client, group, entityIds.asFlow())
        }
    }

    private suspend fun doReplicate(client:Client, group: Group): Boolean {
        val startTime = System.currentTimeMillis()
        try {
            val allIds = this.getAllIdsAndRevs(client)
            val idsToSync = allIds.filter {
                val (id, rev) = it
                syncStatus[SyncKey(group.id, id)] != rev
            }
            log.debug("Replicating existing objects in group ${group.id}")
            val replicated = replicate(client, group, idsToSync).onEach {
                syncStatus[SyncKey(group.id, it.id)] = it.rev
            }.count()
            log.debug("Done replicating $replicated existing objects in group ${group.id}")
        } catch (e: Exception) {
            log.error("Exception during replication of group ${group.id} (${group.name})", e)
            return false
        }

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        log.info("Replication completed for group ${group.id} in $duration ms")
        return true
    }
}