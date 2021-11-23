/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.server.WebSession
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.PropertyLogic
import org.taktik.icure.entities.User
import org.taktik.icure.security.PermissionSetIdentifier
import org.taktik.icure.security.UserDetails
import org.taktik.icure.security.loadSecurityContext
import java.io.Serializable
import javax.servlet.http.HttpSession

@ExperimentalCoroutinesApi
@Service
class AsyncSessionLogicImpl(private val authenticationManager: ReactiveAuthenticationManager,
                            private val userDAO: UserDAO,
                            private val propertyLogic: PropertyLogic) : AsyncSessionLogic {
    /* Generic */

    override fun getOrCreateSession(): HttpSession? {
        val requestAttributes = RequestContextHolder.getRequestAttributes()
        if (requestAttributes is ServletRequestAttributes) {
            val httpRequest = requestAttributes.request
            return httpRequest.getSession(true)
        }
        return null
    }

    override suspend fun login(username: String, password: String, request : ServerHttpRequest, session: WebSession): Authentication? {
        try {
            val token = UsernamePasswordAuthenticationToken(username, password)
            val authentication = authenticationManager.authenticate(token).awaitFirstOrNull()
            session.attributes[SESSION_LOCALE_ATTRIBUTE] = "fr" // TODO MB : add locale support
            return authentication
        } catch (e: AuthenticationException) {
            // Ignore
        }

        return null
    }

    override suspend fun logout() {
        invalidateCurrentAuthentication()
    }

    override fun getSessionContext(authentication: Authentication?): AsyncSessionLogic.AsyncSessionContext? {
        return authentication?.let { SessionContextImpl(it) }
    }

    override suspend fun getCurrentSessionContext(): AsyncSessionLogic.AsyncSessionContext {
        return getCurrentAuthentication()?.let { SessionContextImpl(it) }
                ?: throw AuthenticationServiceException("getCurrentAuthentication() returned null, no SecurityContext in the coroutine context?")
    }

    override suspend fun getCurrentUserId(): String {
        return getCurrentSessionContext().getUser().id
                ?: throw AuthenticationServiceException("Failed extracting currentUser id")
    }

    override suspend fun getCurrentHealthcarePartyId(): String {
        return getCurrentSessionContext().getUser().healthcarePartyId
                ?: throw AuthenticationServiceException("Failed extracting current healthCareParty id")
    }

    private inner class SessionContextImpl(private val authentication: Authentication) : AsyncSessionLogic.AsyncSessionContext, Serializable {
        private var userDetails: UserDetails = extractUserDetails(authentication)
        private var permissionSetIdentifier: PermissionSetIdentifier

        init {
            this.permissionSetIdentifier = userDetails.permissionSetIdentifier
        }

        fun getUserId(): String? = permissionSetIdentifier.getPrincipalIdOfClass(User::class.java)

        override fun getAuthentication(): Authentication = authentication

        override fun getUserDetails(): UserDetails = userDetails

        override fun isAuthenticated(): Boolean = authentication.isAuthenticated

        override fun isAnonymous(): Boolean = false

        override suspend fun getUser(): User {
            val userId = getUserId()
            return userId?.let { userDAO.getUserOnUserDb(userId, false) }
                    ?: throw AuthenticationServiceException("Failed getting the user from session context : userId=$userId")
        }
    }

    companion object {
        val SESSION_LOCALE_ATTRIBUTE = "locale";

        private val log = LoggerFactory.getLogger(AsyncSessionLogicImpl::class.java)

        private suspend fun getCurrentAuthentication() =
                loadSecurityContext()?.map { it.authentication }?.awaitSingle()

        private suspend fun invalidateCurrentAuthentication() {
                loadSecurityContext()?.map { it.authentication.isAuthenticated = false }?.awaitSingle()
                        ?: throw AuthenticationServiceException("Could not find authentication object in ReactorContext")
        }

        private fun extractUserDetails(authentication: Authentication): UserDetails {
            return authentication.principal?.let { it as? UserDetails }
                    ?: throw AuthenticationServiceException("Failed extracting user details: ${authentication.principal}")
        }
    }
}
