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
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.ClassificationTemplateDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.entities.embed.Delegation

interface ClassificationTemplateLogic : EntityPersister<ClassificationTemplate, String> {
	fun getGenericDAO(): ClassificationTemplateDAO

	suspend fun createClassificationTemplate(classificationTemplate: ClassificationTemplate): ClassificationTemplate?

	suspend fun getClassificationTemplate(classificationTemplateId: String): ClassificationTemplate?
	fun deleteClassificationTemplates(ids: Set<String>): Flow<DocIdentifier>

	suspend fun modifyClassificationTemplate(classificationTemplate: ClassificationTemplate): ClassificationTemplate

	suspend fun addDelegation(classificationTemplateId: String, healthcarePartyId: String, delegation: Delegation): ClassificationTemplate?

	suspend fun addDelegations(classificationTemplateId: String, delegations: List<Delegation>): ClassificationTemplate?
	fun getClassificationTemplates(ids: List<String>): Flow<ClassificationTemplate>
	fun listClasificationsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: ArrayList<String>): Flow<ClassificationTemplate>

	fun listClassificationTemplates(paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
}
