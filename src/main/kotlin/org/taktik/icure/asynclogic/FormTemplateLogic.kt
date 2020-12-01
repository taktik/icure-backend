package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.asyncdao.FormTemplateDAO
import org.taktik.icure.dto.gui.layout.FormLayout
import org.taktik.icure.entities.FormTemplate

interface FormTemplateLogic : EntityPersister<FormTemplate, String> {
    fun createEntities(entities: Collection<FormTemplate>, createdEntities: Collection<FormTemplate>): Flow<FormTemplate>

    suspend fun createFormTemplate(entity: FormTemplate): FormTemplate

    suspend fun getFormTemplateById(formTemplateId: String): FormTemplate?
    fun getFormTemplatesByGuid(userId: String, specialityCode: String, formTemplateGuid: String): Flow<FormTemplate>
    fun getFormTemplatesBySpecialty(specialityCode: String, loadLayout: Boolean): Flow<FormTemplate>
    fun getFormTemplatesByUser(userId: String, loadLayout: Boolean): Flow<FormTemplate>

    suspend fun modifyFormTemplate(formTemplate: FormTemplate): FormTemplate?

    suspend fun build(data: ByteArray): FormLayout
    fun getGenericDAO(): FormTemplateDAO
    fun getFieldsNames(formLayout: FormLayout): List<String>
}
