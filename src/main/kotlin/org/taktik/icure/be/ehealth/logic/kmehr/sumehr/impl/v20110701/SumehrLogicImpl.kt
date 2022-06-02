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

package org.taktik.icure.be.ehealth.logic.kmehr.sumehr.impl.v20110701

import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.ByteBuffer
import be.fgov.ehealth.ehvalidator.core.EhValidator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.be.ehealth.dto.SumehrStatus
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.sumehr.SumehrLogic
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

@ExperimentalCoroutinesApi
@org.springframework.stereotype.Service("sumehrLogicV1")
class SumehrLogicImpl(val contactLogic: ContactLogic, val healthcarePartyLogic: HealthcarePartyLogic, @Qualifier("sumehrExportV1") val sumehrExport: SumehrExport, @Qualifier("sumehrImportV1") val sumehrImport: SumehrImport) : SumehrLogic {

	override suspend fun isSumehrValid(hcPartyId: String, patient: Patient, patientSecretForeignKeys: List<String>, excludedIds: List<String>, includeIrrelevantInformation: Boolean, services: List<Service>?, healthElements: List<HealthElement>?): SumehrStatus {
		val sumehrServiceIds = ArrayList<String>()
		sumehrServiceIds.addAll(contactLogic.listServiceIdsByTag(hcPartyId, patientSecretForeignKeys, "CD-TRANSACTION", "sumehr", null, null).toList())

		if (sumehrServiceIds.isEmpty()) {
			return SumehrStatus.absent
		}

		val servicesByIds = mutableMapOf<String, Service>()
		val comparator = Comparator<Service> { a, b -> a.modified?.compareTo(b.modified ?: 0) ?: -1 }

		contactLogic.getServices(sumehrServiceIds).toList().sortedWith(comparator).forEach { if (it.endOfLife != null) servicesByIds.remove(it.id) else servicesByIds[it.id] = it }

		if (servicesByIds.isEmpty()) {
			return SumehrStatus.outdated
		}

		return if (servicesByIds.values.sortedWith(comparator).last().comment == getSumehrMd5(hcPartyId, patient, patientSecretForeignKeys, excludedIds, includeIrrelevantInformation)) SumehrStatus.uptodate else SumehrStatus.outdated
	}

	override suspend fun getSumehrMd5(hcPartyId: String, patient: Patient, patientSecretForeignKeys: List<String>, excludedIds: List<String>, includeIrrelevantInformation: Boolean) =
		sumehrExport.getMd5(hcPartyId, patient, patientSecretForeignKeys, excludedIds, includeIrrelevantInformation)

	override suspend fun importSumehr(inputData: Flow<ByteBuffer>, author: User, language: String, dest: Patient?, mappings: Map<String, List<ImportMapping>>, saveToDatabase: Boolean): List<ImportResult> {
		return sumehrImport.importSumehr(inputData, author, language, mappings, saveToDatabase, dest)
	}

	override suspend fun importSumehrByItemId(inputData: Flow<ByteBuffer>, itemId: String, author: User, language: String, dest: Patient?, mappings: Map<String, List<ImportMapping>>, saveToDatabase: Boolean): List<ImportResult> {
		return sumehrImport.importSumehrByItemId(inputData, itemId, author, language, mappings, saveToDatabase, dest)
	}

	override fun createSumehr(pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, comment: String, excludedIds: List<String>, includeIrrelevantInformation: Boolean, decryptor: AsyncDecrypt?, services: List<Service>?, healthElements: List<HealthElement>?, config: Config) = sumehrExport.createSumehr(pat, sfks, sender, recipient, language, comment, excludedIds, includeIrrelevantInformation, decryptor, services, healthElements, config)

	@Throws(IOException::class)
	override fun validateSumehr(pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, comment: String, excludedIds: List<String>, includeIrrelevantInformation: Boolean, decryptor: AsyncDecrypt?, services: List<Service>?, healthElements: List<HealthElement>?, config: Config) = flow<DataBuffer> {
		val temp = File.createTempFile("temp", java.lang.Long.toString(System.nanoTime()))

		val sos = sumehrExport.createSumehr(pat, sfks, sender, recipient, language, comment, excludedIds, includeIrrelevantInformation, decryptor, services, healthElements, config)
		try {
			val databuffer = sos.first()
			val html = EhValidator.getHTMLReport(temp.absolutePath, EhValidator.Language.french, "Sumehr")
			val w = OutputStreamWriter(databuffer.asOutputStream(), "UTF-8")
			w.write(html)
			w.close()
		} catch (e: Exception) {
			throw IOException(e)
		}

		emitAll(sos)
	}

	override suspend fun getAllServices(hcPartyId: String, sfks: List<String>, excludedIds: List<String>, includeIrrelevantInformation: Boolean, decryptor: AsyncDecrypt?) =
		healthcarePartyLogic.getHealthcareParty(hcPartyId)?.let { healthcarePartyLogic.getHcpHierarchyIds(it) }?.let { sumehrExport.getAllServices(it, sfks, excludedIds, includeIrrelevantInformation, decryptor) } ?: listOf()

	override suspend fun getHealthElements(hcPartyId: String, sfks: List<String>, excludedIds: List<String>, includeIrrelevantInformation: Boolean): List<HealthElement> {
		return healthcarePartyLogic.getHealthcareParty(hcPartyId)?.let { healthcarePartyLogic.getHcpHierarchyIds(it) }?.let { sumehrExport.getHealthElements(it, sfks, excludedIds, includeIrrelevantInformation, HashSet()) } ?: listOf()
	}

	override suspend fun getContactPeople(hcPartyId: String, sfks: List<String>, excludedIds: List<String>, patientId: String): List<Partnership> {
		return healthcarePartyLogic.getHealthcareParty(hcPartyId)?.let { healthcarePartyLogic.getHcpHierarchyIds(it) }?.let { sumehrExport.getContactPeople(it, sfks, excludedIds, patientId) } ?: listOf()
	}

	override suspend fun getPatientHealthcareParties(hcPartyId: String, sfks: List<String>, excludedIds: List<String>, patientId: String): List<PatientHealthCareParty> {
		return healthcarePartyLogic.getHealthcareParty(hcPartyId)?.let { healthcarePartyLogic.getHcpHierarchyIds(it) }?.let { sumehrExport.getPatientHealthCareParties(it, sfks, excludedIds, patientId) } ?: listOf()
	}
}
