package org.taktik.icure.config

import com.google.gson.Gson
import com.hazelcast.core.HazelcastInstance
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.session.MapSession
import org.springframework.session.ReactiveSessionRepository
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.WebSocketService
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import org.springframework.web.reactive.socket.server.upgrade.JettyRequestUpgradeStrategy
import org.springframework.web.server.session.CookieWebSessionIdResolver
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.services.external.http.WebSocketOperationHandler
import org.taktik.icure.services.external.rest.v1.wscontrollers.KmehrWsController
import org.taktik.icure.spring.ReactiveHazelcastSessionRepository


@Configuration
@EnableSpringWebSession
class SessionConfig {

    @Bean
    fun reactiveSessionRepository(@Qualifier("hazelcast") hazelcastInstance: HazelcastInstance): ReactiveSessionRepository<MapSession> {
        return ReactiveHazelcastSessionRepository(hazelcastInstance.getMap("spring:session:sessions"))
    }

    @Bean
    fun webSocketHandler(kmehrWsController: KmehrWsController, sessionLogic: AsyncSessionLogic) =
            WebSocketOperationHandler(kmehrWsController, Gson(), sessionLogic)

    @Bean
    fun handlerMapping(webSocketHandler: WebSocketOperationHandler) = SimpleUrlHandlerMapping().apply {
        urlMap = mapOf("/ws/*" to webSocketHandler)
        order = Ordered.HIGHEST_PRECEDENCE
    }

    @Bean
    fun handlerAdapter(webSocketService: WebSocketService) =
            WebSocketHandlerAdapter(webSocketService)

    @Bean
    fun webSocketService() = HandshakeWebSocketService(JettyRequestUpgradeStrategy())

    @Bean
    fun webSessionIdResolver() = CookieWebSessionIdResolver().apply { addCookieInitializer {cb -> cb.sameSite("None")} }
}
