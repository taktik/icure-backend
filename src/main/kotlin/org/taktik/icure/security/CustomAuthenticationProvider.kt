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

package org.taktik.icure.security

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import org.apache.commons.logging.LogFactory
import org.jboss.aerogear.security.otp.Totp
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.SpringSecurityMessageSource
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.util.Assert
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.PermissionLogic
import org.taktik.icure.constants.Users
import org.taktik.icure.entities.User
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.security.database.DatabaseUserDetails
import reactor.core.publisher.Mono
import java.net.URI
import java.util.*

@ExperimentalCoroutinesApi
class CustomAuthenticationProvider(
        couchDbProperties: CouchDbProperties,
        private val userDAO: UserDAO, //prevent cyclic dependnecies
        private val permissionLogic: PermissionLogic,
        private val passwordEncoder: PasswordEncoder,
        private val messageSourceAccessor: MessageSourceAccessor = SpringSecurityMessageSource.getAccessor()
) : ReactiveAuthenticationManager {
    private val log = LogFactory.getLog(javaClass)
    private val dbInstanceUri = URI(couchDbProperties.url)
    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    override fun authenticate(authentication: Authentication?): Mono<Authentication> = mono {
        authentication?.principal ?: throw BadCredentialsException("Invalid username or password")
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken::class.java, authentication,
                messageSourceAccessor.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.onlySupports",
                        "Only UsernamePasswordAuthenticationToken is supported"))

        val username: String = authentication.name
        val isFullToken = username.matches(Regex("(.+/)([0-9a-zA-Z]{8}-?[0-9a-zA-Z]{4}-?[0-9a-zA-Z]{4}-?[0-9a-zA-Z]{4}-?[0-9a-zA-Z]{12}|idUser_.+)"))
        val isPartialToken = username.matches(Regex("[0-9a-zA-Z]{8}-?[0-9a-zA-Z]{4}-?[0-9a-zA-Z]{4}-?[0-9a-zA-Z]{4}-?[0-9a-zA-Z]{12}|idUser_.+"))

        val usersFlow = when {
            isFullToken -> {
                userDAO.getUsersByPartialIdOnFallback(username.replace(Regex("(.+/)"),""))
            }
            isPartialToken -> {
                userDAO.getUsersByPartialIdOnFallback(username)
            }
            else -> {
                userDAO.findByUsernameOnFallback(username)
            }
        }

        val users = usersFlow
                .filterNotNull()
                .filter { it.status == Users.Status.ACTIVE }
                .toList()
                .sortedBy { it.id }


        val matchingUsers = mutableListOf<User>()
        val password: String = authentication.credentials.toString()

        for (candidate in users) {
            if (candidate != null && isPasswordValid(candidate, password)) {
                matchingUsers.add(candidate)
            }
        }

        val user: User? = matchingUsers.firstOrNull()

        if (user == null) {
            log.warn("Invalid username or password for user " + username + ", no user matched out of " + users.size + " candidates")
            throw BadCredentialsException("Invalid username or password")
        }

        if (user.use2fa == true && user.secret?.isNotBlank() == true && !user.applicationTokens.containsValue(password)) {
            val splittedPassword = password.split("\\|")
            if (splittedPassword.size < 2) {
                throw BadCredentialsException("Missing verfication code")
            }
            val verificationCode = splittedPassword[1]
            val totp = Totp(user.secret)
            if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
                throw BadCredentialsException("Invalid verfication code")
            }
        }

        // Build permissionSetIdentifier
        val permissionSetIdentifier = PermissionSetIdentifier(User::class.java, user.id)
        val authorities = permissionLogic.getPermissionSet(permissionSetIdentifier, user)?.grantedAuthorities ?: setOf()
        val userDetails = DatabaseUserDetails(
                permissionSetIdentifier = permissionSetIdentifier,
                authorities = authorities,
                principalPermissions = user.permissions,
                passwordHash = user.passwordHash,
                secret = user.secret,
                use2fa = user.use2fa ?: false,
                rev = user.rev,
                applicationTokens = user.applicationTokens,
                application = user.applicationTokens.filterValues { it == authentication.credentials }.keys.firstOrNull(),
                groupIdUserIdMatching = matchingUsers.map { it.id },
        )

        UsernamePasswordAuthenticationToken(
                userDetails,
                authentication,
                authorities
        )
    }

    private fun isPasswordValid(u: User, password: String): Boolean {
        if (u.applicationTokens.containsValue(password)) {
            return true
        }
        return when {
            u.passwordHash == null -> false
            u.use2fa == true && u.secret?.isNotBlank() == true -> {
                val splittedPassword = password.split("\\|").toTypedArray()
                passwordEncoder.matches(splittedPassword[0], u.passwordHash)
            }
            else -> {
                passwordEncoder.matches(password, u.passwordHash)
            }
        }
    }

    private fun isValidLong(code: String): Boolean {
        try {
            code.toLong()
        } catch (e: NumberFormatException) {
            return false
        }
        return true
    }

}
