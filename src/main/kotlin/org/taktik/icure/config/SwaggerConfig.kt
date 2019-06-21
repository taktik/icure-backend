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
    fun api(): Docket {
        /*val securityReference = SecurityReference.builder()
                .reference("basicAuth")
                .scopes(arrayOf<AuthorizationScope>())
                .build()

        val securityContexts = listOf(SecurityContext.builder().securityReferences(listOf(securityReference)).build())

        val auth = listOf(BasicAuth("basicAuth"))*/
        return Docket(DocumentationType.SWAGGER_2)
                //.securitySchemes(auth)
                //.securityContexts(securityContexts)
                /*.pathProvider(object: AbstractPathProvider() {
                    override fun applicationPath() = "/rest/v1" // TODO SH map controllers to /rest/v1 and then uncomment this
                    override fun getDocumentationPath() = "/" // see https://github.com/springfox/springfox/issues/1443 and https://github.com/springfox/springfox/issues/2817 https://stackoverflow.com/questions/38212691/how-to-change-base-url-only-for-rest-controllers/38228080 server.servlet.context-path = /baseApiName
                })*/
                .select().apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build()
    }
}