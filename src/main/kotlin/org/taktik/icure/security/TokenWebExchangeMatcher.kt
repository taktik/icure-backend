package org.taktik.icure.security

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.web.server.ServerWebExchange
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import reactor.core.publisher.Mono
import kotlin.coroutines.CoroutineContext

class TokenWebExchangeMatcher(val asyncCacheManager: AsyncCacheManager) : ServerWebExchangeMatcher {
    val log: Logger = LoggerFactory.getLogger(javaClass)
    val cache = asyncCacheManager.getCache<String, SecurityToken>("spring.security.tokens")

    override fun matches(exchange: ServerWebExchange?): Mono<ServerWebExchangeMatcher.MatchResult> = mono {
        val path = exchange?.request?.path?.toString()
        if (path?.contains(';') == true) {
            val token = path.split(';')[1]
            token.let {
                if (it.contains('=')) {
                    val (key, value) = it.split('=')
                    if (key == "tokenid") {
                        cache.get(value)?.let { restriction ->
                            cache.evict(value)
                            if (
                                    exchange.request.method?.equals(restriction.method) == true &&
                                    path.startsWith(restriction.path)
                            ) {
                                val secContext =  SecurityContextImpl(restriction.authentication)
                                val securityContext = kotlin.coroutines.coroutineContext[ReactorContext]?.context?.put(SecurityContext::class.java, Mono.just(secContext))
                                return@mono withContext(kotlin.coroutines.coroutineContext.plus(securityContext?.asCoroutineContext() as CoroutineContext)) {
                                    exchange.session.awaitFirst().attributes["SPRING_SECURITY_CONTEXT"] = secContext
                                    ServerWebExchangeMatcher.MatchResult.match().awaitFirst()
                                }
                            }
                        }
                    }
                }
            }
        }
        ServerWebExchangeMatcher.MatchResult.notMatch().awaitFirst()
    }
}
