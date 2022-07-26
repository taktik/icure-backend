/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.config

import java.time.Duration
import com.fasterxml.jackson.databind.ObjectMapper
import io.icure.asyncjacksonhttpclient.net.web.WebClient
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.client.reactive.ReactorResourceFactory
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.taktik.couchdb.springramework.webclient.SpringWebfluxWebClient
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncMapCacheManager
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider

@Configuration
class CouchDbConfig(val couchDbProperties: CouchDbProperties) {
	val webClientWithTimeoutLogger: Log = LogFactory.getLog("org.taktik.icure.config.WebClientWithTimeout")
	val webClientLogger: Log = LogFactory.getLog("org.taktik.icure.config.WebClient")

	@Bean
	fun connectionProvider(): ConnectionProvider {
		return ConnectionProvider.builder("LARGE_POOL")
			.maxConnections(5000)
			.maxIdleTime(Duration.ofSeconds(120))
			.pendingAcquireMaxCount(-1).build()
	}

	@Bean
	fun httpClientWithTimeout(connectionProvider: ConnectionProvider) = SpringWebfluxWebClient(
		ReactorClientHttpConnector(HttpClient.create(connectionProvider).compress(true).responseTimeout(Duration.ofSeconds(2)))
	) { xff ->
		xff.add(
			ExchangeFilterFunction.ofRequestProcessor { req ->
				if (webClientWithTimeoutLogger.isDebugEnabled) {
					webClientWithTimeoutLogger.debug("-> ${req.method().name} ${req.url()}")
				}
				Mono.just(req)
			}
		)
	}

	@Bean
	fun httpClient(connectionProvider: ConnectionProvider) = SpringWebfluxWebClient(
		ReactorClientHttpConnector(HttpClient.create(connectionProvider).compress(true))
	) { xff ->
		xff.add(
			ExchangeFilterFunction.ofRequestProcessor { req ->
				if (webClientLogger.isDebugEnabled) {
					webClientLogger.debug("-> ${req.method().name} ${req.url()}")
				}
				Mono.just(req)
			}
		)
	}

	@Bean
	fun reactorClientResourceFactory(connectionProvider: ConnectionProvider) = ReactorResourceFactory().apply {
		isUseGlobalResources = false
		this.connectionProvider = connectionProvider
	}

	@Bean
	fun asyncCacheManager() = AsyncMapCacheManager()

	@Bean
	fun patientCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper): CouchDbDispatcher {
		return CouchDbDispatcher(httpClient, objectMapper, couchDbProperties.prefix, "patient", couchDbProperties.username!!, couchDbProperties.password!!, 1)
	}

	@Bean
	fun healthdataCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, couchDbProperties.prefix, "healthdata", couchDbProperties.username!!, couchDbProperties.password!!, 1)

	@Bean
	fun baseCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, couchDbProperties.prefix, "base", couchDbProperties.username!!, couchDbProperties.password!!, 1)

	@Bean
	fun configCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, "icure", "config", couchDbProperties.username!!, couchDbProperties.password!!, 1)

	@Bean
	fun drugCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, "icure", "drugs", couchDbProperties.username!!, couchDbProperties.password!!, 1)

	@Bean
	fun chapIVCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, "icure", "chapiv", couchDbProperties.username!!, couchDbProperties.password!!, 1)

    @Bean
    fun systemCouchDbDispatcher(httpClient: WebClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, "icure", "system", couchDbProperties.username!!, couchDbProperties.password!!, 1)
}
