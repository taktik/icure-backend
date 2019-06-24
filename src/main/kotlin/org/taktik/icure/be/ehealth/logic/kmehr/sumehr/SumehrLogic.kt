/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth.logic.kmehr.sumehr

import org.taktik.icure.be.ehealth.dto.SumehrStatus
import org.taktik.icure.dto.mapping.ImportMapping
import org.taktik.icure.dto.result.ImportResult
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.services.external.api.AsyncDecrypt
import java.io.InputStream
import java.io.OutputStream

interface SumehrLogic {
    fun isSumehrValid(hcPartyId: String, patient: Patient, patientSecretForeignKeys: List<String>): SumehrStatus
    fun createSumehr(os: OutputStream, pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, comment: String, decryptor: AsyncDecrypt?)
	fun createSumehrPlusPlus(os: OutputStream, pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, comment: String, decryptor: AsyncDecrypt?)
    fun validateSumehr(os: OutputStream, pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, comment: String, decryptor: AsyncDecrypt?)
    fun getAllServices(hcPartyId: String, sfks: List<String>, decryptor: AsyncDecrypt?): List<Service>
	fun getAllServicesPlusPlus(hcPartyId: String, sfks: List<String>, decryptor: AsyncDecrypt?): List<Service>
	fun getHealthElements(hcPartyId: String, sfks: List<String>): List<HealthElement>
	fun getSumehrMd5(hcPartyId: String, patient: Patient, patientSecretForeignKeys: List<String>): String
    fun importSumehr(inputStream: InputStream, author: User, language: String, dest: Patient? = null, mappings: Map<String, List<ImportMapping>> = HashMap()): List<ImportResult>
}
