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
