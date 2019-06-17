package org.taktik.icure.dao.replicator

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.slf4j.LoggerFactory
import org.taktik.icure.dao.UserDAO
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.User

class UserReplicator(sslContextFactory: SslContextFactory, private val userDAO: UserDAO) : AbstractReplicator<User>(sslContextFactory) {
    private val log = LoggerFactory.getLogger(AbstractReplicator::class.java)

    override val entityType: Class<User>
        get() = User::class.java

    override suspend fun getAllIds(groupId: String, dbInstanceUrl: String?): List<String> = withContext(IO) {
        userDAO.getUsersOnDb(groupId, dbInstanceUrl).map { it.id }
    }

    override suspend fun prepareReplication(group: Group) = withContext(IO) {
        userDAO.initStandardDesignDocument(group)
    }

    override suspend fun replicate(group: Group, entityIds: List<String>): List<String> {
        return entityIds.mapNotNull { userId ->
            withContext(IO) {
                val from = userDAO.getUserOnUserDb(userId, group.id, group.dbInstanceUrl(), true)
                var to: User? = userDAO.findOnFallback(group.id + ":" + userId, true)

                if (to == null) {
                    to = User()
                    to.id = group.id + ":" + from.id
                    log.warn("User {} not found on fallback: Creating !", to.id)
                }
                if (
                        to.status != from.status ||
                        to.isUse2fa != from.isUse2fa ||
                        to.passwordHash != from.passwordHash ||
                        to.healthcarePartyId != from.healthcarePartyId ||
                        (!from.isSecretEmpty && to.secret != from.secret) ||
                        to.login != from.login ||
                        to.applicationTokens != from.applicationTokens ||
                        to.groupId != group.id
                ) {
                    to.status = from.status
                    to.isUse2fa = from.isUse2fa
                    to.passwordHash = from.passwordHash
                    to.healthcarePartyId = from.healthcarePartyId
                    to.secret = if (from.isSecretEmpty) null else from.secret
                    to.login = from.login
                    to.applicationTokens = from.applicationTokens
                    to.groupId = group.id

                    userDAO.saveOnFallback(to).id
                } else null
            }
        }
    }
}
