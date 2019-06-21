package org.taktik.icure.config

import com.hazelcast.core.HazelcastInstance
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.session.MapSession
import org.springframework.session.ReactiveMapSessionRepository
import org.springframework.session.ReactiveSessionRepository
import org.springframework.session.Session
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession


@Configuration
@EnableSpringWebSession
class SessionConfig {
    @Bean
    fun reactiveSessionRepository(hazelcastInstance: HazelcastInstance): ReactiveSessionRepository<MapSession> {
        val mapName = "sessionsMap"
        val config = hazelcastInstance.config // TODO SH configure TTL to purge expired sessions
        val map = hazelcastInstance.getMap<String, Session>(mapName)
        return ReactiveMapSessionRepository(map)
    }
}
