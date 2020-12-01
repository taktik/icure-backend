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

package org.taktik.icure.be.ehealth.logic.kmehr.medicationscheme.impl.v20161201

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.icure.be.ehealth.logic.kmehr.medicationscheme.MedicationSchemeLogic
import org.taktik.icure.domain.mapping.ImportMapping
import org.taktik.icure.domain.result.ImportResult
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.http.websocket.AsyncProgress
import java.nio.ByteBuffer

/**
 * @author Bernard Paulus on 24/05/17.
 */
@Service
class MedicationSchemeLogicImpl(val medicationSchemeExport: MedicationSchemeExport,
                                   val medicationSchemeImport: MedicationSchemeImport) : MedicationSchemeLogic {

    override suspend fun importMedicationSchemeFile(inputData : Flow<ByteBuffer>,
                                                    author: User,
                                                    language: String,
                                                    dest: Patient?,
                                                    mappings: Map<String, List<ImportMapping>>,
                                                    saveToDatabase: Boolean
    ) : List<ImportResult> {
        return medicationSchemeImport.importMedicationSchemeFile(inputData, author, language, mappings, saveToDatabase, dest)
    }

    override fun createMedicationSchemeExport(
            patient: Patient,
            sfks: List<String>,
            sender: HealthcareParty,
            language: String,
            recipientSafe: String,
            version: Int,
            decryptor: AsyncDecrypt?,
            progressor: AsyncProgress?
    ) =
        medicationSchemeExport.exportMedicationScheme(patient, sfks, sender, language, recipientSafe, version, null, decryptor, progressor)


    override fun createMedicationSchemeExport(
            patient: Patient,
            sender: HealthcareParty,
            language: String,
            recipientSafe: String,
            version: Int,
            services: List<org.taktik.icure.entities.embed.Service>,
            progressor: AsyncProgress?
    ) =
        medicationSchemeExport.exportMedicationScheme(patient, listOf(), sender, language, recipientSafe, version, services, null, progressor)

}
