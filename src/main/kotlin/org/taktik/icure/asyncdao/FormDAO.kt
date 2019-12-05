package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.support.View
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Form
import java.net.URI

interface FormDAO: GenericDAO<Form> {
    fun findByHcPartyPatient(dbInstanceUrl: URI, groupId: String, hcPartyId: String, secretPatientKeys: List<String>): Flow<Form>

    fun findByHcPartyParentId(dbInstanceUrl: URI, groupId: String, hcPartyId: String, formId: String): Flow<Form>

    fun findAll(dbInstanceUrl: URI, groupId: String, pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>

    fun listConflicts(dbInstanceUrl: URI, groupId: String): Flow<Form>
}
