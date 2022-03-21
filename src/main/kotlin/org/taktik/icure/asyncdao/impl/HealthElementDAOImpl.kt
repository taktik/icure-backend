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
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.HealthElementDAO
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.distinctBy

/**
 * Created by aduchate on 18/07/13, 13:36
 */
@ExperimentalCoroutinesApi
@Repository("healthElementDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthElement' && !doc.deleted) emit( null, doc._id )}")
internal class HealthElementDAOImpl(couchDbProperties: CouchDbProperties,
                                    @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<HealthElement>(couchDbProperties, HealthElement::class.java, couchDbDispatcher, idGenerator), HealthElementDAO {

    @View(name = "by_hcparty", map = "classpath:js/healthelement/By_hcparty_map.js")
    override fun listHealthElementsByHcParty(hcPartyId: String) = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        emitAll(client.queryView<Array<String>, String>(createQuery(client, "by_hcparty").key(hcPartyId).includeDocs(false)).mapNotNull { it.value })
    }

    override fun listHealthElementIdsByHcPartyAndSecretPatientKeys(
            hcPartyId: String,
            secretPatientKeys: List<String>
    ) = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }

        emitAll(client.queryView<Array<String>, String>(createQuery(client, "by_hcparty_patient").keys(keys).includeDocs(false)).distinctBy { it.id }.map { it.id })
    }

    @View(name = "by_hcparty_and_codes", map = "classpath:js/healthelement/By_hcparty_code_map.js")
    override fun listHealthElementsByHcPartyAndCodes(hcPartyId: String, codeType: String, codeNumber: String): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        emitAll(client.queryView<Array<String>, String>(createQuery(client, "by_hcparty_and_codes").key(ComplexKey.of(hcPartyId, "$codeType:$codeNumber")).includeDocs(false)).mapNotNull { it.value })
    }

    @View(name = "by_hcparty_and_tags", map = "classpath:js/healthelement/By_hcparty_tag_map.js")
    override fun listHealthElementsByHcPartyAndTags(hcPartyId: String, tagType: String, tagCode: String) = flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.queryView<Array<String>, String>(createQuery(client, "by_hcparty_and_tags").key(ComplexKey.of(hcPartyId, "$tagType:$tagCode")).includeDocs(false)).mapNotNull { it.value })
   }

    @View(name = "by_hcparty_and_status", map = "classpath:js/healthelement/By_hcparty_status_map.js")
    override fun listHealthElementsByHcPartyAndStatus(hcPartyId: String, status: Int?) = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        emitAll(client.queryView<Array<String>, String>(createQuery(client, "by_hcparty_and_status").key(ComplexKey.of(hcPartyId, status)).includeDocs(false)).mapNotNull { it.value })
    }

    @View(name = "by_hcparty_and_identifiers", map = "classpath:js/healthelement/By_hcparty_identifiers_map.js")
    override fun listHealthElementsIdsByHcPartyAndIdentifiers(hcPartyId: String, identifiers: List<Identifier>) = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val queryView = createQuery(client, "by_hcparty_and_identifiers")
            .keys(identifiers.map {
                ComplexKey.of(hcPartyId, it.system, it.value)
            })

        emitAll(client.queryView<ComplexKey, String>(queryView)
            .mapNotNull {
                if (it.key == null || it.key!!.components.size < 3) {
                    null
                }
                else {
                    it.id
                }
            }
        )
    }

    @View(name = "by_planOfActionId", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthElement' && !doc.deleted) {\n" +
            "            for(var i= 0;i<doc.plansOfAction.length;i++) {\n" +
            "        emit([doc.plansOfAction[i].id], doc._id);\n" +
            "    }\n" +
            "}}")
    override suspend fun getHealthElementByPlanOfActionId(planOfActionId: String): HealthElement? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocs<ComplexKey, String, HealthElement>(createQuery(client, "by_planOfActionId").key(planOfActionId).includeDocs(true)).map { it.doc }.firstOrNull()
    }

    override suspend fun getHealthElement(healthElementId: String): HealthElement? {
        return get(healthElementId)
    }

    @View(name = "by_hcparty_patient", map = "classpath:js/healthelement/By_hcparty_patient_map.js")
    override fun listHealthElementsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<HealthElement> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }

        val result = client.queryViewIncludeDocs<Array<String>, String, HealthElement>(
            createQuery(
                client,
                "by_hcparty_patient"
            ).keys(keys).includeDocs(true)
        ).map { it.doc }
        emitAll(result.distinctUntilChangedBy { it.id })
    }

    @View(
        name = "conflicts",
        map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthElement' && !doc.deleted && doc._conflicts) emit(doc._id )}"
    )
    override fun listConflicts(): Flow<HealthElement> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        emitAll(
            client.queryViewIncludeDocsNoValue<ComplexKey, HealthElement>(
                createQuery(
                    client,
                    "conflicts"
                ).includeDocs(true)
            ).map { it.doc })
    }

    override fun findHealthElementsByIds(healthElementIds: Flow<String>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.getForPagination(healthElementIds, HealthElement::class.java))
    }
}
