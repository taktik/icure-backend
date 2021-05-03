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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.taktik.couchdb.annotation.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.AccessLogDAO
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.properties.CouchDbProperties


import java.time.Instant

@FlowPreview
@ExperimentalCoroutinesApi
@Repository("accessLogDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.AccessLog' && !doc.deleted) emit( null, doc._id )}")
class AccessLogDAOImpl(couchDbProperties: CouchDbProperties,
                       @Qualifier("patientCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<AccessLog>(couchDbProperties, AccessLog::class.java, couchDbDispatcher, idGenerator), AccessLogDAO {

    @View(name = "all_by_date", map = "classpath:js/accesslog/all_by_date_map.js")
    override fun list(fromEpoch: Long, toEpoch: Long, paginationOffset: PaginationOffset<Long>, descending: Boolean): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = pagedViewQuery<AccessLog, Long>("all_by_date", fromEpoch, toEpoch, paginationOffset, descending)

        return client.queryView(viewQuery, Long::class.java, String::class.java, AccessLog::class.java)
    }

    @View(name = "all_by_user_date", map = "classpath:js/accesslog/all_by_user_type_and_date_map.js")
    override fun findByUserAfterDate(userId: String, accessType: String?, startDate: Instant?, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = if (startDate == null) {
            val key = ComplexKey.of(userId, accessType, 0L)
            pagedViewQuery<AccessLog, ComplexKey>("all_by_user_date", key, null, pagination, descending)
        } else {
            val startKey = ComplexKey.of(userId, accessType, startDate.toEpochMilli())
            val endKey = ComplexKey.of(userId, accessType ?: ComplexKey.emptyObject(), java.lang.Long.MAX_VALUE)
            pagedViewQuery<AccessLog, ComplexKey>("all_by_user_date", if (descending) endKey else startKey, if (descending) startKey else endKey, pagination, descending)
        }
        return client.queryView(viewQuery, Array<String>::class.java, String::class.java, AccessLog::class.java)
    }

    @View(name = "by_hcparty_patient", map = "classpath:js/accesslog/By_hcparty_patient_map.js")
    override fun findByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<AccessLog> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }

        val viewQuery = createQuery("by_hcparty_patient").includeDocs(true).keys(keys)

        return client.queryViewIncludeDocs<Array<String>, String, AccessLog>(viewQuery).map { it.doc }
    }
}
