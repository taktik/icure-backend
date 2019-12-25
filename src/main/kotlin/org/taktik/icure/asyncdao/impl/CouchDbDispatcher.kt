package org.taktik.icure.asyncdao.impl

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.eclipse.jetty.client.HttpClient
import org.ektorp.http.URI
import org.taktik.couchdb.ClientImpl
import java.util.concurrent.TimeUnit

class CouchDbDispatcher(private val httpClient: HttpClient, private val prefix: String, private val dbFamily: String, private val username: String, private val password: String) {
    @ExperimentalCoroutinesApi
    private val connectors = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(240, TimeUnit.MINUTES)
            .build<CouchDbConnectorReference, ClientImpl>(object : CacheLoader<CouchDbConnectorReference, ClientImpl>() {
                @Throws(Exception::class)
                override fun load(key: CouchDbConnectorReference): ClientImpl {
                    return ClientImpl(httpClient, URI.of(key.dbInstanceUrl).append("$prefix-${key.groupId}-$dbFamily"), username, password)
                }
            })

    @ExperimentalCoroutinesApi
    fun getClient(dbInstanceUrl: java.net.URI, groupId: String? = null): ClientImpl {
        return connectors.get(CouchDbConnectorReference(dbInstanceUrl.toString(), groupId ?: "__"))
    }

    private data class CouchDbConnectorReference(internal val dbInstanceUrl: String, internal val groupId: String)
}
