import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.asMono
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
import org.taktik.icure.asyncdao.GroupDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.PermissionLogic
import org.taktik.icure.constants.Users
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.User
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.security.PermissionSetIdentifier
import org.taktik.icure.security.database.DatabaseUserDetails
import reactor.core.publisher.Mono
import java.net.URI
import java.util.*
import java.util.stream.Collectors

@ExperimentalCoroutinesApi
class CustomAuthenticationProvider(
        couchDbProperties: CouchDbProperties,
        private val userDAO: UserDAO, //prevent cyclic dependnecies
        private val groupDAO: GroupDAO,
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
        val isFullToken = username.matches(Regex("(.+/)([0-9a-zA-Z]{8}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{12}|idUser_idLogin_.+)"))
        val isPartialToken = username.matches(Regex("[0-9a-zA-Z]{8}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{12}|idUser_idLogin_.+"))

        val usersFlow = when {
            isFullToken -> {
                flowOf(userDAO.getOnFallback(dbInstanceUri, username.replace('/', ':'), false))
            }
            isPartialToken -> {
                userDAO.getUsersByPartialIdOnFallback(dbInstanceUri, username)
            }
            else -> {
                userDAO.findByUsernameOnFallback(dbInstanceUri, username)
            }
        }

        val users = usersFlow
                .filterNotNull()
                .filter { it.status == Users.Status.ACTIVE }
                .toList()
                .sortedBy { it.id }

        var user: User? = null
        var groupId: String? = null
        var group: Group? = null

        val matchingUsers = mutableListOf<User>()
        val password: String = authentication.credentials.toString()

        for (userOnFallbackDb in users) {
            val userId = if (userOnFallbackDb.id.contains(":")) userOnFallbackDb.id.split(":")[1] else userOnFallbackDb.id
            val gId = userOnFallbackDb.groupId

            if (gId != null) {
                val g = groupDAO.get(gId)
                val candidate = g?.dbInstanceUrl()?.let { userDAO.findUserOnUserDb(URI.create(it), gId, userId, false) }
                if (candidate != null && isPasswordValid(candidate, password)) {
                    if (groupId == null) {
                        user = candidate
                        groupId = gId
                        group = g
                    }
                    matchingUsers.add(userOnFallbackDb)
                } else {
                    log.warn("No match for " + userOnFallbackDb.id + ":" + gId)
                }
            } else {
                log.warn("No group for " + userOnFallbackDb.id)
            }
        }

        if (user == null) {
            log.warn("Invalid username or password for user " + username + ", no user matched out of " + users.size + " candidates")
            throw BadCredentialsException("Invalid username or password")
        }

        if (user.isUse2fa == true && !user.isSecretEmpty && !user.applicationTokens.containsValue(password)) {
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

        val permissionSet = permissionLogic.getPermissionSet(permissionSetIdentifier)
        val authorities = if (permissionSet == null) HashSet() else permissionSet.grantedAuthorities

        val userDetails = DatabaseUserDetails(permissionSetIdentifier, authorities, user.passwordHash, user.secret, user.isUse2fa)
        if (group != null) {
            userDetails.dbInstanceUrl = group.dbInstanceUrl()
        }
        userDetails.groupId = groupId
        userDetails.rev = user.rev
        userDetails.applicationTokens = user.applicationTokens
        userDetails.groupIdUserIdMatching = matchingUsers.stream().map { obj: User -> obj.id }.collect(Collectors.toList())

        for ((key, value) in user.applicationTokens) {
            if (value == authentication.getCredentials()) {
                userDetails.application = key
            }
        }

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
        return if (u.isUse2fa != null && u.isUse2fa!! && !u.isSecretEmpty) {
            val splittedPassword = password.split("\\|").toTypedArray()
            passwordEncoder.matches(splittedPassword[0], u.passwordHash)
        } else {
            passwordEncoder.matches(password, u.passwordHash)
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
