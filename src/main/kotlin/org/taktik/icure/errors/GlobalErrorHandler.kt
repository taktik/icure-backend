package org.taktik.icure.errors

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono
import java.io.IOException


@Configuration
@Order(-2)
class GlobalErrorHandler(private val objectMapper: ObjectMapper): ErrorWebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val bufferFactory = exchange.response.bufferFactory()

        val dataBuffer = try {
            when(ex){
                is ServerWebInputException -> {
                    val error = ex.reason?.let { HttpError(it) } ?: "Unknown error".toByteArray()
                    bufferFactory.wrap(objectMapper.writeValueAsBytes(error))
                }
                else -> {
                    val error = ex.message?.let { HttpError(it) } ?: "Unknown error".toByteArray()
                    bufferFactory.wrap(objectMapper.writeValueAsBytes(error))
                }
            }
        } catch (e: JsonProcessingException) {
            bufferFactory.wrap("".toByteArray())
        }

        exchange.response.headers.contentType = MediaType.APPLICATION_JSON

        exchange.response.statusCode = when(ex){
            is IOException -> HttpStatus.BAD_REQUEST
            is IllegalArgumentException -> HttpStatus.BAD_REQUEST
            is ServerWebInputException ->  HttpStatus.BAD_REQUEST
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        return exchange.response.writeWith(Mono.just(dataBuffer))
    }

    class HttpError internal constructor(val message: String)
}
