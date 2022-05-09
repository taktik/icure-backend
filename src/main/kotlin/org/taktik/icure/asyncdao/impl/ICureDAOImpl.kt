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

package org.taktik.icure.asyncdao.impl

import java.net.URI
import io.icure.asyncjacksonhttpclient.net.web.WebClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ClientImpl
import org.taktik.couchdb.ReplicatorResponse
import org.taktik.couchdb.entity.Indexer
import org.taktik.couchdb.entity.ReplicateCommand
import org.taktik.couchdb.entity.ReplicationTask
import org.taktik.couchdb.entity.Scheduler
import org.taktik.icure.asyncdao.ICureDAO
import org.taktik.icure.entities.embed.DatabaseSynchronization
import org.taktik.icure.properties.CouchDbProperties

@ExperimentalCoroutinesApi
@Repository("iCureDAO")
class ICureDAOImpl(val couchDbProperties: CouchDbProperties, val httpClient: WebClient) : ICureDAO {
	private val dbInstanceUrl = URI(couchDbProperties.url)

	override suspend fun getIndexingStatus(dbInstanceUri: URI): Map<String, Int> {
		val client = ClientImpl(httpClient, dbInstanceUri, couchDbProperties.username!!, couchDbProperties.password!!)
		return client.activeTasks().filterIsInstance<Indexer>().fold(mutableMapOf()) { map, at ->
			map["${at.database}/${at.design_document}"] = at.progress ?: 0
			map
		}
	}

	override suspend fun getPendingChanges(dbInstanceUri: URI): Map<DatabaseSynchronization, Long> {
		val client = ClientImpl(httpClient, dbInstanceUri, couchDbProperties.username!!, couchDbProperties.password!!)
		return client.activeTasks().filterIsInstance<ReplicationTask>().fold(mutableMapOf()) { map, at ->
			map[DatabaseSynchronization(at.source, at.target)] = at.changes_pending?.toLong() ?: 0
			map
		}
	}

	override suspend fun replicate(command: ReplicateCommand): ReplicatorResponse {
		val client = ClientImpl(httpClient, dbInstanceUrl, couchDbProperties.username!!, couchDbProperties.password!!)
		return client.replicate(command)
	}

	override suspend fun deleteReplicatorDoc(docId: String): ReplicatorResponse {
		val client = ClientImpl(httpClient, dbInstanceUrl, couchDbProperties.username!!, couchDbProperties.password!!)
		return client.deleteReplication(docId)
	}

	override suspend fun getSchedulerDocs(): Scheduler.Docs {
		val client = ClientImpl(httpClient, dbInstanceUrl, couchDbProperties.username!!, couchDbProperties.password!!)
		return client.schedulerDocs()
	}
}
