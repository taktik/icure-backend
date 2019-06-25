package org.taktik.icure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.BasicAuth
import springfox.documentation.service.SecurityReference
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux


@Configuration
@EnableSwagger2WebFlux
class SwaggerConfig {
    @Bean
    fun api(): Docket { // TODO SH customize Swagger if needed and then compare jsons with previous version to make sure API is backward-compatible
        val securityReference = SecurityReference.builder()
                .reference("basicAuth")
                .scopes(arrayOf<AuthorizationScope>())
                .build()

        val securityContexts = listOf(SecurityContext.builder().securityReferences(listOf(securityReference)).build())

        val auth = listOf(BasicAuth("basicAuth"))
        return Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(auth)
                .securityContexts(securityContexts)
                .select().apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build()
    }
}