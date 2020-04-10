package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import org.eclipse.jetty.client.HttpClient
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Service
import org.taktik.couchdb.ClientImpl
import org.taktik.couchdb.create
import org.taktik.icure.asyncdao.GroupDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.GroupLogic
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.Replication
import org.taktik.icure.entities.User
import org.taktik.icure.entities.base.Security
import org.taktik.icure.entities.embed.DatabaseSynchronization
import org.taktik.icure.properties.CouchDbProperties
import java.net.URI
import java.net.URISyntaxException

@Service
class GroupLogicImpl(private val httpClient: HttpClient,
                     private val sessionLogic: AsyncSessionLogic,
                     private val groupDAO: GroupDAO,
                     private val userDAO: UserDAO,
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
        val groupUserId = sessionLogic.getCurrentSessionContext().getGroupIdUserId()
        val groupId = userDAO.getOnFallback(dbInstanceUri, groupUserId, false)?.groupId ?: throw IllegalAccessException("Invalid user, no group")
        val userGroup = this.groupDAO.get(groupId)
        if (userGroup == null || (groupId != ADMIN_GROUP && !userGroup.isSuperAdmin)) {
            throw IllegalAccessException("No registered super admin user")
        }
        val paths = listOf(
                "icure-$id-base",
                "icure-$id-patient",
                "icure-$id-healthdata"
        )
        val sanitizedDatabaseSynchronizations = initialReplication?.databaseSynchronizations?.filter { ds: DatabaseSynchronization ->
            try {
                val couch = URI(couchDbProperties.url)
                val dest = URI(ds.target)
                require(dest.port == 443 || dest.port == 5984 || dest.port == -1 && dest.scheme == "https") { "Cannot start replication: invalid destination port (must be 5984 or 443)" }
                require(dest.host == couch.host || dest.host == "127.0.0.1" || dest.host == "localhost") { "Cannot start replication: invalid destination " + dest.host + "(must be " + couch.host + " or localhost/127.0.0.1 )" }
                require(!paths.stream().noneMatch { p: String -> dest.path.startsWith("/$p") }) { "Cannot start replication: invalid destination path " + dest.path + " ( must match start with any of /" + java.lang.String.join(", /", paths) }
                true
            } catch (e: URISyntaxException) {
                throw IllegalArgumentException("Cannot start replication: invalid target")
            }
        } ?: listOf()
        val dbUser = User("org.couchdb.user:$id", id, password)
        val security = Security(id)
        val group = Group(id, name, password).apply {
            superGroup = groupId
            superAdmin = groupId == ADMIN_GROUP
            server?.let { sv -> servers = couchDbProperties.altUrlsList().filter { it.contains(sv) } }
        }

        val servers = if (group.servers?.isNotEmpty() == true) group.servers else listOf(couchDbProperties.url)
        servers.forEach {
            ClientImpl(httpClient, org.ektorp.http.URI.of(it).append("_users"), couchDbProperties.username!!, couchDbProperties.password!!).create(dbUser)
            paths.forEach { c ->
                val client = ClientImpl(httpClient, org.ektorp.http.URI.of(it).append(c), couchDbProperties.username!!, couchDbProperties.password!!)
                if (client.create(if (c.endsWith("-base")) 1 else q, n)) {
                    client.security(security)
                }
            }
        }

        val result = groupDAO.save(group)
        return if (result?.rev != null) result else null
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
