package org.taktik.icure.errors

import java.io.IOException
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import org.taktik.couchdb.exception.CouchDbConflictException
import org.taktik.icure.exceptions.MissingRequirementsException
import reactor.core.publisher.Mono

@Configuration
class GlobalErrorHandler(private val objectMapper: ObjectMapper) : ErrorWebExceptionHandler {
	override fun handle(exchange: ServerWebExchange, ex: Throwable) = exchange.response.let { r ->

		val bufferFactory = r.bufferFactory()

		r.headers.contentType = MediaType.APPLICATION_JSON
		r.writeWith(
			Mono.just(
				when (ex) {
					is IOException -> bufferFactory.toBuffer(ex.message).also { r.statusCode = HttpStatus.BAD_REQUEST }
					is IllegalArgumentException -> bufferFactory.toBuffer(ex.message).also { r.statusCode = HttpStatus.BAD_REQUEST }
					is CouchDbConflictException -> bufferFactory.toBuffer(ex.message).also { r.statusCode = HttpStatus.CONFLICT }
					is MissingRequirementsException -> bufferFactory.toBuffer(ex.message).also { r.statusCode = HttpStatus.BAD_REQUEST }
					is ServerWebInputException -> bufferFactory.toBuffer(ex.reason).also { r.statusCode = HttpStatus.BAD_REQUEST }
					else -> bufferFactory.toBuffer(ex.message).also { r.statusCode = HttpStatus.INTERNAL_SERVER_ERROR }
				}
			)
		)
	}

	private fun DataBufferFactory.toBuffer(info: String?) = try {
		val error = info?.let { HttpError(it) } ?: "Unknown error".toByteArray()
		this.wrap(objectMapper.writeValueAsBytes(error))
	} catch (e: JsonProcessingException) {
		this.wrap("".toByteArray())
	}

	class HttpError internal constructor(val message: String)
}
