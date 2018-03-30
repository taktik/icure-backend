/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.error

import ma.glasnost.orika.MapperFacade
import org.slf4j.LoggerFactory
import org.taktik.icure.be.ehealth.logic.error.ErrorCode
import org.taktik.icure.be.ehealth.logic.error.WsException
import org.taktik.icure.services.external.rest.v1.dto.WsExceptionDto
import java.util.*
import javax.ws.rs.core.Context
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider
import javax.ws.rs.core.Response

@Provider
class WsExceptionMapper : ExceptionMapper<WsException> {
	private val logger = LoggerFactory.getLogger(WsExceptionMapper::class.java)
	private var mapper: MapperFacade? = null

	override fun toResponse(exception: WsException): Response {
        logger.debug("mapping ${exception.error}", exception)
        return Response.status(codeToHttpStatus(exception.error))
			.entity(WsExceptionDto(
				exception.error.level.name,
				exception.error.name,
				translate(exception.error, exception.arguments)
			)).type("application/json").build()
    }

	@Context
	fun setMapper(mapper: MapperFacade) {
		this.mapper = mapper
	}

	private fun codeToHttpStatus(error : ErrorCode) : Response.Status {
		return when (error) {
			ErrorCode.GENERIC_ERROR -> Response.Status.INTERNAL_SERVER_ERROR
			else -> throw UnsupportedOperationException("unmapped exception code $error")
		}
	}



	private fun translate(error: ErrorCode, arguments: List<Any>) : Map<String, String> {
		return SUPPORTED_LOCALES.associate { locale ->
			val translation = String(ResourceBundle.getBundle("org.taktik.icure.services.external.rest.v1.error.wsErrors", locale)
				.getString(error.name).toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8) // workaround to use UTF-8 in java properties files https://stackoverflow.com/questions/4659929/how-to-use-utf-8-in-resource-properties-with-resourcebundle#4660195
			if (arguments.isEmpty()) {
				Pair(locale.toString(), translation)
			} else {
				val expectedNumberOfArguments = translation.replace("%%", "").count { it == '%' }
				require(expectedNumberOfArguments == arguments.size, { "expected $expectedNumberOfArguments args for format string '$translation'. Got ${arguments.size}: $arguments"})
				Pair(
					locale.toString(),
					translation.format(locale, *arguments.toTypedArray())
				)
			}
		}
	}

	companion object {
		@JvmStatic
		val SUPPORTED_LOCALES = listOf(Locale.ENGLISH, Locale.FRENCH, Locale("nl"))
	}
}
