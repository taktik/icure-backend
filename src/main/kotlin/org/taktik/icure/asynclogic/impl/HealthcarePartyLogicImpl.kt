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
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.db.PaginationOffset
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
        private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<HealthcareParty, HealthcarePartyDAO>(sessionLogic), HealthcarePartyLogic {

    private val dbInstanceUri = URI(couchDbProperties.url)

    override fun getGenericDAO(): HealthcarePartyDAO {
        return healthcarePartyDAO
    }

    override suspend fun getHealthcareParty(id: String): HealthcareParty? {
        return healthcarePartyDAO.get(id)
    }

    override fun findHealthcareParties(searchString: String, offset: Int, limit: Int): Flow<HealthcareParty> = flow {
        emitAll(healthcarePartyDAO.findHealthcareParties(searchString, offset, limit))
    }

    override suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String> {
        return healthcarePartyDAO.getHcPartyKeysForDelegate(healthcarePartyId)
    }

    override suspend fun modifyHealthcareParty(healthcareParty: HealthcareParty) = fix(healthcareParty) { healthcareParty ->
        if (healthcareParty.nihii == null && healthcareParty.ssin == null && healthcareParty.name == null && healthcareParty.lastName == null) {
            throw MissingRequirementsException("modifyHealthcareParty: one of Name or Last name, Nihii or  Ssin are required.")
        }
        try {
            updateEntities(setOf(healthcareParty)).firstOrNull()
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
        try {
            emitAll(healthcarePartyDAO.remove(healthcarePartyDAO.getList(healthcarePartyIds).toList()))
        } catch (e: Exception) {
            log.error(e.message, e)
            throw DeletionException("The healthcare party (" + healthcarePartyIds + ") not found or " + e.message, e)
        }
    }

    override suspend fun createHealthcareParty(groupId: String, healthcareParty: HealthcareParty) = fix(healthcareParty) { healthcareParty ->
        if (healthcareParty.nihii == null && healthcareParty.ssin == null && healthcareParty.name == null && healthcareParty.lastName == null) {
            throw MissingRequirementsException("createHealthcareParty: one of Name or Last name, Nihii, and Public key are required.")
        }
        try {
            getGenericDAO().create(listOf(healthcareParty)).firstOrNull()
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid healthcare party", e)
        }
    }

    override suspend fun modifyHealthcareParty(groupId: String, healthcareParty: HealthcareParty)= fix(healthcareParty) { healthcareParty ->
        if (healthcareParty.nihii == null && healthcareParty.ssin == null && healthcareParty.name == null && healthcareParty.lastName == null) {
            throw MissingRequirementsException("modifyHealthcareParty: one of Name or Last name, Nihii or  Ssin are required.")
        }
        try {
            healthcarePartyDAO.save(listOf(healthcareParty)).firstOrNull()
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid healthcare party", e)
        }
    }

    override suspend fun createHealthcareParty(healthcareParty: HealthcareParty)= fix(healthcareParty) { healthcareParty ->
        if (healthcareParty.nihii == null && healthcareParty.ssin == null && healthcareParty.name == null && healthcareParty.lastName == null) {
            throw MissingRequirementsException("createHealthcareParty: one of Name or Last name, Nihii, and Public key are required.")
        }
        try {
            createEntities(setOf(healthcareParty)).firstOrNull()
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid healthcare party", e)
        }
    }

    override suspend fun updateHcPartyKeys(healthcarePartyId: String, newHcPartyKeys: Map<String, Array<String>>): Map<String, Array<String>> {
        Preconditions.checkArgument(newHcPartyKeys.isNotEmpty())
        // Fetching existing HcPartyKeys
        val healthcareParty = getHealthcareParty(healthcarePartyId) ?: throw IllegalStateException("No HCP found for ID $healthcarePartyId")
        return updateEntities(setOf(healthcareParty.copy(hcPartyKeys = healthcareParty.hcPartyKeys + newHcPartyKeys))).firstOrNull()?.hcPartyKeys ?: healthcareParty.hcPartyKeys
    }

    override fun listHealthcareParties(offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent> = flow {
        val healthcareParties = healthcarePartyDAO.listHealthCareParties(offset, desc)
        emitAll(healthcareParties)
    }

    override fun findHealthcareParties(fuzzyName: String, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent> = flow {
        val healthcareParties = healthcarePartyDAO.findByHcPartyNameContainsFuzzy(fuzzyName, offset, desc)
        emitAll(healthcareParties)
    }

    override fun listByNihii(nihii: String): Flow<HealthcareParty> = flow {
        emitAll(healthcarePartyDAO.findByNihii(nihii))
    }

    override fun listBySsin(ssin: String): Flow<HealthcareParty> = flow {
        emitAll(healthcarePartyDAO.findBySsin(ssin))
    }

    override fun listByName(name: String): Flow<HealthcareParty> = flow {
        emitAll(healthcarePartyDAO.findByName(name))
    }

    override suspend fun getPublicKey(healthcarePartyId: String): String? {
        val hcParty = healthcarePartyDAO.get(healthcarePartyId)
                ?: throw DocumentNotFoundException("Healthcare party ($healthcarePartyId) not found in the database.")
        return hcParty.publicKey
    }

    override fun findHealthcareParties(type: String, spec: String, firstCode: String, lastCode: String): Flow<ViewQueryResultEvent> = flow {
        emitAll(healthcarePartyDAO.findBySpecialityPostcode(type, spec, firstCode, lastCode))
    }

    override fun getHealthcareParties(ids: List<String>): Flow<HealthcareParty> = flow {
        emitAll(healthcarePartyDAO.getList(ids))
    }

    override fun getHealthcareParties(groupId: String, ids: List<String>?) = flow {
        emitAll(ids?.let { healthcarePartyDAO.getList(it)} ?: healthcarePartyDAO.getAll() )
    }

    override fun findHealthcarePartiesBySsinOrNihii(searchValue: String, paginationOffset: PaginationOffset<String>, desc: Boolean): Flow<ViewQueryResultEvent> = flow {
        emitAll(healthcarePartyDAO.findBySsinOrNihii(searchValue, paginationOffset, desc))
    }

    override fun getHealthcarePartiesByParentId(parentId: String): Flow<HealthcareParty> = flow {
        emitAll(healthcarePartyDAO.findByParentId(parentId))
    }

    override suspend fun getHcpHierarchyIds(hcParty: HealthcareParty): HashSet<String> {
        val hcpartyIds = HashSet<String>()
        hcpartyIds.add(hcParty.id)

        var hcpInHierarchy: HealthcareParty? = hcParty

        while (hcpInHierarchy?.parentId?.isNotBlank() == true) {
            hcpInHierarchy = getHealthcareParty(hcpInHierarchy.parentId!!)
            hcpInHierarchy?.id?.let { hcpartyIds.add(it) }
        }
        return hcpartyIds
    }

    override suspend fun createHealthcarePartyOnUserDb(healthcareParty: HealthcareParty, dbInstanceUri: URI)= fix(healthcareParty) { healthcareParty ->
        if (healthcareParty.nihii == null && healthcareParty.ssin == null && healthcareParty.name == null && healthcareParty.lastName == null) {
            throw MissingRequirementsException("createHealthcareParty: one of Name or Last name, Nihii, and Public key are required.")
        }
        try {
            getGenericDAO().create(healthcareParty)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid healthcare party", e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(HealthcarePartyLogicImpl::class.java)
    }
}
