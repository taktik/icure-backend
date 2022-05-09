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

import java.io.Serializable
import javax.servlet.http.HttpSession
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.Authentication
import org.springframework.web.server.WebSession
import org.taktik.icure.entities.User
import org.taktik.icure.security.UserDetails

interface AsyncSessionLogic {
	suspend fun login(username: String, password: String, request: ServerHttpRequest, session: WebSession): Authentication?

	suspend fun logout()
	fun getSessionContext(authentication: Authentication?): AsyncSessionContext?

	suspend fun getCurrentSessionContext(): AsyncSessionContext

	fun getOrCreateSession(): HttpSession?
	suspend fun getCurrentUserId(): String
	suspend fun getCurrentHealthcarePartyId(): String
	suspend fun getCurrentDataOwnerId(): String

	interface AsyncSessionContext : Serializable {
		fun getAuthentication(): Authentication
		fun getUserDetails(): UserDetails
		fun isAuthenticated(): Boolean
		fun isAnonymous(): Boolean
		suspend fun getUser(): User
	}

	suspend fun getCurrentPatientId(): String?
}
