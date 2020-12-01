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
