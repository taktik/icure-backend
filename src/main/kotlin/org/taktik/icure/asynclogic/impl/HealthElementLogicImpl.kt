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
package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.Option
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asyncdao.HealthElementDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Identifier

/**
 * Created by emad7105 on 24/06/2014.
 */
@ExperimentalCoroutinesApi
@Service
class HealthElementLogicImpl(private val filters: Filters,
                             private val healthElementDAO: HealthElementDAO,
                             private val uuidGenerator: UUIDGenerator,
                             private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<HealthElement, HealthElementDAO>(sessionLogic), HealthElementLogic {

    override fun getGenericDAO(): HealthElementDAO {
        return healthElementDAO
    }

    override suspend fun createHealthElement(healthElement: HealthElement) = fix(healthElement) { healthElement ->
        try { // Fetching the hcParty
            createEntities(setOf(healthElement)).firstOrNull()
        } catch (e: Exception) {
            log.error("createHealthElement: " + e.message)
            throw IllegalArgumentException("Invalid Healthcare problem", e)
        }
    }

    override suspend fun getHealthElement(healthElementId: String): HealthElement? {
        return healthElementDAO.getHealthElement(healthElementId)
    }

    override fun getHealthElements(healthElementIds: List<String>): Flow<HealthElement> = flow {
        emitAll(healthElementDAO.getEntities(healthElementIds))
    }

    override fun listHealthElementsByHcPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>) = flow {
        emitAll(healthElementDAO.listHealthElementsByHCPartyAndSecretPatientKeys(hcPartyId, secretPatientKeys))
    }

    override fun listHealthElementIdsByHcParty(hcpId: String) = flow {
        emitAll(healthElementDAO.listHealthElementsByHcParty(hcpId))
    }

    override fun listHealthElementIdsByHcPartyAndSecretPatientKeys(hcPartyId: String, secretPatinetKeys: List<String>) = flow {
        emitAll(healthElementDAO.listHealthElementIdsByHcPartyAndSecretPatientKeys(hcPartyId, secretPatinetKeys))
    }

    override suspend fun listLatestHealthElementsByHcPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): List<HealthElement> {
        return healthElementDAO.listHealthElementsByHCPartyAndSecretPatientKeys(hcPartyId, secretPatientKeys).toList()
                .groupBy { it.healthElementId }.values.mapNotNull { value -> value.maxByOrNull { it: HealthElement ->
                    it.modified ?: it.created ?: 0L
                } }
    }

    override fun listHealthElementIdsByHcPartyAndCodes(hcPartyId: String, codeType: String, codeNumber: String) = flow {
        emitAll(healthElementDAO.listHealthElementsByHcPartyAndCodes(hcPartyId, codeType, codeNumber))
    }

    override fun listHealthElementIdsByHcPartyAndTags(hcPartyId: String, tagType: String, tagCode: String) = flow {
        emitAll(healthElementDAO.listHealthElementsByHcPartyAndTags(hcPartyId, tagType, tagCode))
    }

    override fun listHealthElementsIdsByHcPartyAndIdentifiers(hcPartyId: String, identifiers: List<Identifier>) = flow {
        emitAll(healthElementDAO.listHealthElementsIdsByHcPartyAndIdentifiers(hcPartyId, identifiers))
    }

    override fun listHealthElementIdsByHcPartyAndStatus(hcPartyId: String, status: Int) = flow {
        emitAll(healthElementDAO.listHealthElementsByHcPartyAndStatus(hcPartyId, status))
    }

    override fun deleteHealthElements(ids: Set<String>): Flow<DocIdentifier> {
        return try {
            deleteEntities(ids)
        } catch (e: Exception) {
            log.error(e.message, e)
            flowOf()
        }
    }

    override suspend fun modifyHealthElement(healthElement: HealthElement) = fix(healthElement) { healthElement ->
        try {
            modifyEntities(setOf(healthElement)).firstOrNull()
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid Health problem", e)
        }
    }

    override suspend fun addDelegation(healthElementId: String, delegation: Delegation): HealthElement? {
        val healthElement = getHealthElement(healthElementId)
        return delegation.delegatedTo?.let { healthcarePartyId ->
            healthElement?.let { c -> healthElementDAO.save(c.copy(delegations = c.delegations + mapOf(
                    healthcarePartyId to setOf(delegation)
            )))}
        } ?: healthElement
    }

    override suspend fun addDelegations(healthElementId: String, delegations: List<Delegation>): HealthElement? {
        val healthElement = getHealthElement(healthElementId)
        return healthElement?.let {
            return healthElementDAO.save(it.copy(
                    delegations = it.delegations +
                            delegations.mapNotNull { d -> d.delegatedTo?.let { delegateTo -> delegateTo to setOf(d) } }
            ))
        }
    }

    override fun solveConflicts(): Flow<HealthElement> =
        healthElementDAO.listConflicts().mapNotNull {
            healthElementDAO.get(it.id, Option.CONFLICTS)?.let { healthElement ->
                healthElement.conflicts?.mapNotNull { conflictingRevision ->
                    healthElementDAO.get(
                        healthElement.id,
                        conflictingRevision
                    )
                }
                    ?.fold(healthElement) { kept, conflict ->
                        kept.merge(conflict).also { healthElementDAO.purge(conflict) }
                    }
                    ?.let { mergedHealthElement -> healthElementDAO.save(mergedHealthElement) }
            }
        }


    override fun filter(paginationOffset: PaginationOffset<Nothing>, filter: FilterChain<HealthElement>) =
        flow<ViewQueryResultEvent> {
            val ids = filters.resolve(filter.filter)
            val sortedIds = paginationOffset.takeUnless { it.startDocumentId == null }
                ?.let { paginationOffset -> // Sub-set starting from startDocId to the end (including last element)
                    ids.dropWhile { id -> id != paginationOffset.startDocumentId }
                } ?: ids

            val selectedIds =
                sortedIds.take(paginationOffset.limit + 1) // Fetching one more health element for the start key of the next page
            emitAll(healthElementDAO.findHealthElementsByIds(selectedIds))
        }

    companion object {
        private val log = LoggerFactory.getLogger(HealthElementLogicImpl::class.java)
    }
}
