/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.ReactiveAuthenticationManagerAdapter
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.firewall.StrictHttpFirewall
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.taktik.icure.logic.GroupLogic
import org.taktik.icure.logic.ICureSessionLogic
import org.taktik.icure.logic.PermissionLogic
import org.taktik.icure.logic.UserLogic
import org.taktik.icure.security.database.CustomAuthenticationProvider
import org.taktik.icure.security.database.ShaAndVerificationCodePasswordEncoder


@Configuration
class SecurityConfig {
    @Bean
    fun passwordEncoder() = ShaAndVerificationCodePasswordEncoder("SHA-256")

    @Bean
    fun httpFirewall() = StrictHttpFirewall().apply { setAllowSemicolon(true) } // TODO SH might be ignored if not registered in the security config

    @Bean
    fun daoAuthenticationProvider(userLogic: UserLogic, groupLogic: GroupLogic, permissionLogic: PermissionLogic, passwordEncoder: PasswordEncoder) = CustomAuthenticationProvider(userLogic, groupLogic, permissionLogic).apply {
        setPasswordEncoder(passwordEncoder)
    }
}

@Configuration
@EnableWebFluxSecurity
class SecurityConfigAdapter(private val daoAuthenticationProvider: DaoAuthenticationProvider,
                            private val httpFirewall: StrictHttpFirewall,
                            private val sessionLogic: ICureSessionLogic) {

    val log = LoggerFactory.getLogger(SecurityConfigAdapter::class.java)

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        //See https://stackoverflow.com/questions/50954018/prevent-session-creation-when-using-basic-auth-in-spring-security to prevent sessions creation TODO SH do we want this?
        return http
                .csrf().disable()
                .httpBasic().disable()
                .addFilterAt(basicAuthenticationWebFilter(), SecurityWebFiltersOrder.HTTP_BASIC) // TODO SH seems like basic filter is before cors filter !?
                .authenticationManager(authenticationManager()) // TODO SH should swap to an actually reactive version of CustomAuthenticationProvider
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // hasAnyRole coming in Spring 5.2: https://github.com/spring-projects/spring-security/pull/6310
                .pathMatchers("/rest/*/replication/group/**").hasRole("USER")
                .pathMatchers("/rest/*/replication/group/**").hasRole("BOOTSTRAP")
                .pathMatchers("/rest/*/auth/login").permitAll()
                .pathMatchers("/*/api-docs").permitAll()
                .pathMatchers("/rest/*/icure/v").permitAll()
                .pathMatchers("/rest/*/icure/p").permitAll()
                .pathMatchers("/rest/*/icure/check").permitAll()
                .pathMatchers("/rest/*/icure/c").permitAll()
                .pathMatchers("/rest/*/icure/ok").permitAll()
                .pathMatchers("/rest/*/icure/pok").permitAll()
                .pathMatchers("/rest/**").hasRole("USER")

                .pathMatchers("/api/login.html").permitAll()
                .pathMatchers("/api/css/**").permitAll()
                .pathMatchers("/api/**").hasRole("USER")

                .pathMatchers("/").permitAll()

                .pathMatchers("/ping.json").permitAll()

                .pathMatchers("/**").hasRole("USER")
                .and().build()
    }

    @Bean
    fun authenticationManager(): ReactiveAuthenticationManager {
        return ReactiveAuthenticationManagerAdapter(ProviderManager(listOf(daoAuthenticationProvider)))
    }

    @Bean
    fun basicAuthenticationWebFilter(): AuthenticationWebFilter {
        val basicFilter = AuthenticationWebFilter(authenticationManager())
        basicFilter.setAuthenticationSuccessHandler { webFilterExchange, authentication ->
            val exchange = webFilterExchange.exchange
            val result = sessionLogic.onAuthenticationSuccess(exchange, authentication)
            webFilterExchange.chain.filter(exchange).and(result)
        }
        return basicFilter
    }

}
