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
package org.taktik.icure.asynclogic

import java.net.URI
import java.net.URISyntaxException
import java.util.concurrent.Callable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import org.springframework.security.core.Authentication
import org.taktik.icure.entities.User
import org.taktik.icure.security.UserDetails

interface SessionLogic {
	fun login(username: String?, password: String?): SessionContext?
	fun logout()
	fun logout(httpRequest: HttpServletRequest)

	fun getOrCreateSession(): HttpSession?
	fun getCurrentUserId(): String?
	fun getCurrentHealthcarePartyId(): String?
	fun getCurrentGroupId(): String?
	fun getCurrentInstanceUrls(): String?

	fun currentSessionContext(): SessionContext?
	fun <T> doInSessionContext(sessionContext: SessionContext?, callable: Callable<T>?): T

	interface SessionContext {
		val authentication: Authentication?
		val userDetails: UserDetails?
		val isAuthenticated: Boolean
		val isAnonymous: Boolean
		val user: User?
		val dbInstanceUrl: String?

		@get:Throws(URISyntaxException::class)
		val dbInstanceUri: URI?

		val groupIdUserId: String?
		val groupId: String?
	}
}
