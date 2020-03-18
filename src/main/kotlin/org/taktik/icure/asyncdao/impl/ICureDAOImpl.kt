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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.eclipse.jetty.client.HttpClient
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ClientImpl
import org.taktik.couchdb.Indexer
import org.taktik.couchdb.ReplicationTask
import org.taktik.icure.asyncdao.ICureDAO
import org.taktik.icure.entities.embed.DatabaseSynchronization
import org.taktik.icure.properties.CouchDbProperties
import java.net.URI

@ExperimentalCoroutinesApi
@Repository("iCureDAO")
class ICureDAOImpl(couchDbProperties: CouchDbProperties, private val httpClient: HttpClient) : ICureDAO {
    private val client = ClientImpl(httpClient, org.ektorp.http.URI.of(URI(couchDbProperties.url).toString()), couchDbProperties.username!!, couchDbProperties.password!!)
    private val gson = GsonBuilder().create()

    override suspend fun getIndexingStatus(groupId: String?): Map<String, Int> {
        return client.activeTasks().filterIsInstance<Indexer>().filter { i -> groupId?.let { i.database?.contains(it) == true } ?: true }.fold(mutableMapOf()) { map, at ->
            map["${at.database}/${at.design_document}"] = at.progress ?: 0
            map
        }
    }

    override suspend fun getPendingChanges(groupId: String?): Map<DatabaseSynchronization, Long> {
        return client.activeTasks().filterIsInstance<ReplicationTask>().filter { i -> groupId?.let { i.source?.contains(it) == true || i.target?.contains(it) == true } ?: true }.fold(mutableMapOf()) { map, at ->
            map[DatabaseSynchronization(at.source, at.target)] = at.changes_pending?.toLong() ?: 0
            map
        }
    }
}
