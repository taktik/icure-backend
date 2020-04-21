package org.taktik.icure.config

import com.hazelcast.core.HazelcastInstance
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.session.MapSession
import org.springframework.session.ReactiveSessionRepository
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession
import org.taktik.icure.spring.ReactiveHazelcastSessionRepository


@Configuration
@EnableSpringWebSession
class SessionConfig {

    @Bean
    fun reactiveSessionRepository(@Qualifier("hazelcast") hazelcastInstance: HazelcastInstance): ReactiveSessionRepository<MapSession> {
        return ReactiveHazelcastSessionRepository(hazelcastInstance.getMap("spring:session:sessions"))
    }

}
