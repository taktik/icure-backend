package org.taktik.icure.config

import com.google.gson.Gson
import com.hazelcast.core.HazelcastInstance
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.session.MapSession
import org.springframework.session.ReactiveMapSessionRepository
import org.springframework.session.ReactiveSessionRepository
import org.springframework.session.Session
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.WebSocketService
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import org.springframework.web.reactive.socket.server.upgrade.JettyRequestUpgradeStrategy
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.services.external.http.WebSocketHandler
import org.taktik.icure.services.external.rest.v1.wscontrollers.KmehrWsController


@Configuration
@EnableSpringWebSession
class SessionConfig {
    @Bean
    fun reactiveSessionRepository(hazelcastInstance: HazelcastInstance): ReactiveSessionRepository<MapSession> {
        val mapName = "sessionsMap"
        val config = hazelcastInstance.config // TODO SH later: configure TTL to purge expired sessions
        val map = hazelcastInstance.getMap<String, Session>(mapName)
        return ReactiveMapSessionRepository(map)
    }

    @Bean
    fun webSocketHandler(kmehrWsController: KmehrWsController, sessionLogic: AsyncSessionLogic) =
            WebSocketHandler(kmehrWsController, Gson(), sessionLogic)

    @Bean
    fun handlerMapping(webSocketHandler: WebSocketHandler) = SimpleUrlHandlerMapping().apply {
        urlMap = mapOf("/ws/*" to webSocketHandler)
        order = Ordered.HIGHEST_PRECEDENCE
    }

    @Bean
    fun handlerAdapter(webSocketService: WebSocketService) =
            WebSocketHandlerAdapter(webSocketService)

    @Bean
    fun webSocketService() = HandshakeWebSocketService(JettyRequestUpgradeStrategy())
}
