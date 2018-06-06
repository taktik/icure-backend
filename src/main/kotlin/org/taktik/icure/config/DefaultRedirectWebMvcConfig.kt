package org.taktik.icure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@Configuration
class DefaultRedirectWebMvcConfig {
    @Bean
    fun forwardToIndex(): WebMvcConfigurerAdapter {
        return object : WebMvcConfigurerAdapter() {
            override fun addViewControllers(registry: ViewControllerRegistry) {
                // forward requests to /admin and /user to their index.html
                registry.addRedirectViewController("/ht", "/ht/")
                registry.addViewController("/ht/").setViewName("forward:/ht/index.html")
            }
        }
    }
}
