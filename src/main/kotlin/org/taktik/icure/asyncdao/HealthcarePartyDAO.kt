package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.HealthcareParty
import java.net.URI

interface HealthcarePartyDAO: GenericDAO<HealthcareParty> {
    fun findByNihii(nihii: String?): Flow<HealthcareParty>

    fun findBySsin(ssin: String): Flow<HealthcareParty>

    fun findBySpecialityPostcode(type: String, spec: String, firstCode: String, lastCode: String): Flow<ViewQueryResultEvent>

    fun listHealthCareParties(pagination: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

    fun findByName(name: String): Flow<HealthcareParty>

    fun findBySsinOrNihii(searchValue: String?, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

    fun findByHcPartyNameContainsFuzzy(searchString: String?, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

    fun findHealthcareParties(searchString: String, offset: Int, limit: Int): Flow<HealthcareParty>

    suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String>

    fun findByParentId(parentId: String): Flow<HealthcareParty>
}
