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

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.taktik.couchdb.annotation.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.FormDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Form
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.pagedViewQuery

/**
 * Created by aduchate on 02/02/13, 15:24
 */
@FlowPreview
@Repository("formDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Form' && !doc.deleted) emit(null, doc._id )}")
internal class FormDAOImpl(couchDbProperties: CouchDbProperties,
                           @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<Form>(couchDbProperties, Form::class.java, couchDbDispatcher, idGenerator), FormDAO {

    @View(name = "by_hcparty_patientfk", map = "classpath:js/form/By_hcparty_patientfk_map.js")
    override fun findByHcPartyPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<Form> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }

        val result = client.queryViewIncludeDocs<Array<String>, String, Form>(createQuery<Form>("by_hcparty_patientfk").keys(keys).includeDocs(true)).map { it.doc }
        return result.distinctUntilChangedBy { it.id }
    }

    @View(name = "by_hcparty_parentId", map = "classpath:js/form/By_hcparty_parent_id.js")
    override fun findByHcPartyParentId(hcPartyId: String, formId: String): Flow<Form> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocs<Array<String>, String, Form>(createQuery<Form>("by_hcparty_parentId").key(ComplexKey.of(hcPartyId, formId)).includeDocs(true)).map { it.doc }
    }

    override fun findAll(pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = pagedViewQuery<Form,String>("all", null, null, pagination, false)
        return client.queryView(viewQuery, Any::class.java, String::class.java, Form::class.java)
    }

    @View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Form' && !doc.deleted && doc._conflicts) emit(doc._id )}")
    override fun listConflicts(): Flow<Form> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocsNoValue<String, Form>(createQuery<Form>("conflicts").includeDocs(true)).map { it.doc }
    }

    @View(name = "by_externalUuid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Form' && !doc.deleted && doc.externalUuid) emit( doc.externalUuid, doc._id )}")
    override suspend fun getAllByExternalUuid(externalUuid: String): List<Form> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery<Form>("by_externalUuid")
                .key(externalUuid)
                .includeDocs(true)

        return client.queryViewIncludeDocs<String, String, Form>(viewQuery).map { it.doc /*postLoad(it.doc)*/ }.toList().sortedByDescending { it.created ?: 0 }
    }

}
