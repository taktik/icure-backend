package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.HealthcareParty
import java.net.URI

interface HealthcarePartyDAO: GenericDAO<HealthcareParty> {
    fun findByNihii(dbInstanceUrl: URI, groupId: String, nihii: String?): Flow<HealthcareParty>

    fun findBySsin(dbInstanceUrl: URI, groupId: String, ssin: String): Flow<HealthcareParty>

    fun findBySpecialityPostcode(dbInstanceUrl: URI, groupId: String, type: String, spec: String, firstCode: String, lastCode: String): Flow<ViewQueryResultEvent>

    fun listHealthCareParties(dbInstanceUrl: URI, groupId: String, pagination: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

    fun findByName(dbInstanceUrl: URI, groupId: String, name: String): Flow<HealthcareParty>

    fun findBySsinOrNihii(dbInstanceUrl: URI, groupId: String, searchValue: String?, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

    fun findByHcPartyNameContainsFuzzy(dbInstanceUrl: URI, groupId: String, searchString: String?, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

    fun findHealthcareParties(dbInstanceUrl: URI, groupId: String, searchString: String, offset: Int, limit: Int): Flow<HealthcareParty>

    suspend fun getHcPartyKeysForDelegate(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String): Map<String, String>

    fun findByParentId(dbInstanceUrl: URI, groupId: String, parentId: String): Flow<HealthcareParty>
}
