package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.FormTemplate
import java.net.URI

interface FormTemplateDAO {
    fun findByUserGuid(dbInstanceUrl: URI, groupId: String, userId: String, guid: String?, loadLayout: Boolean): Flow<FormTemplate>

    fun findByGuid(dbInstanceUrl: URI, groupId: String, guid: String, loadLayout: Boolean): Flow<FormTemplate>

    fun findBySpecialtyGuid(dbInstanceUrl: URI, groupId: String, specialityCode: String, guid: String?, loadLayout: Boolean): Flow<FormTemplate>

    suspend fun createFormTemplate(dbInstanceUrl: URI, groupId: String, entity: FormTemplate): FormTemplate

    suspend fun beforeSave(dbInstanceUrl: URI, groupId: String, entity: FormTemplate)

    suspend fun afterSave(dbInstanceUrl: URI, groupId: String, entity: FormTemplate)

    suspend fun postLoad(dbInstanceUrl: URI, groupId: String, entity: FormTemplate?)
}
