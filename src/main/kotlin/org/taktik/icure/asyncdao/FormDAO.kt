package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Form
import java.net.URI

interface FormDAO: GenericDAO<Form> {
    fun findByHcPartyPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<Form>

    fun findByHcPartyParentId(hcPartyId: String, formId: String): Flow<Form>

    fun findAll(pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>

    fun listConflicts(): Flow<Form>

    suspend fun getAllByExternalUuid(externalUuid: String): List<Form>
}
