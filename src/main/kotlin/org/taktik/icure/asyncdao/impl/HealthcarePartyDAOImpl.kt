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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.withIndex
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.HealthcarePartyDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.pagedViewQuery
import java.net.URI
import java.util.*

/** Created by aduchate on 18/07/13, 13:36  */
@Repository("healthcarePartyDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) emit( doc.lastName, doc._id )}")
internal class HealthcarePartyDAOImpl(couchDbProperties: CouchDbProperties,
                                      @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, @Qualifier("asyncCacheManager") asyncCacheManager: AsyncCacheManager) : CachedDAOImpl<HealthcareParty>(HealthcareParty::class.java, couchDbProperties, couchDbDispatcher, idGenerator, asyncCacheManager), HealthcarePartyDAO {

    @View(name = "by_nihii", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) emit(doc.nihii.substr(0,8), doc._id )}")
    override fun findByNihii(nihii: String?): Flow<HealthcareParty> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return if (nihii == null) {
            flowOf()
        } else {
            val key = if (nihii.length > 8) nihii.substring(0, 8) else nihii
            client.queryViewIncludeDocs<String, String, HealthcareParty>(createQuery<HealthcareParty>("by_nihii").key(key).includeDocs(true)).map { it.doc }
        }
    }

    @View(name = "by_ssin", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) emit(doc.ssin, doc._id )}")
    override fun findBySsin(ssin: String): Flow<HealthcareParty> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocs<String, String, HealthcareParty>(createQuery<HealthcareParty>("by_ssin").key(ssin).includeDocs(true)).map { it.doc }
    }

    @View(name = "by_speciality_postcode", map = "classpath:js/healthcareparty/By_speciality_postcode.js")
    override fun findBySpecialityPostcode(type: String, spec: String, firstCode: String, lastCode: String): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = pagedViewQuery<HealthcareParty, ComplexKey>("by_speciality_postcode", ComplexKey.of(type, spec, firstCode), ComplexKey.of(type, spec, lastCode), PaginationOffset(10000), false)
        return client.queryView(viewQuery, Array<String>::class.java, String::class.java, HealthcareParty::class.java)
    }

    @View(name = "allForPagination", map = "classpath:js/healthcareparty/All_for_pagination.js")
    override fun listHealthCareParties(pagination: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = pagedViewQuery<HealthcareParty, String>("allForPagination", if (pagination.startKey != null) pagination.startKey.toString() else if (desc != null && desc) "\ufff0" else "\u0000", if (desc != null && desc) "\u0000" else "\ufff0", pagination, desc
                ?: false)

        return client.queryView(viewQuery, String::class.java, String::class.java, HealthcareParty::class.java)
    }

    @View(name = "by_name", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) emit(doc.name, doc._id )}")
    override fun findByName(name: String): Flow<HealthcareParty> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocs<String, String, HealthcareParty>(createQuery<HealthcareParty>("by_name").key(name).includeDocs(true)).map { it.doc }
    }

    @View(name = "by_ssin_or_nihii", map = "classpath:js/healthcareparty/By_Ssin_or_Nihii.js")
    override fun findBySsinOrNihii(searchValue: String?, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val isDesc = desc != null && desc
        val from = if (isDesc) searchValue!! + "\ufff0" else searchValue
        val to = if (searchValue != null) if (isDesc) searchValue else searchValue + "\ufff0" else if (isDesc) null else "\ufff0"

        val viewQuery = pagedViewQuery<HealthcareParty, String>("by_ssin_or_nihii", from, to, offset, isDesc)

        return client.queryView(viewQuery, String::class.java, String::class.java, HealthcareParty::class.java)
    }

    @View(name = "by_hcParty_name", map = "classpath:js/healthcareparty/By_hcparty_name_map.js")
    override fun findByHcPartyNameContainsFuzzy(searchString: String?, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val r = if (searchString != null) StringUtils.sanitizeString(searchString) else null
        val isDesc = desc != null && desc
        val from = if (offset.startKey == null) if (isDesc) r!! + "\ufff0" else r else offset.startKey as String
        val to = if (r != null) if (isDesc) r else r + "\ufff0" else if (isDesc) null else "\ufff0"

        val viewQuery = pagedViewQuery<HealthcareParty, String>("by_hcParty_name", from, to, offset, isDesc)

        return client.queryView(viewQuery, String::class.java, String::class.java, HealthcareParty::class.java)
    }

    override fun findHealthcareParties(searchString: String, offset: Int, limit: Int): Flow<HealthcareParty> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        // TODO test
        val r = StringUtils.sanitizeString(searchString)
        val from = ComplexKey.of(r)
        val to = ComplexKey.of(r + "\ufff0")

        return client.queryViewIncludeDocs<String, String, HealthcareParty>(createQuery<HealthcareParty>("by_hcParty_name").startKey(from).endKey(to).includeDocs(true).limit(limit + offset)).map { it.doc }
                .withIndex().filter { it.index >= offset }.map { it.value }
    }

    @View(name = "by_hcparty_delegate_keys", map = "classpath:js/healthcareparty/By_hcparty_delegate_keys_map.js")
    override suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        //Not transactional aware
        val result = client.queryView<String, List<String>>(createQuery<HealthcareParty>("by_hcparty_delegate_keys").key(healthcarePartyId).includeDocs(false)).mapNotNull { it.value }

        val resultMap = HashMap<String, String>()
        result.collect {
            resultMap[it[0]] = it[1]
        }
        return resultMap
    }

    @View(name = "by_parent", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted && doc.parentId) emit(doc.parentId, doc._id)}")
    override fun findByParentId(parentId: String): Flow<HealthcareParty> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocs<String, String, HealthcareParty>(createQuery<HealthcareParty>("by_parent").key(parentId).includeDocs(true)).map { it.doc }
    }
}
