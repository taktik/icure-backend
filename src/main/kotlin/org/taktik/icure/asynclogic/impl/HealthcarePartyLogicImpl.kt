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

import com.google.common.base.Preconditions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.HealthcarePartyDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.GroupLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.exceptions.DeletionException
import org.taktik.icure.exceptions.DocumentNotFoundException
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.firstOrNull
import java.net.URI
import java.util.*

@ExperimentalCoroutinesApi
@Service
class HealthcarePartyLogicImpl(
        couchDbProperties: CouchDbProperties,
        private val healthcarePartyDAO: HealthcarePartyDAO,
        private val uuidGenerator: UUIDGenerator,
        private val userDAO: UserDAO,
        private val groupLogic: GroupLogic,
        private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<HealthcareParty, HealthcarePartyDAO>(sessionLogic), HealthcarePartyLogic {

    private val dbInstanceUri = URI(couchDbProperties.url)

    override fun getGenericDAO(): HealthcarePartyDAO {
        return healthcarePartyDAO
    }

    override suspend fun getHealthcareParty(id: String): HealthcareParty? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return healthcarePartyDAO.get(dbInstanceUri, groupId, id)
    }

    override fun findHealthcareParties(searchString: String, offset: Int, limit: Int): Flow<HealthcareParty> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(healthcarePartyDAO.findHealthcareParties(dbInstanceUri, groupId, searchString, offset, limit))
    }

    override suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return healthcarePartyDAO.getHcPartyKeysForDelegate(dbInstanceUri, groupId, healthcarePartyId)
    }

    override suspend fun modifyHealthcareParty(healthcareParty: HealthcareParty) = fix(healthcareParty) { healthcareParty ->
        if (healthcareParty.nihii == null && healthcareParty.ssin == null && healthcareParty.name == null && healthcareParty.lastName == null) {
            throw MissingRequirementsException("modifyHealthcareParty: one of Name or Last name, Nihii or  Ssin are required.")
        }
        try {
            updateEntities(setOf(healthcareParty))
            getHealthcareParty(healthcareParty.id)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid healthcare party", e)
        }
    }

    override fun deleteHealthcareParties(healthcarePartyIds: List<String>): Flow<DocIdentifier> {
        return try {
            deleteByIds(healthcarePartyIds)
        } catch (e: Exception) {
            log.error(e.message, e)
            throw DeletionException("The healthcare party (" + healthcarePartyIds + ") not found or " + e.message, e)
        }
    }

    override fun deleteHealthcareParties(groupId: String, healthcarePartyIds: List<String>) = flow {
        val group = getDestinationGroup(groupId)

        try {
            emitAll(healthcarePartyDAO.remove(URI.create(group.dbInstanceUrl() ?: dbInstanceUri.toASCIIString()), group.id, healthcarePartyDAO.getList(URI.create(group.dbInstanceUrl() ?: dbInstanceUri.toASCIIString()), groupId, healthcarePartyIds).toList()))
        } catch (e: Exception) {
            log.error(e.message, e)
            throw DeletionException("The healthcare party (" + healthcarePartyIds + ") not found or " + e.message, e)
        }
    }

    override suspend fun createHealthcareParty(groupId: String, healthcareParty: HealthcareParty) = fix(healthcareParty) { healthcareParty ->
        val group = getDestinationGroup(groupId)
        if (healthcareParty.nihii == null && healthcareParty.ssin == null && healthcareParty.name == null && healthcareParty.lastName == null) {
            throw MissingRequirementsException("createHealthcareParty: one of Name or Last name, Nihii, and Public key are required.")
        }
        try {
            if (healthcareParty.id == null) {
                val newId = uuidGenerator.newGUID().toString()
                healthcareParty.id = newId
            }
            getGenericDAO().create(URI.create(group.dbInstanceUrl() ?: dbInstanceUri.toASCIIString()), group.id, listOf(healthcareParty)).firstOrNull()
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid healthcare party", e)
        }
    }

    override suspend fun modifyHealthcareParty(groupId: String, healthcareParty: HealthcareParty)= fix(healthcareParty) { healthcareParty ->
        val group = getDestinationGroup(groupId)
        if (healthcareParty.nihii == null && healthcareParty.ssin == null && healthcareParty.name == null && healthcareParty.lastName == null) {
            throw MissingRequirementsException("modifyHealthcareParty: one of Name or Last name, Nihii or  Ssin are required.")
        }
        try {
            healthcarePartyDAO.save(URI.create(group.dbInstanceUrl() ?: dbInstanceUri.toASCIIString()), group.id, listOf(healthcareParty)).firstOrNull()
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid healthcare party", e)
        }
    }

    override suspend fun createHealthcareParty(healthcareParty: HealthcareParty)= fix(healthcareParty) { healthcareParty ->
        if (healthcareParty.nihii == null && healthcareParty.ssin == null && healthcareParty.name == null && healthcareParty.lastName == null) {
            throw MissingRequirementsException("createHealthcareParty: one of Name or Last name, Nihii, and Public key are required.")
        }
        try {
            if (healthcareParty.id == null) {
                val newId = uuidGenerator.newGUID().toString()
                healthcareParty.id = newId
            }
            createEntities(setOf(healthcareParty)).firstOrNull()
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid healthcare party", e)
        }
    }

    override suspend fun updateHcPartyKeys(healthcarePartyId: String, newHcPartyKeys: Map<String, Array<String>>): Map<String, Array<String>> {
        Preconditions.checkArgument(newHcPartyKeys.isNotEmpty())
        // Fetching existing HcPartyKeys
        val healthcareParty = getHealthcareParty(healthcarePartyId) ?: throw IllegalStateException("No HCP found for ID $healthcarePartyId")
        val existingHcPartyKeys = healthcareParty.hcPartyKeys
        // Updating with new HcPartyKeys
        existingHcPartyKeys.putAll(newHcPartyKeys)
        healthcareParty.hcPartyKeys = existingHcPartyKeys
        updateEntities(setOf(healthcareParty))
        return existingHcPartyKeys
    }

    override fun listHealthcareParties(offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val healthcareParties = healthcarePartyDAO.listHealthCareParties(dbInstanceUri, groupId, offset, desc)
        emitAll(healthcareParties)
    }

    override fun findHealthcareParties(fuzzyName: String, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val healthcareParties = healthcarePartyDAO.findByHcPartyNameContainsFuzzy(dbInstanceUri, groupId, fuzzyName, offset, desc)
        emitAll(healthcareParties)
    }

    override fun listByNihii(nihii: String): Flow<HealthcareParty> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(healthcarePartyDAO.findByNihii(dbInstanceUri, groupId, nihii))
    }

    override fun listBySsin(ssin: String): Flow<HealthcareParty> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(healthcarePartyDAO.findBySsin(dbInstanceUri, groupId, ssin))
    }

    override fun listByName(name: String): Flow<HealthcareParty> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(healthcarePartyDAO.findByName(dbInstanceUri, groupId, name))
    }

    override suspend fun getPublicKey(healthcarePartyId: String): String? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val hcParty = healthcarePartyDAO.get(dbInstanceUri, groupId, healthcarePartyId)
                ?: throw DocumentNotFoundException("Healthcare party ($healthcarePartyId) not found in the database.")
        return hcParty.publicKey
    }

    override fun findHealthcareParties(type: String, spec: String, firstCode: String, lastCode: String): Flow<ViewQueryResultEvent> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(healthcarePartyDAO.findBySpecialityPostcode(dbInstanceUri, groupId, type, spec, firstCode, lastCode))
    }

    override fun getHealthcareParties(ids: List<String>): Flow<HealthcareParty> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(healthcarePartyDAO.getList(dbInstanceUri, groupId, ids))
    }

    override fun getHealthcareParties(groupId: String, ids: List<String>?) = flow {
        val group = getDestinationGroup(groupId)
        val uri = URI.create(group.dbInstanceUrl() ?: dbInstanceUri.toASCIIString())
        emitAll(ids?.let { healthcarePartyDAO.getList(uri, group.id, it)} ?: healthcarePartyDAO.getAll(uri, groupId) )
    }

    override fun findHealthcarePartiesBySsinOrNihii(searchValue: String, paginationOffset: PaginationOffset<String>, desc: Boolean): Flow<ViewQueryResultEvent> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(healthcarePartyDAO.findBySsinOrNihii(dbInstanceUri, groupId, searchValue, paginationOffset, desc))
    }

    override fun getHealthcarePartiesByParentId(parentId: String): Flow<HealthcareParty> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(healthcarePartyDAO.findByParentId(dbInstanceUri, groupId, parentId))
    }

    override suspend fun getHcpHierarchyIds(hcParty: HealthcareParty): HashSet<String> {
        val hcpartyIds = HashSet<String>()
        hcpartyIds.add(hcParty.id)

        var hcpInHierarchy: HealthcareParty? = hcParty

        while (hcpInHierarchy?.parentId != null) {
            hcpInHierarchy = getHealthcareParty(hcpInHierarchy.parentId)
            hcpInHierarchy?.id?.let { hcpartyIds.add(it) }
        }
        return hcpartyIds
    }

    override suspend fun createHealthcarePartyOnUserDb(healthcareParty: HealthcareParty, groupId: String, dbInstanceUri: URI)= fix(healthcareParty) { healthcareParty ->
        if (healthcareParty.nihii == null && healthcareParty.ssin == null && healthcareParty.name == null && healthcareParty.lastName == null) {
            throw MissingRequirementsException("createHealthcareParty: one of Name or Last name, Nihii, and Public key are required.")
        }
        try {
            if (healthcareParty.id == null) {
                val newId = uuidGenerator.newGUID().toString()
                healthcareParty.id = newId
            }
            getGenericDAO().create(dbInstanceUri, groupId, healthcareParty)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid healthcare party", e)
        }
    }

    protected suspend fun getDestinationGroup(groupId: String): Group {
        val groupUserId = sessionLogic.getCurrentSessionContext().getGroupIdUserId()
        val userGroupId = userDAO.getOnFallback(dbInstanceUri, groupUserId, false)?.groupId
                ?: throw IllegalAccessException("Invalid user, no group")
        val group = groupLogic.getGroup(groupId)

        if (group?.superGroup != userGroupId) {
            throw IllegalAccessException("You are not allowed to access this group database")
        }
        return group
    }


    companion object {
        private val log = LoggerFactory.getLogger(HealthcarePartyLogicImpl::class.java)
    }
}
