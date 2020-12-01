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
import kotlinx.coroutines.flow.map
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.ClassificationTemplateDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.pagedViewQuery
import org.taktik.icure.utils.subsequentDistinctById
import java.net.URI
import java.util.*

/**
 * Created by dlm on 16-07-18
 */
@Repository("classificationTemplateDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.ClassificationTemplate' && !doc.deleted) emit( doc.label, doc._id )}")
internal class ClassificationTemplateDAOImpl(couchDbProperties: CouchDbProperties,
                                             @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericIcureDAOImpl<ClassificationTemplate>(ClassificationTemplate::class.java, couchDbProperties, couchDbDispatcher, idGenerator), ClassificationTemplateDAO {

    override suspend fun getClassificationTemplate(classificationTemplateId: String): ClassificationTemplate? {
        return get(classificationTemplateId)
    }

    @View(name = "by_hcparty_patient", map = "classpath:js/classificationtemplate/By_hcparty_patient_map.js")
    override fun findByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: ArrayList<String>): Flow<ClassificationTemplate> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val keys = secretPatientKeys.map { ComplexKey.of(hcPartyId, it) }

        val viewQuery = createQuery<ClassificationTemplate>("by_hcparty_patient").includeDocs(true).keys(keys)
        return client.queryViewIncludeDocs<ComplexKey, String, ClassificationTemplate>(viewQuery).map { it.doc }.subsequentDistinctById()
    }

    override fun listClassificationTemplates(paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = pagedViewQuery<ClassificationTemplate,String>("all", null, "\ufff0", paginationOffset, false)
        return client.queryView(viewQuery, String::class.java, String::class.java, ClassificationTemplate::class.java)
    }
}
