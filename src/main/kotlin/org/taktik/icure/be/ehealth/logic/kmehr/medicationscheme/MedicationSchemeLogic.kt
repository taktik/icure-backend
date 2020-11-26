package org.taktik.icure.be.ehealth.logic.kmehr.medicationscheme

import org.taktik.icure.dto.mapping.ImportMapping
import org.taktik.icure.dto.result.ImportResult
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.http.websocket.AsyncProgress
import java.io.InputStream
import java.io.OutputStream

interface MedicationSchemeLogic {

    fun importMedicationSchemeFile(inputStream: InputStream, author: User, language: String, dest: Patient?, mappings: Map<String, List<ImportMapping>>, saveToDatabase: Boolean): List<ImportResult>
    fun createMedicationSchemeExport(
            os: OutputStream,
            patient: Patient,
            sfks: List<String>,
            sender: HealthcareParty,
            language: String,
            recipientSafe: String,
            version: Int,
            decryptor: AsyncDecrypt?,
            progressor: AsyncProgress?
    )
    fun createMedicationSchemeExport(
            os: OutputStream,
            patient: Patient,
            sender: HealthcareParty,
            language: String,
            recipientSafe: String,
            version: Int,
            services: List<Service>,
            serviceAuthors: List<HealthcareParty>,
            progressor: AsyncProgress?
    )
}
