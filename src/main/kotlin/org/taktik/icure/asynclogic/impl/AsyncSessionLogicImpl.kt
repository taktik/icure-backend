/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.ReactorContext
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.taktik.icure.constants.PropertyTypes
import org.taktik.icure.entities.User
import org.taktik.icure.logic.PropertyLogic
import org.taktik.icure.logic.UserLogic
import org.taktik.icure.security.PermissionSetIdentifier
import org.taktik.icure.security.UserDetails
import org.taktik.icure.security.database.DatabaseUserDetails
import reactor.core.publisher.Mono
import java.net.URI
import java.util.concurrent.Callable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import kotlin.coroutines.coroutineContext

interface AsyncICureSessionLogic : AsyncSessionLogic {
    fun getOrCreateSession(): HttpSession?
    suspend fun getCurrentUserId(): Mono<String>
    suspend fun getCurrentHealthcarePartyId(): Mono<String>
}
// TODO SH suspend instead of Monos via awaitSingle
interface AsyncSessionLogic {
    suspend fun login(username: String, password: String): AsyncSessionContext?

    suspend fun logout()

    suspend fun logout(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse)

    /* SessionContext related */

    fun getSessionContext(authentication: Authentication): AsyncSessionContext

    suspend fun getCurrentSessionContext(): Mono<AsyncSessionContext>

    suspend fun getInstanceAndGroupInformationFromSecurityContext(): Pair<URI, String>

    fun <T> doInSessionContext(sessionContext: AsyncSessionContext, callable: Callable<T>?): T?

    interface AsyncSessionContext {
        fun getAuthentication(): Authentication
        fun getUserDetails(): UserDetails?
        fun isAuthenticated(): Boolean
        fun isAnonymous(): Boolean
        fun getUser(): User
        fun getDbInstanceUrl(): String
        fun getDbInstanceUri(): URI
        fun getGroupIdUserId(): String
        fun getGroupId(): String
    }
}

@Transactional
@Service
class AsyncSessionLogicImpl(private val authenticationManager: ReactiveAuthenticationManager,
                            private val userLogic: UserLogic,
                            private val propertyLogic: PropertyLogic) : AsyncICureSessionLogic {
    /* Generic */

    override fun getOrCreateSession(): HttpSession? {
        val requestAttributes = RequestContextHolder.getRequestAttributes()
        if (requestAttributes is ServletRequestAttributes) {
            val httpRequest = requestAttributes.request
            return httpRequest.getSession(true)
        }
        return null
    }

    override suspend fun login(username: String, password: String): AsyncSessionLogic.AsyncSessionContext? {
        try {
            // Try to authenticate using given username and password
            val token = UsernamePasswordAuthenticationToken(username, password)
            val authentication = authenticationManager.authenticate(token).block()!! // TODO SH no block

            // Set current authentication
            setCurrentAuthentication(authentication)

            // Check if authentication succeeded
            if (authentication != null && authentication.isAuthenticated) {
                var httpRequest: HttpServletRequest? = null
                val requestAttributes = RequestContextHolder.getRequestAttributes()
                if (requestAttributes is ServletRequestAttributes) {
                    httpRequest = requestAttributes.request
                }
            }
            return getSessionContext(authentication)
        } catch (e: AuthenticationException) {
            // Ignore
        }

        return null
    }

    override suspend fun logout() {
        // Remove current session context
        setCurrentAuthentication(null)
    }

    override suspend fun logout(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse) {
        var logoutURL = propertyLogic.getSystemPropertyValue<String>(PropertyTypes.System.AUTH_URL_LOGOUT.identifier)

        // Invalidate session
        val httpSession = httpRequest.getSession(false)
        if (httpSession != null) {
            try {
                httpSession.invalidate()
            } catch (e: IllegalStateException) {
                log.error("Exception", e)
            }
        }

        // Get SessionContext
        val sessionContext = getCurrentSessionContext().block() // TODO SH

        // Get UserDetails
        val userDetails = sessionContext?.getUserDetails()

        // Determine logout URL
        if (userDetails != null && userDetails.getLogoutURL() != null && !userDetails.getLogoutURL().isEmpty()) {
            logoutURL = userDetails.getLogoutURL()
        }

        if (logoutURL != null && !logoutURL.isEmpty()) {
            // Check for relative path
            if (logoutURL[0] == '/') {
                logoutURL = logoutURL.substring(1)
            }

            // Redirect to logout URL
            httpResponse.sendRedirect(logoutURL)
        }
    }

    /* SessionContext related */

    override fun getSessionContext(authentication: Authentication): AsyncSessionLogic.AsyncSessionContext {
        return SessionContextImpl(authentication)
    }

    @ExperimentalCoroutinesApi
    override suspend fun getCurrentSessionContext(): Mono<AsyncSessionLogic.AsyncSessionContext> {
        val authentication = getCurrentAuthentication()
        return authentication?.map { SessionContextImpl(it) }!!
    }

    override suspend fun getInstanceAndGroupInformationFromSecurityContext(): Pair<URI, String> {
        val dbInstanceUri = getCurrentSessionContext().map { it.getDbInstanceUri() }.awaitSingle()
        val groupId = getCurrentSessionContext().map { it.getGroupId() }.awaitSingle()
        return Pair(dbInstanceUri, groupId)
    }

//    override fun <T> doInSessionContext(sessionContext: SessionLogic.SessionContext, callable: Callable<T>?): Mono<T?> {
//        // Backup current sessionContext and authentication if any
//        val previousAuthentication = getCurrentAuthentication()
//
//        // Set new authentication
//        previousAuthentication.map { previous ->
//            setCurrentAuthentication(sessionContext?.getAuthentication()).map {
//
//                // Prepare result
//                var result: T? = null
//
//                // Call callable
//                try {
//                    if (callable != null) {
//                        result = callable.call()
//                    }
//                } finally {
//                    // Restore previous sessionContext and authentication
//                    return setCurrentAuthentication(previous).map { result }
//                }
//            }
//        }
//    }

    override fun <T> doInSessionContext(sessionContext: AsyncSessionLogic.AsyncSessionContext, callable: Callable<T>?): T? = null


    override suspend fun getCurrentUserId(): Mono<String> {
        return getCurrentSessionContext().map { it.getUser().id }
    }

    override suspend fun getCurrentHealthcarePartyId(): Mono<String> {
        return getCurrentSessionContext().map { it.getUser().healthcarePartyId }
    }

    private inner class SessionContextImpl(private val authentication: Authentication) : AsyncSessionLogic.AsyncSessionContext {
        private var userDetails: UserDetails = extractUserDetails(authentication)
        private var permissionSetIdentifier: PermissionSetIdentifier

        init {
            this.permissionSetIdentifier = userDetails.permissionSetIdentifier
        }

        fun getUserId(): String? = permissionSetIdentifier.getPrincipalIdOfClass(User::class.java)

        override fun getAuthentication(): Authentication = authentication

        override fun getUserDetails(): UserDetails? = userDetails

        override fun isAuthenticated(): Boolean = authentication.isAuthenticated

        override fun isAnonymous(): Boolean = false

        override fun getUser(): User {
            val userId = getUserId()
            val groupId = getGroupId()
            val u = userLogic.getUserOnUserDb(userId, groupId, getDbInstanceUrl())
            u.groupId = groupId
            return u
        }

        override fun getDbInstanceUrl(): String = (userDetails as DatabaseUserDetails).dbInstanceUrl

        override fun getDbInstanceUri(): URI = URI((userDetails as DatabaseUserDetails).dbInstanceUrl)

        override fun getGroupIdUserId(): String = "${getGroupId()}:${getUserId()}"

        override fun getGroupId(): String = (userDetails as DatabaseUserDetails).groupId
    }

    companion object {
        private val log = LoggerFactory.getLogger(AsyncSessionLogicImpl::class.java)

        /* Static methods */

//        private fun getCurrentAuthentication(): Mono<Authentication> {
//            val context = ReactiveSecurityContextHolder.getContext()
//            return context.map { it.authentication }
//        }

        private suspend fun getCurrentAuthentication() =
                coroutineContext[ReactorContext]?.context?.get<Mono<SecurityContext>>(SecurityContext::class.java)?.map { it.authentication }

//        private fun setCurrentAuthentication(authentication: Authentication?): Mono<Unit> {
//            if (authentication != null) {
//                return ReactiveSecurityContextHolder.getContext().map { it.authentication = authentication }
//            }
//            return Mono.empty()
//        }

        private suspend fun setCurrentAuthentication(authentication: Authentication?): Mono<Unit> {
            if (authentication != null) {
                    return coroutineContext[ReactorContext]?.context?.get<Mono<SecurityContext>>(SecurityContext::class.java)?.map { it.authentication = authentication }!! // TODO SH !!
            }
            return Mono.empty()
        }

        private fun extractUserDetails(authentication: Authentication): UserDetails {
            return authentication.principal?.let { it as? UserDetails }
                    ?: throw AuthenticationServiceException("Failed extracting user details: ${authentication.principal}")
        }
    }
}
