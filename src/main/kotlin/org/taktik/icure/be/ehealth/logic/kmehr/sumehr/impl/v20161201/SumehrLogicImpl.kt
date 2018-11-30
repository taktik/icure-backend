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

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter

import be.fgov.ehealth.ehvalidator.core.EhValidator
import org.springframework.stereotype.Service
import org.taktik.icure.be.ehealth.dto.SumehrStatus
import org.taktik.icure.be.ehealth.logic.kmehr.sumehr.SumehrLogic
import org.taktik.icure.dto.mapping.ImportMapping
import org.taktik.icure.dto.result.ImportResult
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.logic.ContactLogic
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.xml.sax.SAXException
import java.io.InputStream
import java.util.*

class SumehrLogicImpl(private val contactLogic: ContactLogic, private val sumehrExport: SumehrExport) : SumehrLogic {

    override fun isSumehrValid(hcPartyId: String, patient: Patient, patientSecretForeignKeys: List<String>): SumehrStatus {
        val sumehrServiceIds = ArrayList<String>()
        patientSecretForeignKeys.forEach { k -> sumehrServiceIds.addAll(contactLogic!!.findServicesByTag(hcPartyId, k, "CD-TRANSACTION", "sumehr", null, null)) }

        if (sumehrServiceIds.size == 0) {
            return SumehrStatus.absent
        }
        val services = contactLogic!!.getServices(sumehrServiceIds).sortedWith(Comparator<org.taktik.icure.entities.embed.Service> { a, b -> if (a.modified == null) 1 else -a.modified!!.compareTo(b.modified?:0) })
        return if (services[0].comment == getSumehrMd5(hcPartyId, patient, patientSecretForeignKeys)) SumehrStatus.uptodate else SumehrStatus.outdated
    }

	override fun getSumehrMd5(hcPartyId: String, patient: Patient, patientSecretForeignKeys: List<String>) =
		sumehrExport.getMd5(hcPartyId, patient, patientSecretForeignKeys)

    override fun importSumehr(inputStream: InputStream, author: User, language: String, dest: Patient?, mappings: Map<String, List<ImportMapping>>): List<ImportResult> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createSumehr(os: OutputStream, pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, comment: String, decryptor: AsyncDecrypt?) = sumehrExport.createSumehr(os, pat, sfks, sender, recipient, language, comment, decryptor)

	override fun createSumehrPlusPlus(os: OutputStream, pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, comment: String, decryptor: AsyncDecrypt?) = sumehrExport.createSumehrPlusPlus(os, pat, sfks, sender, recipient, language, comment, decryptor)

	@Throws(IOException::class)
    override fun validateSumehr(os: OutputStream, pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, comment: String, decryptor: AsyncDecrypt?) {
        val temp = File.createTempFile("temp", java.lang.Long.toString(System.nanoTime()))

        val sos = BufferedOutputStream(FileOutputStream(temp))
        sumehrExport.createSumehr(sos, pat, sfks, sender, recipient, language, comment, decryptor)
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

	override fun getAllServices(hcPartyId: String, sfks: List<String>, decryptor: AsyncDecrypt?)
        = sumehrExport.getAllServices(hcPartyId, sfks, decryptor)

	override fun getAllServicesPlusPlus(hcPartyId: String, sfks: List<String>, decryptor: AsyncDecrypt?)
        = sumehrExport.getAllServicesPlusPlus(hcPartyId, sfks, decryptor)

	override fun getHealthElements(hcPartyId: String, sfks: List<String>): List<HealthElement> {
        return sumehrExport.getHealthElements(hcPartyId, sfks)
    }
}
