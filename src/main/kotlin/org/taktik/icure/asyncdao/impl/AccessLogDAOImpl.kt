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
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.AccessLogDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.properties.CouchDbProperties

@FlowPreview
@ExperimentalCoroutinesApi
@Repository("accessLogDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.AccessLog' && !doc.deleted) emit( null, doc._id )}")
class AccessLogDAOImpl(
	couchDbProperties: CouchDbProperties,
	@Qualifier("patientCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator
) : GenericDAOImpl<AccessLog>(couchDbProperties, AccessLog::class.java, couchDbDispatcher, idGenerator), AccessLogDAO {

	@View(name = "all_by_date", map = "classpath:js/accesslog/all_by_date_map.js")
	override fun listAccessLogsByDate(fromEpoch: Long, toEpoch: Long, paginationOffset: PaginationOffset<Long>, descending: Boolean) = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val viewQuery = pagedViewQuery<AccessLog, Long>(client, "all_by_date", fromEpoch, toEpoch, paginationOffset, descending)

		emitAll(client.queryView(viewQuery, Long::class.java, String::class.java, AccessLog::class.java))
	}

	@View(name = "all_by_user_date", map = "classpath:js/accesslog/all_by_user_type_and_date_map.js")
	override fun findAccessLogsByUserAfterDate(userId: String, accessType: String?, startDate: Long?, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val startKey = ComplexKey.of(
			userId,
			accessType ?: ComplexKey.emptyObject().takeIf { descending },
			startDate ?: Long.MAX_VALUE.takeIf { descending } ?: 0
		)
		val endKey = ComplexKey.of(
			userId,
			accessType ?: ComplexKey.emptyObject().takeIf { !descending },
			Long.MAX_VALUE.takeIf { !descending } ?: 0
		)

		val items = client.queryView(
			pagedViewQuery<AccessLog, ComplexKey>(client, "all_by_user_date", startKey, endKey, pagination, descending),
			Array<Any>::class.java,
			String::class.java,
			AccessLog::class.java
		).toList()
		emitAll(items.asFlow())
	}

	@View(name = "by_hcparty_patient", map = "classpath:js/accesslog/By_hcparty_patient_map.js")
	override fun findAccessLogsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<AccessLog> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }

		val viewQuery = createQuery(client, "by_hcparty_patient").includeDocs(true).keys(keys)

		emitAll(client.queryViewIncludeDocs<Array<String>, String, AccessLog>(viewQuery).map { it.doc })
	}
}
