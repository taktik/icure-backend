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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.withIndex
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.HealthcarePartyDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncCacheManager

/** Created by aduchate on 18/07/13, 13:36  */
@Repository("healthcarePartyDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) emit( doc.lastName, doc._id )}")
internal class HealthcarePartyDAOImpl(
	couchDbProperties: CouchDbProperties,
	@Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator,
	@Qualifier("asyncCacheManager") asyncCacheManager: AsyncCacheManager
) : CachedDAOImpl<HealthcareParty>(HealthcareParty::class.java, couchDbProperties, couchDbDispatcher, idGenerator, asyncCacheManager), HealthcarePartyDAO {

	@View(name = "by_nihii", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) emit(doc.nihii.substr(0,8), doc._id )}")
	override fun listHealthcarePartiesByNihii(nihii: String?): Flow<HealthcareParty> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		emitAll(
			if (nihii == null) {
				flowOf()
			} else {
				val key = if (nihii.length > 8) nihii.substring(0, 8) else nihii
				client.queryViewIncludeDocs<String, String, HealthcareParty>(createQuery(client, "by_nihii").key(key).includeDocs(true)).map { it.doc }
			}
		)
	}

	@View(name = "by_ssin", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) emit(doc.ssin, doc._id )}")
	override fun listHealthcarePartiesBySsin(ssin: String): Flow<HealthcareParty> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		emitAll(client.queryViewIncludeDocs<String, String, HealthcareParty>(createQuery(client, "by_ssin").key(ssin).includeDocs(true)).map { it.doc })
	}

	@View(name = "by_speciality_postcode", map = "classpath:js/healthcareparty/By_speciality_postcode.js")
	override fun listHealthcarePartiesBySpecialityAndPostcode(type: String, spec: String, firstCode: String, lastCode: String): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val viewQuery = pagedViewQuery<HealthcareParty, ComplexKey>(client, "by_speciality_postcode", ComplexKey.of(type, spec, firstCode), ComplexKey.of(type, spec, lastCode), PaginationOffset(10000), false)
		emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, HealthcareParty::class.java))
	}

	@View(name = "allForPagination", map = "classpath:js/healthcareparty/All_for_pagination.js")
	override fun findHealthCareParties(pagination: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val viewQuery = pagedViewQuery<HealthcareParty, String>(
			client, "allForPagination", if (pagination.startKey != null) pagination.startKey.toString() else if (desc != null && desc) "\ufff0" else "\u0000", if (desc != null && desc) "\u0000" else "\ufff0", pagination,
			desc
				?: false
		)

		emitAll(client.queryView(viewQuery, String::class.java, String::class.java, HealthcareParty::class.java))
	}

	@View(name = "by_name", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) emit(doc.name, doc._id )}")
	override fun listHealthcarePartiesByName(name: String): Flow<HealthcareParty> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		emitAll(client.queryViewIncludeDocs<String, String, HealthcareParty>(createQuery(client, "by_name").key(name).includeDocs(true)).map { it.doc })
	}

	@View(name = "by_ssin_or_nihii", map = "classpath:js/healthcareparty/By_Ssin_or_Nihii.js")
	override fun findHealthcarePartiesBySsinOrNihii(searchValue: String?, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val isDesc = desc != null && desc
		val from = if (isDesc) searchValue!! + "\ufff0" else searchValue
		val to = if (searchValue != null) if (isDesc) searchValue else searchValue + "\ufff0" else if (isDesc) null else "\ufff0"

		val viewQuery = pagedViewQuery<HealthcareParty, String>(client, "by_ssin_or_nihii", from, to, offset, isDesc)

		emitAll(client.queryView(viewQuery, String::class.java, String::class.java, HealthcareParty::class.java))
	}

	@View(name = "by_hcParty_name", map = "classpath:js/healthcareparty/By_hcparty_name_map.js")
	override fun findHealthcarePartiesByHcPartyNameContainsFuzzy(searchString: String?, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val r = if (searchString != null) StringUtils.sanitizeString(searchString) else null
		val isDesc = desc != null && desc
		val from = if (offset.startKey == null) if (isDesc) r!! + "\ufff0" else r else offset.startKey as String
		val to = if (r != null) if (isDesc) r else r + "\ufff0" else if (isDesc) null else "\ufff0"

		val viewQuery = pagedViewQuery<HealthcareParty, String>(client, "by_hcParty_name", from, to, offset, isDesc)

		emitAll(client.queryView(viewQuery, String::class.java, String::class.java, HealthcareParty::class.java))
	}

	override fun listHealthcareParties(searchString: String, offset: Int, limit: Int): Flow<HealthcareParty> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		// TODO test
		val r = StringUtils.sanitizeString(searchString)
		val from = ComplexKey.of(r)
		val to = ComplexKey.of(r + "\ufff0")

		emitAll(
			client.queryViewIncludeDocs<String, String, HealthcareParty>(createQuery(client, "by_hcParty_name").startKey(from).endKey(to).includeDocs(true).limit(limit + offset)).map { it.doc }
				.withIndex().filter { it.index >= offset }.map { it.value }
		)
	}

	@View(name = "by_hcparty_delegate_keys", map = "classpath:js/healthcareparty/By_hcparty_delegate_keys_map.js")
	override suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String> {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		//Not transactional aware
		val result = client.queryView<String, List<String>>(createQuery(client, "by_hcparty_delegate_keys").key(healthcarePartyId).includeDocs(false)).mapNotNull { it.value }

		val resultMap = HashMap<String, String>()
		result.collect {
			resultMap[it[0]] = it[1]
		}
		return resultMap
	}

	@View(name = "by_parent", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted && doc.parentId) emit(doc.parentId, doc._id)}")
	override fun listHealthcarePartiesByParentId(parentId: String): Flow<HealthcareParty> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		emitAll(client.queryViewIncludeDocs<String, String, HealthcareParty>(createQuery(client, "by_parent").key(parentId).includeDocs(true)).map { it.doc })
	}

	override fun findHealthcarePartiesByIds(hcpIds: Flow<String>): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(client.getForPagination(hcpIds, HealthcareParty::class.java))
	}
}
