package org.taktik.icure.asynclogic

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.Authentication
import org.springframework.web.server.WebSession
import org.taktik.icure.entities.User
import org.taktik.icure.security.UserDetails
import java.io.Serializable
import java.net.URI
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

interface AsyncSessionLogic {
    suspend fun login(username: String, password: String, request : ServerHttpRequest, session: WebSession): Authentication?

    suspend fun logout()

    suspend fun logout(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse)

    /* SessionContext related */

    fun getSessionContext(authentication: Authentication?): AsyncSessionContext?

    suspend fun getCurrentSessionContext(): AsyncSessionContext

    fun getOrCreateSession(): HttpSession?
    suspend fun getCurrentUserId(): String
    suspend fun getCurrentHealthcarePartyId(): String

    interface AsyncSessionContext : Serializable {
        fun getAuthentication(): Authentication
        fun getUserDetails(): UserDetails
        fun isAuthenticated(): Boolean
        fun isAnonymous(): Boolean
        suspend fun getUser(): User
    }
}
