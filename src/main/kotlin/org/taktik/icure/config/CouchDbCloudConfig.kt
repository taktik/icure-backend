/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.client.reactive.ReactorResourceFactory
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncMapCacheManager
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

@Configuration
class CouchDbCloudConfig(val couchDbProperties: CouchDbProperties) {
    @Value("\${icure.couchdb.prefix}")
    private val couchDbPrefix: String? = null

    @Bean
    fun connectionProvider(): ConnectionProvider {
        return ConnectionProvider.builder("LARGE_POOL")
                .maxConnections(5000)
                .maxIdleTime(Duration.ofSeconds(120))
                .pendingAcquireMaxCount(-1).build()
    }

    @Bean
    fun httpClient(connectionProvider: ConnectionProvider) = WebClient.builder().clientConnector(
            ReactorClientHttpConnector(HttpClient.create(connectionProvider).compress(true))
    ).filters { xff ->
        val log = LogFactory.getLog("org.taktik.icure.config.WebClient")
        xff.add(ExchangeFilterFunction.ofRequestProcessor { req ->
            if (log.isDebugEnabled) {
                log.debug("-> ${req.method().name} ${req.url()}")
            }
            Mono.just(req)
        })
    }.build()

    @Bean
    fun reactorClientResourceFactory(connectionProvider: ConnectionProvider) = ReactorResourceFactory().apply {
        isUseGlobalResources = false
        this.connectionProvider = connectionProvider
    }

    @Bean
    fun asyncCacheManager() = AsyncMapCacheManager()
    @Bean
    fun patientCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, "icure", "patient", couchDbProperties.username!!, couchDbProperties.password!!)
    @Bean
    fun healthdataCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, "icure", "healthdata", couchDbProperties.username!!, couchDbProperties.password!!)
    @Bean
    fun baseCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, "icure", "base", couchDbProperties.username!!, couchDbProperties.password!!)
    @Bean
    fun configCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, "icure", "config", couchDbProperties.username!!, couchDbProperties.password!!)
    @Bean
    fun drugCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, "icure", "drugs", couchDbProperties.username!!, couchDbProperties.password!!)
}
