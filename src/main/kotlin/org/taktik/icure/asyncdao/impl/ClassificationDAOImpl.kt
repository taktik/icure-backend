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
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.ClassificationDAO
import org.taktik.icure.entities.Classification
import org.taktik.icure.properties.CouchDbProperties


/**
 * Created by dlm on 16-07-18
 */
@FlowPreview
@Repository("classificationDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Classification' && !doc.deleted) emit( doc.patientId, doc._id )}")
internal class ClassificationDAOImpl(couchDbProperties: CouchDbProperties,
                                     @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericIcureDAOImpl<Classification>(Classification::class.java, couchDbProperties, couchDbDispatcher, idGenerator), ClassificationDAO {

    override fun listClassificationByPatient(patientId: String): Flow<Classification> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery(client, "all").includeDocs(true).key(patientId)
        emitAll(client.queryViewIncludeDocs<String, String, Classification>(viewQuery).map { it.doc })
    }

    override suspend fun getClassification(classificationId: String): Classification? {
        return get(classificationId)
    }

    @View(name = "by_hcparty_patient", map = "classpath:js/classification/By_hcparty_patient_map.js")
    override fun listClassificationsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<Classification> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }

        val viewQuery = createQuery(client, "by_hcparty_patient").includeDocs(true).keys(keys)
        emitAll(client.queryViewIncludeDocs<ComplexKey, String, Classification>(viewQuery).map { it.doc }.distinctUntilChangedBy { it.id })
    }
}
