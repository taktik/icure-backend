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

package org.taktik.icure.be.ehealth.logic.kmehr.diarynote

import java.io.InputStream
import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.domain.mapping.ImportMapping
import org.taktik.icure.domain.result.ImportResult
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.services.external.api.AsyncDecrypt

interface DiaryNoteLogic {
	fun createDiaryNote(pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, note: String?, tags: List<String>, contexts: List<String>, isPsy: Boolean, documentId: String?, attachmentId: String?, decryptor: AsyncDecrypt?): Flow<DataBuffer>

	//fun getDiaryNoteMd5(hcPartyId: String, patient: Patient, patientSecretForeignKeys: List<String>, excludedIds: List<String>): String
	fun importDiaryNote(inputStream: InputStream, author: User, language: String, dest: Patient? = null, mappings: Map<String, List<ImportMapping>> = HashMap()): List<ImportResult>
}
