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

package org.taktik.icure.be.ehealth.logic.kmehr.medicationscheme.impl.v20161201

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.medicationscheme.MedicationSchemeLogic
import org.taktik.icure.dto.mapping.ImportMapping
import org.taktik.icure.dto.result.ImportResult
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.http.websocket.AsyncProgress
import java.io.InputStream
import java.io.OutputStream
import java.time.Instant

/**
 * @author Bernard Paulus on 24/05/17.
 */
@Service
class MedicationSchemeLogicImpl(val medicationSchemeExport: MedicationSchemeExport,
                                   val medicationSchemeImport: MedicationSchemeImport) : MedicationSchemeLogic {
    @Value("\${icure.version}")
    internal val ICUREVERSION: String = "4.0.0"

    override fun importMedicationSchemeFile(inputStream: InputStream,
                                            author: User,
                                            language: String,
                                            dest: Patient?,
                                            mappings: Map<String, List<ImportMapping>>,
                                            saveToDatabase: Boolean
    ) : List<ImportResult> {
        return medicationSchemeImport.importMedicationSchemeFile(inputStream, author, language, mappings, saveToDatabase, dest)
    }

    override fun createMedicationSchemeExport(
            os: OutputStream,
            patient: Patient,
            sfks: List<String>,
            sender: HealthcareParty,
            language: String,
            recipientSafe: String,
            version: Int,
            decryptor: AsyncDecrypt?,
            progressor: AsyncProgress?
    ) {
        medicationSchemeExport.exportMedicationScheme(os, patient, sfks, sender, language, recipientSafe, version, null, null, decryptor, progressor)
	}

    override fun createMedicationSchemeExport(
            os: OutputStream,
            patient: Patient,
            sender: HealthcareParty,
            language: String,
            recipientSafe: String,
            version: Int,
            services: List<org.taktik.icure.entities.embed.Service>,
            serviceAuthors: List<HealthcareParty>,
            timeZone: String?,
            progressor: AsyncProgress?
    ) {
        medicationSchemeExport.exportMedicationScheme(os, patient, listOf(), sender, language, recipientSafe, version, services, serviceAuthors, null, progressor, Config(_kmehrId = System.currentTimeMillis().toString(),
                date = Utils.makeXGC(Instant.now().toEpochMilli(), unsetMillis = false, setTimeZone = false, timeZone = timeZone ?: "Europe/Brussels")!!,
                time = Utils.makeXGC(Instant.now().toEpochMilli(), unsetMillis = true, setTimeZone = false, timeZone = timeZone ?: "Europe/Brussels")!!,
                soft = Config.Software(name = "iCure", version = ICUREVERSION),
                clinicalSummaryType = "",
                defaultLanguage = "en"
        ))
    }
}
