package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.HealthcarePartyDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.HealthcareParty
import java.net.URI
import java.util.*

interface HealthcarePartyLogic : EntityPersister<HealthcareParty, String> {
    fun getGenericDAO(): HealthcarePartyDAO

    suspend fun getHealthcareParty(id: String): HealthcareParty?
    fun findHealthcareParties(searchString: String, offset: Int, limit: Int): Flow<HealthcareParty>

    suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String>

    suspend fun modifyHealthcareParty(healthcareParty: HealthcareParty): HealthcareParty?
    fun deleteHealthcareParties(healthcarePartyIds: List<String>): Flow<DocIdentifier>

    suspend fun createHealthcareParty(healthcareParty: HealthcareParty): HealthcareParty?

    suspend fun updateHcPartyKeys(healthcarePartyId: String, newHcPartyKeys: Map<String, Array<String>>): Map<String, Array<String>>
    fun listHealthcareParties(offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>
    fun findHealthcareParties(fuzzyName: String, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>
    fun listByNihii(nihii: String): Flow<HealthcareParty>
    fun listBySsin(ssin: String): Flow<HealthcareParty>
    fun listByName(name: String): Flow<HealthcareParty>

    suspend fun getPublicKey(healthcarePartyId: String): String?
    fun findHealthcareParties(type: String, spec: String, firstCode: String, lastCode: String): Flow<ViewQueryResultEvent>
    fun getHealthcareParties(ids: List<String>): Flow<HealthcareParty>
    fun findHealthcarePartiesBySsinOrNihii(searchValue: String, paginationOffset: PaginationOffset<String>, desc: Boolean): Flow<ViewQueryResultEvent>
    fun getHealthcarePartiesByParentId(parentId: String): Flow<HealthcareParty>
    suspend fun getHcpHierarchyIds(sender: HealthcareParty): HashSet<String>

    suspend fun createHealthcarePartyOnUserDb(healthcareParty: HealthcareParty, HealthcareParty: URI): HealthcareParty?
    fun getHealthcareParties(groupId: String, ids: List<String>?): Flow<HealthcareParty>
    fun deleteHealthcareParties(groupId: String, healthcarePartyIds: List<String>): Flow<DocIdentifier>
    suspend fun createHealthcareParty(groupId: String, healthcareParty: HealthcareParty): HealthcareParty?
    suspend fun modifyHealthcareParty(groupId: String, healthcareParty: HealthcareParty): HealthcareParty?
}
