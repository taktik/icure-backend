package org.taktik.icure.security

import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.ReactorContext
import org.springframework.security.core.context.SecurityContext
import reactor.core.publisher.Mono

@ExperimentalCoroutinesApi
suspend fun loadSecurityContext(): Mono<SecurityContext>? {
	return coroutineContext[ReactorContext]?.context?.get<Mono<SecurityContext>>(SecurityContext::class.java)
}
