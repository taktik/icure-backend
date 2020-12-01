package org.taktik.icure.asyncdao.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.http.client.utils.URIBuilder
import org.springframework.web.reactive.function.client.WebClient
import org.taktik.couchdb.ClientImpl
import java.net.URI
import java.util.concurrent.TimeUnit

class CouchDbDispatcher(
        private val httpClient: WebClient,
        private val objectMapper: ObjectMapper,
        private val prefix: String,
        private val dbFamily: String,
        private val username: String,
        private val password: String
) {
    @ExperimentalCoroutinesApi
    private val connectors = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(240, TimeUnit.MINUTES)
            .build<CouchDbConnectorReference, ClientImpl>(object : CacheLoader<CouchDbConnectorReference, ClientImpl>() {
                @Throws(Exception::class)
                override fun load(key: CouchDbConnectorReference): ClientImpl {
                    return ClientImpl(httpClient, URIBuilder(key.dbInstanceUrl).setPath("$prefix-$dbFamily").build(), username, password, objectMapper)
                }
            })

    @ExperimentalCoroutinesApi
    fun getClient(dbInstanceUrl: URI): ClientImpl {
        return connectors.get(CouchDbConnectorReference(dbInstanceUrl.toString()))
    }

    private data class CouchDbConnectorReference(internal val dbInstanceUrl: String)
}
