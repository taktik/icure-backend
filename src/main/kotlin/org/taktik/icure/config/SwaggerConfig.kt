package org.taktik.icure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.server.WebSession
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.BasicAuth
import springfox.documentation.service.SecurityReference
import springfox.documentation.spi.DocumentationType
import springfox.documentation.service.Tag
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux

@Configuration
@EnableSwagger2WebFlux
class SwaggerConfig {
    @Bean
    fun api(): Docket {
        val securityReference = SecurityReference.builder()
                .reference("basicAuth")
                .scopes(arrayOf<AuthorizationScope>())
                .build()

        val securityContexts = listOf(SecurityContext.builder().securityReferences(listOf(securityReference)).build())

        val auth = listOf(BasicAuth("basicAuth"))
        return Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(auth)
                .securityContexts(securityContexts)
                .consumes(setOf(MediaType.APPLICATION_JSON_VALUE))
                .produces(setOf(MediaType.APPLICATION_JSON_VALUE))
                .ignoredParameterTypes(ServerHttpRequest::class.java, WebSession::class.java)
                .tags(
                        Tag("accesslog", "Access logs base API"),
                        Tag("code", "Codes CRUD and advanced API"),
                        Tag("contact", "Contacts CRUD and advanced API"),
                        Tag("document", "Documents CRUD and advanced API"),
                        Tag("entitytemplate", "Entity templates CRUD and advanced API"),
                        Tag("doctemplate", "Entity templates CRUD and advanced API"),
                        Tag("filter", "Entity templates CRUD and advanced API"),
                        Tag("form", "Forms CRUD and advanced API"),
                        Tag("generic", "iCure generic actions API"),
                        Tag("group", "Practice groups API"),
                        Tag("hcparty", "Healthcare parties CRUD and advanced API"),
                        Tag("helement", "Health elements CRUD and advanced API"),
                        Tag("icure", "iCure application basic API"),
                        Tag("insurance", "Insurances CRUD and advanced API"),
                        Tag("invoice", "Invoices CRUD and advanced API"),
                        Tag("auth", "Authentification API"),
                        Tag("message", "Messages CRUD and advanced API"),
                        Tag("patient", "Patients CRUD and advanced API"),
                        Tag("replication", "Replication API"),
                        Tag("tarification", "Tarifications CRUD and advanced API"),
                        Tag("technicaladmin", "Technical internal API"),
                        Tag("user", "Users CRUD and advanced API"),
                        Tag("be_drugs", "API for belgian Drugs service"),
                        Tag("be_mikrono", "API for belgian Mikrono service"),
                        Tag("be_progenda", "API for belgian Progenda service"),
                        Tag("be_kmehr", "API for belgian Kmehr service"),
                        Tag("be_result_import", "API for belgian Result_import service"),
                        Tag("be_result_export", "API for belgian Result_export service")
                )
                .select().apis(RequestHandlerSelectors.basePackage("org.taktik.icure.services.external.rest.v1"))
                .paths(PathSelectors.none()).build()
    }
}
