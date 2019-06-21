//package org.taktik.icure.config
//
//import org.springframework.context.annotation.Configuration
//import org.springframework.web.reactive.config.ResourceHandlerRegistry
//import org.springframework.web.reactive.config.WebFluxConfigurer
//
//@Configuration // TODO SH this is probably useless
//class WebfluxConfig: WebFluxConfigurer {
//
//    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
//        registry.addResourceHandler("/swagger-ui.html**")
//                .addResourceLocations("classpath:/META-INF/resources/");
//
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }
//}
