/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.config

import java.util.concurrent.ConcurrentHashMap
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.session.MapSession
import org.springframework.session.ReactiveMapSessionRepository
import org.springframework.session.ReactiveSessionRepository
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession
import org.springframework.web.server.session.CookieWebSessionIdResolver

@Configuration
@EnableSpringWebSession
class SessionConfig {
	@Bean
	fun reactiveSessionRepository(): ReactiveSessionRepository<MapSession> {
		return ReactiveMapSessionRepository(ConcurrentHashMap(4096))
	}

	@Bean
	fun webSessionIdResolver() = CookieWebSessionIdResolver().apply { addCookieInitializer { cb -> cb.sameSite("None").secure(true) } }
}
