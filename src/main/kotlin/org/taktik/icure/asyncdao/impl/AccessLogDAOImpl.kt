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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.AccessLogDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.AccessLog
import java.net.URI
import java.time.Instant

@FlowPreview
@ExperimentalCoroutinesApi
@Repository("accessLogDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.AccessLog' && !doc.deleted) emit( null, doc._id )}")
class AccessLogDAOImpl(@Qualifier("patientCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<AccessLog>(AccessLog::class.java, couchDbDispatcher, idGenerator), AccessLogDAO {

    @View(name = "all_by_date", map = "classpath:js/accesslog/all_by_date_map.js")
    override fun list(dbInstanceUrl: URI, groupId: String, paginationOffset: PaginationOffset<Long>, descending: Boolean): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val viewQuery = pagedViewQuery(client, "all_by_date", paginationOffset.startKey, null, paginationOffset, descending)

        return client.queryView(viewQuery, Long::class.java, String::class.java, AccessLog::class.java)
    }

    @View(name = "all_by_user_date", map = "classpath:js/accesslog/all_by_user_type_and_date_map.js")
    override fun findByUserAfterDate(dbInstanceUrl: URI, groupId: String, userId: String, accessType: String, startDate: Instant?, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val viewQuery = if (startDate == null) {
            val key = if (pagination.startKey == null) ComplexKey.of(userId, accessType, 0L) else ComplexKey.of(listOf(pagination.startKey))
            pagedViewQuery(client, "all_by_user_date", key, null, pagination, descending)
        } else {
            val startKey = if (pagination.startKey == null) ComplexKey.of(userId, accessType, startDate.toEpochMilli()) else pagination.startKey as ComplexKey
            val endKey = ComplexKey.of(userId, accessType, java.lang.Long.MAX_VALUE)
            pagedViewQuery(client, "all_by_user_date", if (descending) endKey else startKey, if (descending) startKey else endKey, pagination, descending)
        }
        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, AccessLog::class.java)
    }

    @View(name = "by_hcparty_patient", map = "classpath:js/accesslog/By_hcparty_patient_map.js")
    override fun findByHCPartySecretPatientKeys(dbInstanceUrl: URI, groupId: String, hcPartyId: String, secretPatientKeys: List<String>): Flow<AccessLog> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }

        val viewQuery = createQuery("by_hcparty_patient").includeDocs(true).keys(keys)

        return client.queryViewIncludeDocs<ComplexKey, String, AccessLog>(viewQuery).map { it.doc }
    }
}
