package org.taktik.icure.services.external.http

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@Component
class RequestTimingFilter : WebFilter {
    private val log = LoggerFactory.getLogger(javaClass.name)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val startMillis = System.currentTimeMillis()
        return chain.filter(exchange)
                .doOnSuccess { log.info("Elapsed Time: {} {}ms", exchange.request.path, System.currentTimeMillis() - startMillis) }
    }
}
