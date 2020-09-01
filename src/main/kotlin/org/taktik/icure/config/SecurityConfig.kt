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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.encoding.PasswordEncoder
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.FilterChainProxy
import org.springframework.security.web.access.ExceptionTranslationFilter
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor
import org.springframework.security.web.firewall.HttpFirewall
import org.springframework.security.web.firewall.StrictHttpFirewall
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.taktik.icure.logic.ICureSessionLogic
import org.taktik.icure.logic.PermissionLogic
import org.taktik.icure.logic.UserLogic
import org.taktik.icure.security.AuthenticationFailureHandler
import org.taktik.icure.security.AuthenticationSuccessHandler
import org.taktik.icure.security.Http401UnauthorizedEntryPoint
import org.taktik.icure.security.database.CustomAuthenticationProvider
import org.taktik.icure.security.database.ShaAndVerificationCodePasswordEncoder
import org.taktik.icure.security.web.BasicAuthenticationFilter
import org.taktik.icure.security.web.LoginUrlAuthenticationEntryPoint
import org.taktik.icure.security.web.UsernamePasswordAuthenticationFilter
import javax.servlet.Filter

@Configuration
class SecurityConfig {

    @Bean
    fun passwordEncoder() = ShaAndVerificationCodePasswordEncoder(256)

    @Bean
    fun authenticationProcessingFilterEntryPoint() = LoginUrlAuthenticationEntryPoint("/", mapOf("/api" to "api/login.html"))

    @Bean
    fun basicAuthenticationFilter(authenticationManager: AuthenticationManager, authenticationProcessingFilterEntryPoint: LoginUrlAuthenticationEntryPoint) = BasicAuthenticationFilter(authenticationManager)

    /*@Bean
    fun sameSiteFilter() = object : GenericFilterBean() {
        override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
            response?.let { (it as? HttpServletResponse)?.setHeader("Set-Cookie", "HttpOnly; SameSite=None; Secure") }
            chain?.doFilter(request, response)
        }
    }*/

    @Bean
    fun usernamePasswordAuthenticationFilter(authenticationManager: AuthenticationManager, authenticationProcessingFilterEntryPoint: LoginUrlAuthenticationEntryPoint, sessionLogic: ICureSessionLogic) = UsernamePasswordAuthenticationFilter().apply {
        usernameParameter = "username"
        passwordParameter = "password"
        setAuthenticationManager(authenticationManager)
        setAuthenticationSuccessHandler(AuthenticationSuccessHandler().apply { setDefaultTargetUrl("/"); setAlwaysUseDefaultTargetUrl(false); setSessionLogic(sessionLogic) })
        setAuthenticationFailureHandler(AuthenticationFailureHandler().apply { setDefaultFailureUrl("/error"); })
        setRequiresAuthenticationRequestMatcher(AntPathRequestMatcher("/login"))
        setPostOnly(true)
    }

    @Bean
    fun remotingExceptionTranslationFilter() = ExceptionTranslationFilter(Http401UnauthorizedEntryPoint(), HttpSessionRequestCache().apply { setCreateSessionAllowed(false) })

    @Bean
    fun exceptionTranslationFilter(authenticationProcessingFilterEntryPoint: LoginUrlAuthenticationEntryPoint) = ExceptionTranslationFilter(authenticationProcessingFilterEntryPoint)

    @Bean
    fun httpFirewal() = StrictHttpFirewall().apply { setAllowSemicolon(true) }

    @Bean
    fun securityConfigAdapter(
            daoAuthenticationProvider: CustomAuthenticationProvider,
            //sameSiteFilter: GenericFilterBean,
            basicAuthenticationFilter: BasicAuthenticationFilter,
            usernamePasswordAuthenticationFilter: UsernamePasswordAuthenticationFilter,
            exceptionTranslationFilter: ExceptionTranslationFilter,
            remotingExceptionTranslationFilter: ExceptionTranslationFilter,
            httpFirewall: HttpFirewall
                             )
        = SecurityConfigAdapter(daoAuthenticationProvider, /*sameSiteFilter, */ basicAuthenticationFilter, usernamePasswordAuthenticationFilter, exceptionTranslationFilter, remotingExceptionTranslationFilter, httpFirewall)

    @Bean
    fun daoAuthenticationProvider(userLogic: UserLogic, permissionLogic: PermissionLogic, passwordEncoder: PasswordEncoder) = CustomAuthenticationProvider(userLogic, permissionLogic).apply {
        setPasswordEncoder(passwordEncoder)
    }

}

@Configuration
class SecurityConfigAdapter(private val daoAuthenticationProvider: CustomAuthenticationProvider,
                            //private val sameSiteFilter: Filter,
                            private val basicAuthenticationFilter: Filter,
                            private val usernamePasswordAuthenticationFilter: Filter,
                            private val exceptionTranslationFilter: Filter,
                            private val remotingExceptionTranslationFilter: Filter,
                            private val httpFirewall: HttpFirewall
    ) : WebSecurityConfigurerAdapter(false) {

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder?) {
        auth!!.authenticationProvider(daoAuthenticationProvider)
    }

    override fun configure(web: WebSecurity) {
        web.httpFirewall(httpFirewall)
    }

    override fun configure(http: HttpSecurity?) {
        //See https://stackoverflow.com/questions/50954018/prevent-session-creation-when-using-basic-auth-in-spring-security to prevent sessions creation
        http!!.csrf().disable().addFilterBefore(
            FilterChainProxy(
                listOf(
                    DefaultSecurityFilterChain(AntPathRequestMatcher("/rest/**"), /* sameSiteFilter, */ basicAuthenticationFilter, remotingExceptionTranslationFilter),
                    DefaultSecurityFilterChain(AntPathRequestMatcher("/**"), /* sameSiteFilter, */ basicAuthenticationFilter, usernamePasswordAuthenticationFilter, exceptionTranslationFilter)
                      )
                ).apply {
                    setFirewall(httpFirewall)
                }, FilterSecurityInterceptor::class.java)
                .authorizeRequests()
                .antMatchers("/rest/*/replication/group/**").hasAnyRole("USER", "BOOTSTRAP")
                .antMatchers("/rest/*/auth/login").permitAll()
                .antMatchers("/rest/*/swagger.json").permitAll()
                .antMatchers("/rest/*/icure/v").permitAll()
                .antMatchers("/rest/*/icure/p").permitAll()
                .antMatchers("/rest/*/icure/check").permitAll()
                .antMatchers("/rest/*/icure/c").permitAll()
                .antMatchers("/rest/*/icure/ok").permitAll()
                .antMatchers("/rest/*/icure/pok").permitAll()
                .antMatchers("/rest/*/user/forgottenPassword/*").permitAll()

                .antMatchers("/rest/**").hasRole("USER")

                .antMatchers("/api/login.html").permitAll()
                .antMatchers("/api/css/**").permitAll()
                .antMatchers("/api/**").hasRole("USER")

                .antMatchers("/").permitAll()

                .antMatchers("/ping.json").permitAll()

                .antMatchers("/**").hasRole("USER")
    }
}
