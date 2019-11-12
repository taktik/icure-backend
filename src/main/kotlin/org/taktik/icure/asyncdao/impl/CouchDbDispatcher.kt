package org.taktik.icure.asyncdao.impl

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.eclipse.jetty.client.HttpClient
import org.ektorp.CouchDbInstance
import org.ektorp.http.StdHttpClient
import org.ektorp.http.URI
import org.ektorp.impl.StdCouchDbInstance
import org.taktik.couchdb.Client
import org.taktik.couchdb.ClientImpl
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.dao.impl.ektorp.StdCouchDbICureConnector
import java.util.*
import java.util.concurrent.TimeUnit

class CouchDbDispatcher(private val httpClient: HttpClient, private val prefix: String, private val dbFamily: String, private val username: String, private val password: String) {
    private val connectors = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(240, TimeUnit.MINUTES)
            .build<CouchDbConnectorReference, ClientImpl>(object : CacheLoader<CouchDbConnectorReference, ClientImpl>() {
                @Throws(Exception::class)
                override fun load(key: CouchDbConnectorReference): ClientImpl {
                    return ClientImpl(httpClient, URI.of(key.dbInstanceUrl).append("$prefix-${key.groupId}-$dbFamily"), username, password)
                }
            })

    fun getClient(dbInstanceUrl: java.net.URI, groupId: String): ClientImpl {
        return connectors.get(CouchDbConnectorReference(dbInstanceUrl.toString(), groupId))
    }

    private data class CouchDbConnectorReference(internal val dbInstanceUrl: String, internal val groupId: String)
}
