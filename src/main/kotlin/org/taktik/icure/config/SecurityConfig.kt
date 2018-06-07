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
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.encoding.PasswordEncoder
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.FilterChainProxy
import org.springframework.security.web.access.ExceptionTranslationFilter
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.taktik.icure.logic.ICureSessionLogic
import org.taktik.icure.security.AuthenticationFailureHandler
import org.taktik.icure.security.AuthenticationSuccessHandler
import org.taktik.icure.security.Http401UnauthorizedEntryPoint
import org.taktik.icure.security.database.ApplicationTokensUserDetailsAuthenticationProvider
import org.taktik.icure.security.database.CustomAuthenticationProvider
import org.taktik.icure.security.database.ShaAndVerificationCodePasswordEncoder
import org.taktik.icure.security.database.UserDetailsService
import org.taktik.icure.security.web.BasicAuthenticationFilter
import org.taktik.icure.security.web.LoginUrlAuthenticationEntryPoint
import org.taktik.icure.security.web.UsernamePasswordAuthenticationFilter
import javax.servlet.Filter

@Configuration
class SecurityConfig {
	@Bean fun passwordEncoder() = ShaAndVerificationCodePasswordEncoder(256)
	@Bean fun authenticationProcessingFilterEntryPoint() = LoginUrlAuthenticationEntryPoint("/", mapOf("/api" to "api/login.html"))
	@Bean fun basicAuthenticationFilter(authenticationManager: AuthenticationManager, authenticationProcessingFilterEntryPoint: LoginUrlAuthenticationEntryPoint) = BasicAuthenticationFilter(authenticationManager, authenticationProcessingFilterEntryPoint)
	@Bean fun usernamePasswordAuthenticationFilter(authenticationManager: AuthenticationManager, authenticationProcessingFilterEntryPoint: LoginUrlAuthenticationEntryPoint, sessionLogic: ICureSessionLogic) = UsernamePasswordAuthenticationFilter().apply {
		usernameParameter = "username"
		passwordParameter = "password"
		setAuthenticationManager(authenticationManager)
		setAuthenticationSuccessHandler(AuthenticationSuccessHandler().apply { setDefaultTargetUrl("/"); setAlwaysUseDefaultTargetUrl(false); setSessionLogic(sessionLogic) })
		setAuthenticationFailureHandler(AuthenticationFailureHandler().apply { setDefaultFailureUrl("/error"); })
		setRequiresAuthenticationRequestMatcher(AntPathRequestMatcher("/login"))
		setPostOnly(true)
	}
	@Bean fun remotingExceptionTranslationFilter() = ExceptionTranslationFilter(Http401UnauthorizedEntryPoint())
	@Bean fun exceptionTranslationFilter(authenticationProcessingFilterEntryPoint: LoginUrlAuthenticationEntryPoint) = ExceptionTranslationFilter(authenticationProcessingFilterEntryPoint)
	@Bean fun securityConfigAdapter(
			daoAuthenticationProvider:CustomAuthenticationProvider,
			applicationTokensAuthenticationProvider:ApplicationTokensUserDetailsAuthenticationProvider,
			basicAuthenticationFilter : BasicAuthenticationFilter,
			usernamePasswordAuthenticationFilter : UsernamePasswordAuthenticationFilter,
			exceptionTranslationFilter : ExceptionTranslationFilter,
			remotingExceptionTranslationFilter : ExceptionTranslationFilter) = SecurityConfigAdapter(daoAuthenticationProvider, applicationTokensAuthenticationProvider, basicAuthenticationFilter, usernamePasswordAuthenticationFilter, exceptionTranslationFilter, remotingExceptionTranslationFilter)
	@Bean fun daoAuthenticationProvider(userDetailsService : UserDetailsService, passwordEncoder: PasswordEncoder) = CustomAuthenticationProvider().apply {
		setPasswordEncoder(passwordEncoder)
		setUserDetailsService(userDetailsService)
	}
	@Bean fun applicationTokensAuthenticationProvider() = ApplicationTokensUserDetailsAuthenticationProvider()
	@Bean fun userDetailsService() = UserDetailsService()
}

@Configuration
class SecurityConfigAdapter(private val daoAuthenticationProvider: CustomAuthenticationProvider,
                                          private val applicationTokensAuthenticationProvider: ApplicationTokensUserDetailsAuthenticationProvider,
                                          private val basicAuthenticationFilter : Filter,
                                          private val usernamePasswordAuthenticationFilter : Filter,
                                          private val exceptionTranslationFilter : Filter,
                                          private val remotingExceptionTranslationFilter : Filter) : WebSecurityConfigurerAdapter(false) {
	@Autowired
	fun configureGlobal(auth: AuthenticationManagerBuilder?) {
		auth!!.authenticationProvider(daoAuthenticationProvider)
				.authenticationProvider(applicationTokensAuthenticationProvider)
	}

	override fun configure(http: HttpSecurity?) {
		http!!.csrf().disable().addFilterBefore(FilterChainProxy(listOf(
				DefaultSecurityFilterChain(AntPathRequestMatcher("/rest/**"), basicAuthenticationFilter, remotingExceptionTranslationFilter),
				DefaultSecurityFilterChain(AntPathRequestMatcher("/**"), basicAuthenticationFilter, usernamePasswordAuthenticationFilter, exceptionTranslationFilter))), FilterSecurityInterceptor::class.java)
				.authorizeRequests()
				.antMatchers("/login").permitAll()
				.antMatchers("/rest/*/hcparty/signup").hasAnyRole("USER","BOOTSTRAP")
				.antMatchers("/rest/*/replication/group/**").hasAnyRole("USER","BOOTSTRAP")
				.antMatchers("/rest/*/auth/**").permitAll()
				.antMatchers("/rest/*/swagger.json").permitAll()
				.antMatchers("/rest/*/icure/v").permitAll()
				.antMatchers("/rest/*/icure/p").permitAll()
				.antMatchers("/rest/*/icure/ok").permitAll()
				.antMatchers("/rest/*/icure/pok").permitAll()
				.antMatchers("/rest/**").hasRole("USER")

				.antMatchers("/api/login.html").permitAll()
				.antMatchers("/api/css/**").permitAll()
				.antMatchers("/api/**").hasRole("USER")

				.antMatchers("/ht").permitAll()
				.antMatchers("/tz").permitAll()
				.antMatchers("/ht/**").permitAll()
				.antMatchers("/tz/**").permitAll()

				.antMatchers("/ping.json").permitAll()

				.antMatchers("/**").hasRole("USER")
	}
}
