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

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
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
import org.springframework.web.server.session.CookieWebSessionIdResolver
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.services.external.http.WebSocketOperationHandler
import org.taktik.icure.services.external.rest.v1.wscontrollers.KmehrWsController


@Configuration
@EnableWebFlux
class WebConfig : WebFluxConfigurer {
	private final val CLASSPATH_RESOURCE_LOCATIONS = arrayOf("classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/")
	override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
		registry.addResourceHandler("/**")
				.addResourceLocations(*CLASSPATH_RESOURCE_LOCATIONS)
	}

	override fun addCorsMappings(registry: CorsRegistry) {
		registry.addMapping("/**").allowCredentials(true).allowedOrigins("*").allowedMethods("*").allowedHeaders("*")
	}

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(ObjectMapper().apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }))
        configurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder().apply { maxInMemorySize = 8*1024*1024 })
    }

    @Bean
    fun webSocketHandler(kmehrWsController: KmehrWsController, sessionLogic: AsyncSessionLogic) =
            WebSocketOperationHandler(kmehrWsController, Gson(), sessionLogic)

    @Bean
    fun handlerMapping(webSocketHandler: WebSocketOperationHandler) = SimpleUrlHandlerMapping().apply {
        urlMap = mapOf("/ws/**" to webSocketHandler)
        order = Ordered.HIGHEST_PRECEDENCE
    }

    @Bean
    fun handlerAdapter(webSocketService: WebSocketService) =
            WebSocketHandlerAdapter(webSocketService)

    @Bean
    fun webSocketService() = HandshakeWebSocketService(ReactorNettyRequestUpgradeStrategy().apply { maxFramePayloadLength = 8*1024*1024 })

    @Bean
    fun webSessionIdResolver() = CookieWebSessionIdResolver().apply { addCookieInitializer { cb -> cb.sameSite("None").secure(true) } }
}
