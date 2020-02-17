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

package org.taktik.icure.be.ehealth.logic.kmehr.sumehr.impl.v20161201

import be.fgov.ehealth.ehvalidator.core.EhValidator
import org.springframework.beans.factory.annotation.Qualifier
import org.taktik.icure.be.ehealth.dto.SumehrStatus
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.sumehr.SumehrLogic
import org.taktik.icure.dto.mapping.ImportMapping
import org.taktik.icure.dto.result.ImportResult
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.entities.embed.Partnership
import org.taktik.icure.entities.embed.PatientHealthCareParty
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.logic.ContactLogic
import org.taktik.icure.logic.HealthcarePartyLogic
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.xml.sax.SAXException
import java.io.*

@org.springframework.stereotype.Service("sumehrLogicV2")
class SumehrLogicImpl(val contactLogic: ContactLogic, val healthcarePartyLogic: HealthcarePartyLogic, @Qualifier("sumehrExportV2") val sumehrExport: SumehrExport, @Qualifier("sumehrImportV2") val sumehrImport: SumehrImport) : SumehrLogic {
    override fun isSumehrValid(hcPartyId: String, patient: Patient, patientSecretForeignKeys: List<String>, excludedIds: List<String>, includeIrrelevantInformation: Boolean, services: List<Service>?, healthElements: List<HealthElement>?): SumehrStatus {
        val sumehrServiceIds = ArrayList<String>()
       sumehrServiceIds.addAll(contactLogic.listServiceIdsByTag(hcPartyId, patientSecretForeignKeys, "CD-TRANSACTION", "sumehr", null, null))

        if (sumehrServiceIds.isEmpty()) {
            return SumehrStatus.absent
        }

		val servicesByIds = mutableMapOf<String, Service>()
		val comparator = Comparator<Service> { a, b -> a.modified?.compareTo(b.modified ?: 0) ?: -1 }

		contactLogic.getServices(sumehrServiceIds).sortedWith(comparator).forEach { if (it.endOfLife != null) servicesByIds.remove(it.id) else servicesByIds[it.id!!] = it }

		if (servicesByIds.isEmpty()) {
			return SumehrStatus.outdated
		}

		return if (servicesByIds.values.sortedWith(comparator).last().comment == getSumehrMd5(hcPartyId, patient, patientSecretForeignKeys, excludedIds, includeIrrelevantInformation)) SumehrStatus.uptodate else SumehrStatus.outdated
    }

	override fun getSumehrMd5(hcPartyId: String, patient: Patient, patientSecretForeignKeys: List<String>, excludedIds: List<String>, includeIrrelevantInformation: Boolean) =
		sumehrExport.getMd5(hcPartyId, patient, patientSecretForeignKeys, excludedIds, includeIrrelevantInformation)

    override fun importSumehr(inputStream: InputStream, author: User, language: String, dest: Patient?, mappings: Map<String, List<ImportMapping>>, saveToDatabase: Boolean): List<ImportResult> {
        return sumehrImport.importSumehr(inputStream, author, language, mappings, saveToDatabase, dest)
    }

    override fun importSumehrByItemId(inputStream: InputStream, itemId: String, author: User, language: String, dest: Patient?, mappings: Map<String, List<ImportMapping>>, saveToDatabase: Boolean): List<ImportResult> {
        return sumehrImport.importSumehrByItemId(inputStream, itemId, author, language, mappings, saveToDatabase, dest)
    }

    override fun createSumehr(os: OutputStream, pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, comment: String, excludedIds: List<String>, includeIrrelevantInformation: Boolean, decryptor: AsyncDecrypt?, services: List<Service>?, healthElements: List<HealthElement>?, config: Config) = sumehrExport.createSumehr(os, pat, sfks, sender, recipient, language, comment, excludedIds, includeIrrelevantInformation, decryptor, services, healthElements, config)

	@Throws(IOException::class)
    override fun validateSumehr(os: OutputStream, pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, comment: String, excludedIds: List<String>, includeIrrelevantInformation: Boolean, decryptor: AsyncDecrypt?, services: List<Service>?, healthElements: List<HealthElement>?, config: Config) {
        val temp = File.createTempFile("temp", java.lang.Long.toString(System.nanoTime()))

        val sos = BufferedOutputStream(FileOutputStream(temp))
        sumehrExport.createSumehr(sos, pat, sfks, sender, recipient, language, comment, excludedIds, includeIrrelevantInformation, decryptor, services, healthElements, config)
        sos.close()

        try {
            val html = EhValidator.getHTMLReport(temp.absolutePath, EhValidator.Language.french, "Sumehr")
            val w = OutputStreamWriter(os, "UTF-8")
            w.write(html)
            w.close()
        } catch (e: SAXException) {
            throw IOException(e)
        }
    }

	override fun getAllServices(hcPartyId: String, sfks: List<String>, excludedIds: List<String>, includeIrrelevantInformation: Boolean, decryptor: AsyncDecrypt?)
        = sumehrExport.getAllServices(healthcarePartyLogic.getHcpHierarchyIds(healthcarePartyLogic.getHealthcareParty(hcPartyId)), sfks, excludedIds, includeIrrelevantInformation, decryptor)

	override fun getHealthElements(hcPartyId: String, sfks: List<String>, excludedIds: List<String>, includeIrrelevantInformation: Boolean): List<HealthElement> {
        return sumehrExport.getHealthElements(healthcarePartyLogic.getHcpHierarchyIds(healthcarePartyLogic.getHealthcareParty(hcPartyId)), sfks, excludedIds, includeIrrelevantInformation, HashSet())
    }

    override fun getContactPeople(hcPartyId: String, sfks: List<String>, excludedIds: List<String>, patientId: String): List<Partnership> {
        return sumehrExport.getContactPeople(healthcarePartyLogic.getHcpHierarchyIds(healthcarePartyLogic.getHealthcareParty(hcPartyId)), sfks, excludedIds, patientId)
    }

    override fun getPatientHealthcareParties(hcPartyId: String, sfks: List<String>, excludedIds: List<String>, patientId: String): List<PatientHealthCareParty> {
        return sumehrExport.getPatientHealthCareParties(healthcarePartyLogic.getHcpHierarchyIds(healthcarePartyLogic.getHealthcareParty(hcPartyId)), sfks, excludedIds, patientId)
    }
}
