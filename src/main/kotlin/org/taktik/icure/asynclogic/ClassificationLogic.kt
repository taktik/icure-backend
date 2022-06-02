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
import org.taktik.icure.asyncdao.ClassificationDAO
import org.taktik.icure.entities.Classification
import org.taktik.icure.entities.embed.Delegation

interface ClassificationLogic : EntityPersister<Classification, String> {
	fun getGenericDAO(): ClassificationDAO

	suspend fun createClassification(classification: Classification): Classification?

	suspend fun getClassification(classificationId: String): Classification?
	fun listClassificationsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<Classification>
	fun deleteClassifications(ids: Set<String>): Flow<DocIdentifier>

	suspend fun modifyClassification(classification: Classification): Classification

	suspend fun addDelegation(classificationId: String, healthcarePartyId: String, delegation: Delegation): Classification?

	suspend fun addDelegations(classificationId: String, delegations: List<Delegation>): Classification?
	fun getClassifications(ids: List<String>): Flow<Classification>
}
