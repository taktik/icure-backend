package org.taktik.icure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean



@Configuration
class CrossOriginConfiguration {
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
    fun registerCorsFilter(filter: CorsFilter): FilterRegistrationBean {
        val reg = FilterRegistrationBean(filter)
        reg.order = 4
        return reg
    }
}