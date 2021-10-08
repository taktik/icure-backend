/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.security

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.lang.Void

class Http401UnauthorizedEntryPoint : ServerAuthenticationEntryPoint {
    override fun commence(exchange: ServerWebExchange, e: AuthenticationException): Mono<Void> {
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        return Mono.empty()
    }
}
