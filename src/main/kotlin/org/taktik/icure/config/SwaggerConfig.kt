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

package org.taktik.icure.config

import java.util.Map
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.GroupedOpenApi
import org.springdoc.core.SpringDocUtils
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod

@Configuration
class SwaggerConfig {
	companion object {
		init {
			SpringDocUtils.getConfig().removeRequestWrapperToIgnore(Map::class.java)
		}
	}

	@Bean
	fun iCureOpenAPI(): OpenAPI? {
		return OpenAPI()
			.components(
				Components()
					.addSecuritySchemes(
						"basicScheme",
						SecurityScheme()
							.type(SecurityScheme.Type.HTTP).scheme("basic")
					)
			)
			.addSecurityItem(SecurityRequirement().addList("basicScheme"))
	}

	@Bean
	fun iCureOpenAPIV1(): GroupedOpenApi? = groupedOpenApiBuilder(
		description = "The iCure Data Stack Application API is the native interface to iCure. This version is obsolete, please use v2.",
		version = "v1",
		paths = arrayOf("/rest/v1/**"),
		packages = arrayOf("org.taktik.icure.services.external.rest.v1"),
		packagesToExclude = arrayOf(
			"org.taktik.icure.services.external.rest.v1.error",
			"org.taktik.icure.services.external.rest.v1.mapper",
			"org.taktik.icure.services.external.rest.v1.utils",
			"org.taktik.icure.services.external.rest.v1.wscontrollers"
		)
	)

	@Bean
	fun iCureOpenAPIV2(): GroupedOpenApi? = groupedOpenApiBuilder(
		description = "The iCure Data Stack Application API is the native interface to iCure.",
		version = "v2",
		paths = arrayOf("/rest/v2/**"),
		packages = arrayOf("org.taktik.icure.services.external.rest.v2"),
		packagesToExclude = arrayOf(
			"org.taktik.icure.services.external.rest.v2.mapper",
			"org.taktik.icure.services.external.rest.v2.utils",
			"org.taktik.icure.services.external.rest.v2.wscontrollers"
		)
	)

	private fun groupedOpenApiBuilder(
		description: String,
		version: String,
		paths: Array<String>,
		packages: Array<String>,
		packagesToExclude: Array<String>
	): GroupedOpenApi? = GroupedOpenApi.builder()
		.group(version)
		.pathsToMatch(*paths)
		.packagesToScan(*packages)
		.packagesToExclude(*packagesToExclude)
		.addOpenApiCustomiser { openApi ->
			openApi
				.info(
					Info()
						.title("iCure Data Stack API Documentation")
						.description(description)
						.version(version)
				)
		}
		.build()

	@Bean
	fun springOperationCustomizer() = object : OperationCustomizer {
		override fun customize(operation: Operation, handlerMethod: HandlerMethod) = operation.also {
			try {
				if (it.parameters != null) {
					it.parameters = it.parameters.sortedWith(
						compareBy { p ->
							handlerMethod.methodParameters.indexOfFirst { mp -> mp.parameter.name == p.name }
						}
					)
				}
			} catch (e: IllegalStateException) {
			}
		}
	}
}
