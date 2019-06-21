package org.taktik.icure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean



@Configuration
class CrossOriginConfiguration { // TODO SH migrate this to WebFlux see https://www.baeldung.com/spring-webflux-cors
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("HEAD")
        config.addAllowedMethod("GET")
        config.addAllowedMethod("PUT")
        config.addAllowedMethod("POST")
        config.addAllowedMethod("DELETE")
        config.addAllowedMethod("PATCH")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

    @Bean
    fun registerCorsFilter(filter: CorsFilter): FilterRegistrationBean<CorsFilter> { // TODO SH why did i need .cors()? this doesn't work?
        val reg = FilterRegistrationBean(filter)
        reg.order = 4
        return reg
    }
}