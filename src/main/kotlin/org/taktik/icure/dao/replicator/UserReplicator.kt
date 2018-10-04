package org.taktik.icure.dao.replicator

import com.hazelcast.core.HazelcastInstance
import org.taktik.icure.dao.UserDAO
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.User

import javax.ws.rs.core.Context

/**
 * @author Bernard Paulus - 13/03/2017
 */
class UserReplicator(hazelcast: HazelcastInstance, private var userDAO: UserDAO?) : AbstractReplicator<User>(hazelcast) {

    override val entityType: Class<User>
        get() = User::class.java

    override fun getAllIds(groupId: String): List<String> {
        return userDAO!!.getUsersOnDb(groupId).map { it.id }
    }

    override fun prepareReplication(group: Group) {
        userDAO!!.initStandardDesignDocument(group.id)
    }

    override fun replicate(group: Group, entityIds: List<String>): List<String> {
        return entityIds.map { id ->
            val from = userDAO!!.getUserOnUserDb(id, group.id, true)
            var to: User? = userDAO!!.findOnFallback(group.id+":"+id)

            if (to == null) {
                to = User()
                to.id = group.id+":"+from.id
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

                userDAO!!.saveOnFallback(to).id
            } else null
        }.filterNotNull()
    }

    @Context
    fun setUserDAO(userDAO: UserDAO) {
        this.userDAO = userDAO
    }
}
