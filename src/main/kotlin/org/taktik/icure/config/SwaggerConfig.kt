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

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod


@Configuration
class SwaggerConfig {
    @Bean
    fun springOpenAPI(): OpenAPI? {
        return OpenAPI()
                .components(Components()
                        .addSecuritySchemes("basicScheme", SecurityScheme()
                                .type(SecurityScheme.Type.HTTP).scheme("basic")))
                .info(Info().title("iCure Cloud API Documentation")
                .description("Spring shop sample application")
                .version("v0.0.1"))
                .addSecurityItem(SecurityRequirement().addList("basicScheme"))
    }

    @Bean
    fun springOperationCustomizer() = object : OperationCustomizer {
        override fun customize(operation: Operation, handlerMethod: HandlerMethod) = operation.also {
            try {
                if (it.parameters != null) {
                    it.parameters = it.parameters.sortedWith(compareBy { p ->
                        handlerMethod.methodParameters.indexOfFirst { mp -> mp.parameter.name == p.name }
                    })
                }
            } catch(e:IllegalStateException) {}
        }
    }
}
