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

package org.taktik.icure.be.ehealth.logic.kmehr.diarynote.impl.v20170901

//import org.taktik.icure.be.ehealth.dto.SumehrStatus
//import org.taktik.icure.be.ehealth.logic.kmehr.diarynote.impl.v20170901.DiaryNoteImport
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.beans.factory.annotation.Qualifier
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.be.ehealth.logic.kmehr.diarynote.DiaryNoteLogic
import org.taktik.icure.domain.mapping.ImportMapping
import org.taktik.icure.domain.result.ImportResult
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.services.external.api.AsyncDecrypt
import java.io.InputStream

@ExperimentalCoroutinesApi
@org.springframework.stereotype.Service("diaryNoteLogic")
class DiaryNoteLogicImpl(val contactLogic: ContactLogic, @Qualifier("dairyNoteExport") val diaryNoteExport: DiaryNoteExport, @Qualifier("diaryNoteImport") val diaryNoteImport: DiaryNoteImport) : DiaryNoteLogic {
    override fun createDiaryNote(pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, note: String?, tags: List<String>, contexts: List<String>, isPsy: Boolean, documentId: String?, attachmentId: String?, decryptor: AsyncDecrypt?) = flow { emitAll(diaryNoteExport.createDiaryNote(pat, sfks, sender, recipient, language, note, tags, contexts, isPsy, documentId, attachmentId, decryptor)) }

    override fun importDiaryNote(inputStream: InputStream, author: User, language: String, dest: Patient?, mappings: Map<String, List<ImportMapping>>): List<ImportResult> {
        return diaryNoteImport.importDiaryNote(inputStream, author, language, mappings, dest)
    }
}
