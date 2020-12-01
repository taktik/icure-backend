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

package org.taktik.icure.be.ehealth.logic.kmehr.sumehr

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.be.ehealth.dto.SumehrStatus
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.domain.mapping.ImportMapping
import org.taktik.icure.domain.result.ImportResult
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.entities.embed.Partnership
import org.taktik.icure.entities.embed.PatientHealthCareParty
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.services.external.api.AsyncDecrypt
import java.nio.ByteBuffer

interface SumehrLogic {
    suspend fun isSumehrValid(hcPartyId: String, patient: Patient, patientSecretForeignKeys: List<String>, excludedIds: List<String>, includeIrrelevantInformation: Boolean, services: List<Service>?, healthElements: List<HealthElement>?): SumehrStatus
    fun createSumehr(pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, comment: String, excludedIds: List<String>, includeIrrelevantInformation: Boolean, decryptor: AsyncDecrypt?, services: List<Service>?, healthElements: List<HealthElement>?, config: Config): Flow<DataBuffer>
    fun validateSumehr(pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, comment: String, excludedIds: List<String>, includeIrrelevantInformation: Boolean, decryptor: AsyncDecrypt?, services: List<Service>?, healthElements: List<HealthElement>?, config: Config): Flow<DataBuffer>
    suspend fun getAllServices(hcPartyId: String, sfks: List<String>, excludedIds: List<String>, includeIrrelevantInformation: Boolean, decryptor: AsyncDecrypt?): List<Service>
	suspend fun getHealthElements(hcPartyId: String, sfks: List<String>, excludedIds: List<String>, includeIrrelevantInformation: Boolean): List<HealthElement>
    suspend fun getContactPeople(hcPartyId: String, sfks: List<String>, excludedIds: List<String>, patientId: String): List<Partnership>
    suspend fun getPatientHealthcareParties(hcPartyId: String, sfks: List<String>, excludedIds: List<String>, patientId: String): List<PatientHealthCareParty>
	suspend fun getSumehrMd5(hcPartyId: String, patient: Patient, patientSecretForeignKeys: List<String>, excludedIds: List<String>, includeIrrelevantInformation: Boolean): String
    suspend fun importSumehr(inputData : Flow<ByteBuffer>, author: User, language: String, dest: Patient? = null, mappings: Map<String, List<ImportMapping>> = HashMap(), saveToDatabase: Boolean): List<ImportResult>
    suspend fun importSumehrByItemId(inputData : Flow<ByteBuffer>, itemId: String, author: User, language: String, dest: Patient? = null, mappings: Map<String, List<ImportMapping>> = HashMap(), saveToDatabase: Boolean): List<ImportResult>
}
