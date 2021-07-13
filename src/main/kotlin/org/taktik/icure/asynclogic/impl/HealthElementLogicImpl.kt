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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.HealthElementDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.couchdb.entity.Option
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.utils.firstOrNull

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
        emitAll(healthElementDAO.getList(healthElementIds))
    }

    override fun findByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<HealthElement> = flow {
        emitAll(healthElementDAO.findByHCPartySecretPatientKeys(hcPartyId, secretPatientKeys))
    }

    override suspend fun findLatestByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): List<HealthElement> {
        return healthElementDAO.findByHCPartySecretPatientKeys(hcPartyId, secretPatientKeys).toList()
                .groupBy { it.healthElementId }.values.mapNotNull { value -> value.maxBy { it.modified ?: it.created ?: 0L } }
    }

    override fun findByHCPartyAndCodes(hcPartyId: String, codeType: String, codeNumber: String) = flow {
        emitAll(healthElementDAO.findByHCPartyAndCodes(hcPartyId, codeType, codeNumber))
    }

    override fun findByHCPartyAndTags(hcPartyId: String, tagType: String, tagCode: String) = flow {
        emitAll(healthElementDAO.findByHCPartyAndTags(hcPartyId, tagType, tagCode))
    }

    override fun findByHCPartyAndStatus(hcPartyId: String, status: Int): Flow<String> = flow {
        emitAll(healthElementDAO.findByHCPartyAndStatus(hcPartyId, status))
    }

    override fun deleteHealthElements(ids: Set<String>): Flow<DocIdentifier> {
        return try {
            deleteByIds(ids)
        } catch (e: Exception) {
            log.error(e.message, e)
            flowOf()
        }
    }

    override suspend fun modifyHealthElement(healthElement: HealthElement) = fix(healthElement) { healthElement ->
        try {
            updateEntities(setOf(healthElement)).firstOrNull()
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
           healthElementDAO.listConflicts().mapNotNull {healthElementDAO.get(it.id, Option.CONFLICTS)?.let {healthElement ->
               healthElement.conflicts?.mapNotNull { conflictingRevision ->healthElementDAO.get(healthElement.id, conflictingRevision) }
                        ?.fold(healthElement) { kept, conflict -> kept.merge(conflict).also {healthElementDAO.purge(conflict) } }
                        ?.let { mergedHealthElement ->healthElementDAO.save(mergedHealthElement) }
            } }


    override fun filter(filter: FilterChain<HealthElement>) = flow<HealthElement> {
        val ids = filters.resolve(filter.filter).toList()
        val healthElements = getHealthElements(ids) //TODO MBB implement get elements flow
        val predicate = filter.predicate
        emitAll(if (predicate != null) healthElements.filter { predicate.apply(it) } else healthElements)
    }

    companion object {
        private val log = LoggerFactory.getLogger(HealthElementLogicImpl::class.java)
    }
}
