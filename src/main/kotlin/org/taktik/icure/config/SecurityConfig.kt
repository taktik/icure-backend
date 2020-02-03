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

import CustomAuthenticationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.firewall.StrictHttpFirewall
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.taktik.icure.asyncdao.GroupDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.PermissionLogic
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.security.database.ShaAndVerificationCodePasswordEncoder


@ExperimentalCoroutinesApi
@Configuration
class SecurityConfig {

    @Bean
    fun passwordEncoder() = ShaAndVerificationCodePasswordEncoder("SHA-256")

    @Bean
    fun httpFirewall() = StrictHttpFirewall().apply { setAllowSemicolon(true) } // TODO SH later: might be ignored if not registered in the security config

    @Bean
    fun authenticationManager(
            couchDbProperties: CouchDbProperties,
            userDAO: UserDAO,
            groupDAO: GroupDAO,
            permissionLogic: PermissionLogic,
            passwordEncoder: PasswordEncoder
    ) =
            CustomAuthenticationProvider(couchDbProperties, userDAO, groupDAO, permissionLogic, passwordEncoder)
}

@ExperimentalCoroutinesApi
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfigAdapter(private val httpFirewall: StrictHttpFirewall,
                            private val sessionLogic: AsyncSessionLogic,
                            private val authenticationManager: CustomAuthenticationProvider) {

    val log: Logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
                .csrf().disable()
                .httpBasic().securityContextRepository(WebSessionServerSecurityContextRepository())
                //.securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) //See https://stackoverflow.com/questions/50954018/prevent-session-creation-when-using-basic-auth-in-spring-security to prevent sessions creation // https://stackoverflow.com/questions/56056404/disable-websession-creation-when-using-spring-security-with-spring-webflux for webflux (TODO SH later: necessary?)
                .authenticationManager(authenticationManager)
                .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers("/v2/api-docs").permitAll()
                .pathMatchers("/api/**").permitAll()
                .pathMatchers("/rest/*/replication/group/**").hasAnyRole("USER", "BOOTSTRAP")
                .pathMatchers("/rest/*/auth/login").permitAll()
                .pathMatchers("/rest/*/icure/v").permitAll()
                .pathMatchers("/rest/*/icure/p").permitAll()
                .pathMatchers("/rest/*/icure/check").permitAll()
                .pathMatchers("/rest/*/icure/c").permitAll()
                .pathMatchers("/rest/*/icure/ok").permitAll()
                .pathMatchers("/rest/*/icure/pok").permitAll()
                .pathMatchers("/rest/**").hasRole("USER")
                .pathMatchers("/").permitAll()
                .pathMatchers("/ping.json").permitAll()
                .pathMatchers("/**").hasRole("USER")
                .and().build()
    }

}
