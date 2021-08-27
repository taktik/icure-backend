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

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Charsets
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.FormTemplateDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.FormTemplateLogic
import org.taktik.icure.dto.gui.layout.FormLayout
import org.taktik.icure.entities.FormTemplate
import org.taktik.icure.utils.firstOrNull

@ExperimentalCoroutinesApi
@Service
class FormTemplateLogicImpl(private val formTemplateDAO: FormTemplateDAO,
                            private val sessionLogic: AsyncSessionLogic,
                            private val objectMapper: ObjectMapper) : GenericLogicImpl<FormTemplate, FormTemplateDAO>(sessionLogic), FormTemplateLogic {

    override fun createFormTemplates(entities: Collection<FormTemplate>, createdEntities: Collection<FormTemplate>) = flow {
        emitAll(super.createEntities(entities))
    }

    override suspend fun createFormTemplate(entity: FormTemplate) = fix(entity) { entity ->
        formTemplateDAO.createFormTemplate(entity)
    }

    override suspend fun getFormTemplate(formTemplateId: String): FormTemplate? {
        return formTemplateDAO.get(formTemplateId)
    }

    override fun getFormTemplatesByGuid(userId: String, specialityCode: String, formTemplateGuid: String): Flow<FormTemplate> = flow {
        val byUserGuid = formTemplateDAO.listFormTemplatesByUserGuid(userId, formTemplateGuid, true)
        if (byUserGuid.firstOrNull() != null) {
            emitAll(byUserGuid)
        } else {
            emitAll(formTemplateDAO.listFormsBySpecialtyAndGuid(specialityCode, formTemplateGuid, true))
        }
    }

    override fun getFormTemplatesBySpecialty(specialityCode: String, loadLayout: Boolean): Flow<FormTemplate> = flow {
        emitAll(formTemplateDAO.listFormsBySpecialtyAndGuid(specialityCode, null, loadLayout))
    }

    override fun getFormTemplatesByUser(userId: String, loadLayout: Boolean): Flow<FormTemplate> = flow {
        emitAll(formTemplateDAO.listFormTemplatesByUserGuid(userId, null, loadLayout))
    }

    override suspend fun modifyFormTemplate(formTemplate: FormTemplate) = fix(formTemplate) { formTemplate ->
        formTemplateDAO.save(formTemplate)
    }

    override suspend fun build(data: ByteArray): FormLayout {
        return objectMapper.readValue(String(data, Charsets.UTF_8), FormLayout::class.java)
    }

    override fun getGenericDAO(): FormTemplateDAO {
        return formTemplateDAO
    }

    override fun getFieldsNames(formLayout: FormLayout): List<String> {
        val fieldNames: MutableList<String> = ArrayList()
        val sections = formLayout.sections
        sections.forEach { section ->
            val formColumns = section.formColumns
            formColumns.forEach { column ->
                val formDataList = column.formDataList
                formDataList.forEach { formData -> fieldNames.add(formData.name) }
            }
        }
        return fieldNames
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FormTemplateLogicImpl::class.java)
    }
}
