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

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.netty.channel.ChannelOption
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.WebSocketService
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.services.external.http.WebSocketOperationHandler
import org.taktik.icure.services.external.rest.v1.wscontrollers.KmehrWsController
import reactor.netty.http.server.WebsocketServerSpec

@Configuration
@EnableWebFlux
class WebConfig : WebFluxConfigurer {
	private final val CLASSPATH_RESOURCE_LOCATIONS = arrayOf("classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/")
	override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
		registry.addResourceHandler("/**")
			.addResourceLocations(*CLASSPATH_RESOURCE_LOCATIONS)
	}

	override fun addCorsMappings(registry: CorsRegistry) {
		registry.addMapping("/**").allowCredentials(true).allowedOriginPatterns("http://*", "https://*").allowedMethods("*").allowedHeaders("*")
	}

	override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
		configurer.defaultCodecs().maxInMemorySize(128 * 1024 * 1024)

		configurer.defaultCodecs().jackson2JsonEncoder(
			Jackson2JsonEncoder(
				ObjectMapper().registerModule(
					KotlinModule.Builder()
						.reflectionCacheSize(512)
						.nullIsSameAsDefault(true)
						.build()
				).apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }
			)
		)
		configurer.defaultCodecs().jackson2JsonDecoder(
			Jackson2JsonDecoder(
				ObjectMapper().registerModule(
					KotlinModule.Builder()
						.reflectionCacheSize(512)
						.nullIsSameAsDefault(true)
						.build()
				)
			).apply { maxInMemorySize = 128 * 1024 * 1024 }
		)
	}

	@Bean
	fun objectMapper(): ObjectMapper = ObjectMapper().registerModule(
		KotlinModule.Builder()
			.reflectionCacheSize(512)
			.nullIsSameAsDefault(true)
			.nullToEmptyCollection(true)
			.nullToEmptyMap(true)
			.build()
	).apply {
		setSerializationInclusion(JsonInclude.Include.NON_NULL)
	}

	@Bean
	fun webSocketHandler(kmehrWsController: KmehrWsController, sessionLogic: AsyncSessionLogic, objectMapper: ObjectMapper) =
		WebSocketOperationHandler(kmehrWsController, objectMapper, sessionLogic)

	@Bean
	fun handlerMapping(webSocketHandler: WebSocketOperationHandler) = SimpleUrlHandlerMapping().apply {
		urlMap = mapOf("/ws/**" to webSocketHandler)
		order = Ordered.HIGHEST_PRECEDENCE
	}

	@Bean
	fun handlerAdapter(webSocketService: WebSocketService) =
		WebSocketHandlerAdapter(webSocketService)

	@Bean
	fun webSocketService() = HandshakeWebSocketService(ReactorNettyRequestUpgradeStrategy(WebsocketServerSpec.builder().maxFramePayloadLength(64 * 1024 * 1024)))

	@Bean
	fun reactiveWebServerFactory(): ReactiveWebServerFactory? {
		val factory = NettyReactiveWebServerFactory()
		factory.addServerCustomizers(nettyCustomizer())
		return factory
	}

	fun nettyCustomizer() = NettyServerCustomizer { httpServer ->
		httpServer.option(ChannelOption.SO_BACKLOG, 2048)
	}
}
