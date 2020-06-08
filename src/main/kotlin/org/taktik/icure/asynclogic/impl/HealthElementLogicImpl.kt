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
package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.HealthElementDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.dao.Option
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.dto.filter.chain.FilterChain
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.utils.FuzzyValues
import org.taktik.icure.utils.firstOrNull
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.function.Consumer

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
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return healthElementDAO.getHealthElement(dbInstanceUri, groupId, healthElementId)
    }

    override fun getHealthElements(healthElementIds: List<String>): Flow<HealthElement> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(healthElementDAO.getList(dbInstanceUri, groupId, healthElementIds))
    }

    override fun findByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<HealthElement> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(healthElementDAO.findByHCPartySecretPatientKeys(dbInstanceUri, groupId, hcPartyId, secretPatientKeys))
    }

    override suspend fun findLatestByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): List<HealthElement> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return healthElementDAO.findByHCPartySecretPatientKeys(dbInstanceUri, groupId, hcPartyId, secretPatientKeys).toList()
                .groupBy { it.healthElementId }.values.mapNotNull { value -> value.maxBy { it.modified ?: it.created ?: 0L } }
    }

    override fun findByHCPartyAndCodes(hcPartyId: String, codeType: String, codeNumber: String) = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(healthElementDAO.findByHCPartyAndCodes(dbInstanceUri, groupId, hcPartyId, codeType, codeNumber))
    }

    override fun findByHCPartyAndTags(hcPartyId: String, tagType: String, tagCode: String) = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(healthElementDAO.findByHCPartyAndTags(dbInstanceUri, groupId, hcPartyId, tagType, tagCode))
    }

    override fun findByHCPartyAndStatus(hcPartyId: String, status: Int): Flow<String> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(healthElementDAO.findByHCPartyAndStatus(dbInstanceUri, groupId, hcPartyId, status))
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
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val healthElement = getHealthElement(healthElementId)
        return delegation.delegatedTo?.let { healthcarePartyId ->
            healthElement?.let { c -> healthElementDAO.save(dbInstanceUri, groupId, c.copy(delegations = c.delegations + mapOf(
                    healthcarePartyId to setOf(delegation)
            )))}
        } ?: healthElement
    }

    override suspend fun addDelegations(healthElementId: String, delegations: List<Delegation>): HealthElement? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val healthElement = getHealthElement(healthElementId)
        return healthElement?.let {
            return healthElementDAO.save(dbInstanceUri, groupId, it.copy(
                    delegations = it.delegations +
                            delegations.mapNotNull { d -> d.delegatedTo?.let { delegateTo -> delegateTo to setOf(d) } }
            ))
        }
    }

    override suspend fun solveConflicts() {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val healthElementsInConflict = healthElementDAO.listConflicts(dbInstanceUri, groupId).mapNotNull { healthElementDAO.get(dbInstanceUri, groupId, it.id, Option.CONFLICTS) }
        healthElementsInConflict.collect { he ->
            var modifiedContact = he
            he.conflicts?.mapNotNull { c: String -> healthElementDAO.get(dbInstanceUri, groupId, he.id, c) }?.forEach { cp ->
                modifiedContact = modifiedContact.merge(cp)
                healthElementDAO.purge(dbInstanceUri, groupId, cp)
            }
            healthElementDAO.save(dbInstanceUri, groupId, modifiedContact)
        }
    }

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
