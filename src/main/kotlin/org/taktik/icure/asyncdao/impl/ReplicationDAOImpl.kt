/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.asyncdao.impl

import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.map
import org.ektorp.CouchDbInstance
import org.ektorp.ReplicationCommand
import org.ektorp.ReplicationTask
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Repository
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.ReplicationDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.Replication
import org.taktik.icure.entities.embed.DatabaseSynchronization
import org.taktik.icure.utils.firstOrNull
import java.net.URI

@Repository("replicationDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Replication' && !doc.deleted) emit( null, doc._id )}")
class ReplicationDAOImpl(@Qualifier("configCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, @param:Qualifier("couchdbInstance") private val couchdbInstance: CouchDbInstance, idGenerator: IDGenerator, @Qualifier("entitiesCacheManager") cacheManager: CacheManager) : GenericDAOImpl<Replication>(Replication::class.java, couchDbDispatcher, idGenerator), ReplicationDAO {

    private val gson = GsonBuilder().create()

    @View(name = "by_name", map = "function(doc) {\n" +
            "            if (doc.java_type == 'org.taktik.icure.entities.Replication' && !doc.deleted && doc.name) {\n" +
            "            emit(doc.name,doc._id);\n" +
            "}\n" +
            "}")
    override suspend fun getByName(dbInstanceUrl: URI, groupId: String, name: String): Replication? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        return client.queryViewIncludeDocs<String, String, Replication>(createQuery("by_name").key(name).includeDocs(true)).map { it.doc }.firstOrNull()
    }

    // TODO SH now: here and in ICureDAOImpl: couchdbConfig.connection.getUncached
    /*override fun getPendingChanges(dbInstanceUrl: URI, groupId: String): Map<DatabaseSynchronization, Number>? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val active_tasks = db.connection.getUncached("/_active_tasks")
        val inputStreamReader: InputStreamReader
        try {
            inputStreamReader = InputStreamReader(active_tasks.content, "UTF8")
            val json = gson.fromJson(inputStreamReader, List<*>::class.java)
            val result = HashMap<DatabaseSynchronization, Number>()
            for (status in json) {
                val source = status.get("source") as String
                val target = status.get("target") as String
                if (source != null && target != null) {
                    result[DatabaseSynchronization(source, target)] = status.get("changes_pending") as Number
                }
            }
            return result
        } catch (e: UnsupportedEncodingException) {
            //
        }

        return null
    }*/

    override fun getActiveReplications(): List<ReplicationTask> {
        return this.couchdbInstance.activeTasks.filterIsInstance<ReplicationTask>().map { task -> task as ReplicationTask }
    }

    override fun startReplication(databaseSynchronization: DatabaseSynchronization, continuous: Boolean) {
        this.couchdbInstance.replicate(ReplicationCommand.Builder().source(databaseSynchronization.source).target(databaseSynchronization.target).continuous(continuous).filter(databaseSynchronization.filter).build())
    }

    override fun cancelReplication(databaseSynchronization: DatabaseSynchronization) {
        this.couchdbInstance.replicate(ReplicationCommand.Builder().source(databaseSynchronization.source).target(databaseSynchronization.target).continuous(true).filter(databaseSynchronization.filter).cancel(true).build())
    }

    companion object {
        private val NAME = "name"
    }
}
