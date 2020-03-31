package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import org.apache.http.client.utils.URIBuilder
import org.eclipse.jetty.client.HttpClient
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Service
import org.taktik.couchdb.ClientImpl
import org.taktik.couchdb.ReplicatorDocument
import org.taktik.couchdb.create
import org.taktik.couchdb.update
import org.taktik.icure.asyncdao.GroupDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.GroupLogic
import org.taktik.icure.asynclogic.ICureLogic
import org.taktik.icure.dao.replicator.Replicator
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.Replication
import org.taktik.icure.entities.base.Security
import org.taktik.icure.entities.base.User
import org.taktik.icure.entities.embed.DatabaseSynchronization
import org.taktik.icure.properties.CouchDbProperties
import java.lang.IllegalArgumentException
import java.net.URI
import java.net.URISyntaxException

@Service
class GroupLogicImpl(private val httpClient: HttpClient,
                     private val sessionLogic: AsyncSessionLogic,
                     private val groupDAO: GroupDAO,
                     private val userDAO: UserDAO,
                     private val iCureLogic: ICureLogic,
                     private val couchDbProperties: CouchDbProperties,
                     private val threadPoolTaskExecutor: TaskExecutor) : GroupLogic {

    private val dbInstanceUri = URI(couchDbProperties.url)

    override suspend fun createGroup(
            id: String,
            name: String,
            password: String,
            server: String?,
            q: Int?,
            n: Int?,
            initialReplication: Replication?
    ): Group? {
        if (id != id.replace(Regex("[^a-zA-Z0-9_\\-]"), "")) {
            throw IllegalArgumentException("Invalid id, must only contain characters of class [a-zA-Z0-9_-]")
        }
        if (password != password.replace(Regex("[^a-zA-Z0-9_\\-]"), "")) {
            throw IllegalArgumentException("Invalid password, must only contain characters of class [a-zA-Z0-9_-]")
        }

        val groupUserId = sessionLogic.getCurrentSessionContext().getGroupIdUserId()
        val groupId = userDAO.getOnFallback(dbInstanceUri, groupUserId, false)?.groupId ?: throw IllegalAccessException("Invalid user, no group")
        val userGroup = this.groupDAO.get(groupId)
        if (userGroup == null || (groupId != ADMIN_GROUP && !userGroup.isSuperAdmin)) {
            throw IllegalAccessException("No registered super admin user")
        }

        val paths = listOf(
                "icure-$id-base",
                "icure-$id-healthdata",
                "icure-$id-patient"
        )

        val dbUser = User("org.couchdb.user:$id", id, password)
        val security = Security(id)
        val group = Group(id, name, password).apply {
            superGroup = groupId
            superAdmin = groupId == ADMIN_GROUP
            server?.let { sv -> servers = couchDbProperties.altUrlsList().filter { it.contains(sv) } }
        }

        val servers = if (group.servers?.isNotEmpty() == true) group.servers else listOf(couchDbProperties.url)
        servers.forEach { server ->
            ClientImpl(httpClient, org.ektorp.http.URI.of(server).append("_users"), couchDbProperties.username!!, couchDbProperties.password!!).create(dbUser)
            paths.forEach { c ->
                val client = ClientImpl(httpClient, org.ektorp.http.URI.of(server).append(c), couchDbProperties.username!!, couchDbProperties.password!!)
                if (client.create(if (c.endsWith("-base")) 1 else q, n)) {
                    client.security(security)
                }
            }

            val client = ClientImpl(httpClient, org.ektorp.http.URI.of(server).append("_replicator"), couchDbProperties.username!!, couchDbProperties.password!!)
            initialReplication?.databaseSynchronizations?.forEach {
                if (it.source != null && it.localTarget != null) {
                    val src = it.source
                    val dst = URIBuilder(server).setUserInfo(group.id, group.password).setPath("/"+paths[it.localTarget.ordinal]).build().toString()
                    client.update(ReplicatorDocument("$id-${it.target}", null, src, dst))
                }
            }
        }

        val result = groupDAO.save(group)
        return if (result?.rev != null) result.also { iCureLogic.updateAllDesignDoc(it.id) } else null
    }

    override suspend fun getGroup(groupId: String): Group? {
        return groupDAO.get(groupId)
    }

    override fun listGroups(): Flow<Group> = flow {
        val groupUserId = sessionLogic.getCurrentSessionContext().getGroupIdUserId()
        val groupId = userDAO.getOnFallback(dbInstanceUri, groupUserId, false)?.groupId ?: throw IllegalAccessException("Invalid user, no group")

        emitAll(groupDAO.getAll().filter { it.superGroup == groupId  })
    }

    companion object {
        const val ADMIN_GROUP = "xx-f203c688-159c-4d7e-8453-c5f24dce8418"
    }
}
