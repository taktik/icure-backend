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
import org.taktik.icure.asynclogic.AsyncICureSessionLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.dao.Option
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.dto.filter.chain.FilterChain
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
                             private val sessionLogic: AsyncICureSessionLogic) : GenericLogicImpl<HealthElement, HealthElementDAO>(sessionLogic), HealthElementLogic {

    override fun getGenericDAO(): HealthElementDAO {
        return healthElementDAO
    }

    override suspend fun createHealthElement(healthElement: HealthElement): HealthElement? {
        try { // Fetching the hcParty
            val healthcarePartyId = sessionLogic.getCurrentHealthcarePartyId()
            // Setting Healthcare problem attributes
            healthElement.id = uuidGenerator.newGUID().toString()
            if (healthElement.openingDate == null) {
                healthElement.openingDate = FuzzyValues.getFuzzyDateTime(LocalDateTime.now(), ChronoUnit.SECONDS)
            }
            healthElement.author = healthcarePartyId
            healthElement.responsible = healthcarePartyId
            // TODO should we check that opening contacts or closing contacts are valid?
            return createEntities(setOf(healthElement)).firstOrNull()
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

    override fun findByHCPartyAndCodes(hcPartyId: String, codeType: String, codeNumber: String): Flow<String> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(healthElementDAO.findByHCPartyAndCodes(dbInstanceUri, groupId, hcPartyId, codeType, codeNumber))
    }

    override fun findByHCPartyAndTags(hcPartyId: String, tagType: String, tagCode: String): Flow<String> = flow {
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

    override suspend fun modifyHealthElement(healthElement: HealthElement): HealthElement? {
        return try {
            updateEntities(setOf(healthElement))
            getHealthElement(healthElement.id)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid Health problem", e)
        }
    }

    override suspend fun addDelegation(healthElementId: String, healthcarePartyId: String, delegation: Delegation): HealthElement? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val healthElement = getHealthElement(healthElementId)
        healthElement?.addDelegation(healthcarePartyId, delegation)
        return healthElement?.let { healthElementDAO.save(dbInstanceUri, groupId, it) }
    }

    override suspend fun addDelegations(healthElementId: String, delegations: List<Delegation>): HealthElement? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val healthElement = getHealthElement(healthElementId)
        delegations.forEach(Consumer { d: Delegation -> healthElement?.addDelegation(d.delegatedTo, d) })
        return healthElement?.let { healthElementDAO.save(dbInstanceUri, groupId, it) }
    }

    override suspend fun solveConflicts() {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val healthElementsInConflict = healthElementDAO.listConflicts(dbInstanceUri, groupId).mapNotNull { healthElementDAO.get(dbInstanceUri, groupId, it.id, Option.CONFLICTS) }
        healthElementsInConflict.collect { p ->
            p.conflicts.mapNotNull { c: String? -> healthElementDAO.get(dbInstanceUri, groupId, p.id, c) }.forEach { cp ->
                p.solveConflictWith(cp)
                healthElementDAO.purge(dbInstanceUri, groupId, cp)
            }
            healthElementDAO.save(dbInstanceUri, groupId, p)
        }
    }

    override suspend fun filter(filter: FilterChain<HealthElement>): Flow<HealthElement> {
        val ids = filters.resolve(filter.getFilter()).toList()
        val healthElements = getHealthElements(ids)
        val predicate = filter.predicate
        return if (predicate != null) healthElements.filter { predicate.apply(it) } else healthElements
    }

    companion object {
        private val log = LoggerFactory.getLogger(HealthElementLogicImpl::class.java)
    }
}
