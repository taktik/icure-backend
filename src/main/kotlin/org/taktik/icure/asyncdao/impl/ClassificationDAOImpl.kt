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
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.ClassificationDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.Classification
import org.taktik.icure.utils.createQuery
import java.net.URI

/**
 * Created by dlm on 16-07-18
 */
@FlowPreview
@Repository("classificationDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Classification' && !doc.deleted) emit( doc.patientId, doc._id )}")
internal class ClassificationDAOImpl(@Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericIcureDAOImpl<Classification>(Classification::class.java, couchDbDispatcher, idGenerator, mapper), ClassificationDAO {

    override fun findByPatient(dbInstanceUrl: URI, groupId: String, patientId: String): Flow<Classification> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery<Classification>("all").includeDocs(true).key(patientId)
        return client.queryViewIncludeDocs<String, String, Classification>(viewQuery).map { it.doc }
    }

    override suspend fun getClassification(dbInstanceUrl: URI, groupId: String, classificationId: String): Classification? {
        return get(dbInstanceUrl, groupId, classificationId)
    }

    @View(name = "by_hcparty_patient", map = "classpath:js/classification/By_hcparty_patient_map.js")
    override fun findByHCPartySecretPatientKeys(dbInstanceUrl: URI, groupId: String, hcPartyId: String, secretPatientKeys: List<String>): Flow<Classification> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }

        val viewQuery = createQuery<Classification>("by_hcparty_patient").includeDocs(true).keys(keys)
        return client.queryViewIncludeDocs<ComplexKey, String, Classification>(viewQuery).map { it.doc }.distinctUntilChangedBy { it.id }
    }
}
