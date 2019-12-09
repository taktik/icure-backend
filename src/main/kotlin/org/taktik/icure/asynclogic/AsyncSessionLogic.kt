package org.taktik.icure.asynclogic

import org.springframework.security.core.Authentication
import org.taktik.icure.entities.User
import org.taktik.icure.security.UserDetails
import java.net.URI
import java.util.concurrent.Callable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

interface AsyncSessionLogic {
    suspend fun login(username: String, password: String): AsyncSessionContext?

    suspend fun logout()

    suspend fun logout(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse)

    /* SessionContext related */

    fun getSessionContext(authentication: Authentication?): AsyncSessionContext?

    suspend fun getCurrentSessionContext(): AsyncSessionContext

    suspend fun getInstanceAndGroupInformationFromSecurityContext(): Pair<URI, String>

    fun <T> doInSessionContext(sessionContext: AsyncSessionContext, callable: Callable<T>?): T?

    interface AsyncSessionContext {
        fun getAuthentication(): Authentication
        fun getUserDetails(): UserDetails
        fun isAuthenticated(): Boolean
        fun isAnonymous(): Boolean
        fun getUser(): User
        fun getDbInstanceUrl(): String
        fun getDbInstanceUri(): URI
        fun getGroupIdUserId(): String
        fun getGroupId(): String
    }
}
