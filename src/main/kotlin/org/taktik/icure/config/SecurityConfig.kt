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

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.FilterChainProxy
import org.springframework.security.web.access.ExceptionTranslationFilter
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor
import org.springframework.security.web.firewall.StrictHttpFirewall
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.taktik.icure.logic.*
import org.taktik.icure.security.AuthenticationFailureHandler
import org.taktik.icure.security.AuthenticationSuccessHandler
import org.taktik.icure.security.Http401UnauthorizedEntryPoint
import org.taktik.icure.security.database.CustomAuthenticationProvider
import org.taktik.icure.security.database.ShaAndVerificationCodePasswordEncoder
import org.taktik.icure.security.web.BasicAuthenticationFilter
import org.taktik.icure.security.web.LoginUrlAuthenticationEntryPoint
import org.taktik.icure.security.web.UsernamePasswordAuthenticationFilter


@Configuration
class SecurityConfig {
    @Bean
    fun passwordEncoder() = ShaAndVerificationCodePasswordEncoder("SHA-256")

    @Bean
    fun httpFirewall() = StrictHttpFirewall().apply { setAllowSemicolon(true) }

    @Bean
    fun daoAuthenticationProvider(userLogic: UserLogic, groupLogic: GroupLogic, permissionLogic: PermissionLogic, passwordEncoder: PasswordEncoder) = CustomAuthenticationProvider(userLogic, groupLogic, permissionLogic).apply {
        setPasswordEncoder(passwordEncoder)
    }
}

@Configuration
class SecurityConfigAdapter(private val daoAuthenticationProvider: DaoAuthenticationProvider,
                            private val httpFirewall: StrictHttpFirewall,
                            private val sessionLogic: ICureSessionLogic) : WebSecurityConfigurerAdapter(false) {

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.authenticationProvider(daoAuthenticationProvider)
    }

    override fun configure(web: WebSecurity) {
        web.httpFirewall(httpFirewall)
    }

    override fun configure(http: HttpSecurity?) {
        //See https://stackoverflow.com/questions/50954018/prevent-session-creation-when-using-basic-auth-in-spring-security to prevent sessions creation
        http!!
                .csrf().disable()
                .cors().and() // adds the Spring-provided CorsFilter to the application context which in turn bypasses the authorization checks for OPTIONS requests.
                .addFilterBefore(
            FilterChainProxy(
                listOf(
                    DefaultSecurityFilterChain(AntPathRequestMatcher("/rest/**"), basicAuthenticationFilter(), remotingExceptionTranslationFilter()),
                    DefaultSecurityFilterChain(AntPathRequestMatcher("/**"), basicAuthenticationFilter(), usernamePasswordAuthenticationFilter(), exceptionTranslationFilter())
                      )
                ).apply {
                    setFirewall(httpFirewall)
                }, FilterSecurityInterceptor::class.java)
                .authorizeRequests()
                .antMatchers("/rest/*/replication/group/**").hasAnyRole("USER", "BOOTSTRAP")
                .antMatchers("/rest/*/auth/login").permitAll()
                .antMatchers("/*/api-docs").permitAll()
                .antMatchers("/rest/*/icure/v").permitAll()
                .antMatchers("/rest/*/icure/p").permitAll()
                .antMatchers("/rest/*/icure/check").permitAll()
                .antMatchers("/rest/*/icure/c").permitAll()
                .antMatchers("/rest/*/icure/ok").permitAll()
                .antMatchers("/rest/*/icure/pok").permitAll()
                .antMatchers("/rest/**").hasRole("USER")

                .antMatchers("/api/login.html").permitAll()
                .antMatchers("/api/css/**").permitAll()
                .antMatchers("/api/**").hasRole("USER")

                .antMatchers("/").permitAll()

                .antMatchers("/ping.json").permitAll()

                .antMatchers("/**").hasRole("USER")
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    fun authenticationProcessingFilterEntryPoint() = LoginUrlAuthenticationEntryPoint("/", mapOf("/api" to "api/login.html"))

    @Bean
    fun basicAuthenticationFilter() = BasicAuthenticationFilter(authenticationManagerBean())

    fun usernamePasswordAuthenticationFilter() = UsernamePasswordAuthenticationFilter().apply {
        usernameParameter = "username"
        passwordParameter = "password"
        setAuthenticationManager(authenticationManager())
        setAuthenticationSuccessHandler(AuthenticationSuccessHandler().apply { setDefaultTargetUrl("/"); setAlwaysUseDefaultTargetUrl(false); setSessionLogic(sessionLogic) })
        setAuthenticationFailureHandler(AuthenticationFailureHandler().apply { setDefaultFailureUrl("/error"); })
        setRequiresAuthenticationRequestMatcher(AntPathRequestMatcher("/login"))
        setPostOnly(true)
    }

    fun remotingExceptionTranslationFilter() = ExceptionTranslationFilter(Http401UnauthorizedEntryPoint())

    fun exceptionTranslationFilter() = ExceptionTranslationFilter(authenticationProcessingFilterEntryPoint())
}
