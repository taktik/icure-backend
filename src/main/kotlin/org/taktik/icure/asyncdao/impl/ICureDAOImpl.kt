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
import org.ektorp.CouchDbConnector
import org.ektorp.CouchDbInstance
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ClientImpl
import org.taktik.couchdb.Indexer
import org.taktik.couchdb.ReplicationTask
import org.taktik.icure.asyncdao.ICureDAO
import org.taktik.icure.properties.CouchDbProperties
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.URI
import java.util.*

@ExperimentalCoroutinesApi
@Repository("iCureDAO")
class ICureDAOImpl(couchDbProperties: CouchDbProperties, private val httpClient: HttpClient, private val couchdbInstance: CouchDbInstance) : ICureDAO {
    private val client = ClientImpl(httpClient, org.ektorp.http.URI.of(URI(couchDbProperties.url).toString()), couchDbProperties.username!!, couchDbProperties.password!!)
    private val gson = GsonBuilder().create()

    override suspend fun getIndexingStatus(groupId: String?): Map<String, Int> {
        return client.activeTasks().filterIsInstance<Indexer>().fold(mutableMapOf<String,Int>()) { map, at ->
            map["${at.databaseName}/${at.designDocumentId}"] = at.progress
            map
        }
    }
}
