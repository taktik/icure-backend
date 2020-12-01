package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.FormTemplate
import java.net.URI

interface FormTemplateDAO: GenericDAO<FormTemplate> {
    fun findByUserGuid(userId: String, guid: String?, loadLayout: Boolean): Flow<FormTemplate>

    fun findByGuid(guid: String, loadLayout: Boolean): Flow<FormTemplate>

    fun findBySpecialtyGuid(specialityCode: String, guid: String?, loadLayout: Boolean): Flow<FormTemplate>

    suspend fun createFormTemplate(entity: FormTemplate): FormTemplate
}
