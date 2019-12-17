package org.taktik.icure.asynclogic.impl

import org.ektorp.CouchDbInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.GroupDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.GroupLogic
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.Replication
import org.taktik.icure.entities.base.Security
import org.taktik.icure.entities.base.User
import org.taktik.icure.entities.embed.DatabaseSynchronization
import org.taktik.icure.logic.ReplicationLogic
import org.taktik.icure.properties.CouchDbProperties
import java.net.URI
import java.net.URISyntaxException

@Service
class GroupLogicImpl(private val sessionLogic: AsyncSessionLogic,
                     private val groupDAO: GroupDAO,
                     private val couchdbInstance: CouchDbInstance,
                     private val userLogic: UserLogic,
                     private val replicationLogic: ReplicationLogic,
                     private val couchDbProperties: CouchDbProperties,
                     private val threadPoolTaskExecutor: TaskExecutor) : GroupLogic {
    
    override suspend fun createGroup(group: Group, initialReplication: Replication): Group? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()

        val id = sessionLogic.getCurrentSessionContext().getGroupIdUserId()
        if (ADMIN_GROUP != userLogic.getUserOnFallbackDb(id).groupId) {
            throw IllegalAccessException("No registered user")
        }
        val paths = listOf(
                "icure-" + group.id + "-base",
                "icure-" + group.id + "-patient",
                "icure-" + group.id + "-healthdata"
        )
        val sanitizedDatabaseSynchronizations = initialReplication.databaseSynchronizations?.filter { ds: DatabaseSynchronization ->
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
        }
        val dbUser = User(group.id, group.password)
        couchdbInstance.createConnector("_users", false).create("org.couchdb.user:" + group.id, dbUser)
        val security = Security(group.id)
        paths.forEach { c ->
            val connector = couchdbInstance.createConnector(c, true)
            connector.create("_security", security)
        }
        val result = groupDAO.save(dbInstanceUri, groupId, group)
        initialReplication.databaseSynchronizations = sanitizedDatabaseSynchronizations
        threadPoolTaskExecutor.execute { replicationLogic.startDatabaseSynchronisations(initialReplication, false) }
        return if (result?.rev != null) result else null
    }

    override suspend fun findGroup(groupId: String): Group? {
        val (dbInstanceUri, dbGroupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return groupDAO.get(dbInstanceUri, dbGroupId, groupId) // TODO SH AD: was previously find, ok? groupId, is that the same?
    }

    companion object {
        const val ADMIN_GROUP = "xx-f203c688-159c-4d7e-8453-c5f24dce8418"
    }
}
