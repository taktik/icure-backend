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

package org.taktik.icure.be.ehealth.logic.kmehr.diarynote.impl.v20170901

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter

import be.fgov.ehealth.ehvalidator.core.EhValidator
import org.springframework.beans.factory.annotation.Qualifier
import org.taktik.icure.be.ehealth.logic.kmehr.Config
//import org.taktik.icure.be.ehealth.dto.SumehrStatus
import org.taktik.icure.be.ehealth.logic.kmehr.diarynote.DiaryNoteLogic
//import org.taktik.icure.be.ehealth.logic.kmehr.diarynote.impl.v20170901.DiaryNoteImport
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
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.xml.sax.SAXException
import java.io.InputStream

@org.springframework.stereotype.Service("diaryNoteLogic")
class DiaryNoteLogicImpl(val contactLogic: ContactLogic, @Qualifier("dairyNoteExport") val diaryNoteExport: DiaryNoteExport, @Qualifier("diaryNoteImport") val diaryNoteImport: DiaryNoteImport) : DiaryNoteLogic {
    override fun createDiaryNote(os: OutputStream, pat: Patient, sfks: List<String>, sender: HealthcareParty, recipient: HealthcareParty, language: String, note: String?, tags: List<String>, contexts: List<String>, isPsy: Boolean, documentId: String?, attachmentId: String?, decryptor: AsyncDecrypt?, config: Config) = diaryNoteExport.createDiaryNote(os, pat, sfks, sender, recipient, language, note, tags, contexts, isPsy, documentId, attachmentId, decryptor, config)

    override fun importDiaryNote(inputStream: InputStream, author: User, language: String, dest: Patient?, mappings: Map<String, List<ImportMapping>>): List<ImportResult> {
        return diaryNoteImport.importDiaryNote(inputStream, author, language, mappings, dest)
    }
}
