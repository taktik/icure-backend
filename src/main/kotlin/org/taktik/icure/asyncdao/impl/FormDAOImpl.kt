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

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoKey
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.FormDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Form
import java.net.URI

/**
 * Created by aduchate on 02/02/13, 15:24
 */
@FlowPreview
@Repository("formDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Form' && !doc.deleted) emit(null, doc._id )}")
internal class FormDAOImpl(@Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<Form>(Form::class.java, couchDbDispatcher, idGenerator), FormDAO {

    @View(name = "by_hcparty_patientfk", map = "classpath:js/form/By_hcparty_patientfk_map.js")
    override fun findByHcPartyPatient(dbInstanceUrl: URI, groupId: String, hcPartyId: String, secretPatientKeys: List<String>): Flow<Form> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }

        val result = client.queryViewIncludeDocs<ComplexKey, String, Form>(createQuery("by_hcparty_patientfk").keys(keys).includeDocs(true)).map { it.doc }
        return result.distinctUntilChangedBy { it.id }
    }

    @View(name = "by_hcparty_parentId", map = "classpath:js/form/By_hcparty_parent_id.js")
    override fun findByHcPartyParentId(dbInstanceUrl: URI, groupId: String, hcPartyId: String, formId: String): Flow<Form> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        return client.queryViewIncludeDocs<ComplexKey, String, Form>(createQuery("by_hcparty_parentId").key(ComplexKey.of(hcPartyId, formId)).includeDocs(true)).map { it.doc }
    }

    override fun findAll(dbInstanceUrl: URI, groupId: String, pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = pagedViewQuery("all", pagination.startKey, null, pagination, false)
        // TODO SH now: endKey null ok because we don't set it in pagedViewQuery?
        return client.queryViewIncludeDocsNoKey<String, Form>(viewQuery)
    }

    @View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Form' && !doc.deleted && doc._conflicts) emit(doc._id )}")
    override fun listConflicts(dbInstanceUrl: URI, groupId: String): Flow<Form> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        return client.queryViewIncludeDocsNoValue<ComplexKey, Form>(createQuery("conflicts").includeDocs(true)).map { it.doc }
    }
}
