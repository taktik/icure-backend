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

package org.taktik.icure.be.ehealth.logic.kmehr.smf

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.domain.mapping.ImportMapping
import org.taktik.icure.domain.result.CheckSMFPatientResult
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
interface SoftwareMedicalFileLogic {
	fun createSmfExport(patient: Patient, sfks: List<String>, sender: HealthcareParty, language: String, decryptor: AsyncDecrypt?, progressor: AsyncProgress?, config: Config): Flow<DataBuffer>
    suspend fun importSmfFile(inputData: ByteArray,
                              author: User,
                              language: String,
                              dryRun: Boolean,
                              dest: Patient? = null,
                              mappings: Map<String, List<ImportMapping>> = HashMap()): List<ImportResult>

    suspend fun checkIfSMFPatientsExists(inputData : Flow<ByteBuffer>,
                                         author: User,
                                         language: String,
                                         dest: Patient?,
                                         mappings: Map<String, List<ImportMapping>>
    ) : List<CheckSMFPatientResult>
}
