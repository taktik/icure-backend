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

package org.taktik.icure.services.external.rest.v1.controllers.support

import java.util.UUID
import kotlin.coroutines.CoroutineContext
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.springframework.http.HttpMethod
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.WebSession
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.security.SecurityToken
import org.taktik.icure.services.external.rest.v1.dto.AuthenticationResponse
import org.taktik.icure.services.external.rest.v1.dto.LoginCredentials
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import reactor.core.publisher.Mono

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/auth")
@Tag(name = "auth")
class LoginController(
	private val sessionLogic: AsyncSessionLogic,
	asyncCacheManager: AsyncCacheManager
) {
	val cache = asyncCacheManager.getCache<String, SecurityToken>("spring.security.tokens")

	@Operation(summary = "login", description = "Login using username and password")
	@PostMapping("/login")
	fun login(request: ServerHttpRequest, @RequestBody loginCredentials: LoginCredentials, @Parameter(hidden = true) session: WebSession) = mono {
		val response = AuthenticationResponse()
		val authentication = sessionLogic.login(loginCredentials.username!!, loginCredentials.password!!, request, session)
		response.successful = authentication != null && authentication.isAuthenticated
		if (response.successful) {
			val secContext = SecurityContextImpl(authentication)
			val securityContext = kotlin.coroutines.coroutineContext[ReactorContext]?.context?.put(SecurityContext::class.java, Mono.just(secContext))
			withContext(kotlin.coroutines.coroutineContext.plus(securityContext?.asCoroutineContext() as CoroutineContext)) {
				response.healthcarePartyId = sessionLogic.getCurrentHealthcarePartyId()
				response.username = loginCredentials.username
				session.attributes["SPRING_SECURITY_CONTEXT"] = secContext
			}
		}
		response
	}

	@Operation(summary = "logout", description = "Logout")
	@GetMapping("/logout")
	fun logout() = mono {
		sessionLogic.logout()
		AuthenticationResponse(successful = true)
	}

	@Operation(summary = "logout", description = "Logout")
	@PostMapping("/logout")
	fun logoutPost() = mono {
		sessionLogic.logout()
		AuthenticationResponse(successful = true)
	}

	@Operation(summary = "token", description = "Get token for subsequent operation")
	@GetMapping("/token/{method}/{path}")
	fun token(@PathVariable method: String, @PathVariable path: String) = mono {
		val token = UUID.randomUUID().toString()
		cache.put(token, SecurityToken(HttpMethod.valueOf(method), path, sessionLogic.getCurrentSessionContext().getAuthentication()))
		token
	}
}
