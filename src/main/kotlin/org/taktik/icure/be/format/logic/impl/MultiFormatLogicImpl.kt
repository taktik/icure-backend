/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.be.format.logic.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.taktik.icure.be.format.logic.MultiFormatLogic
import org.taktik.icure.be.format.logic.ResultFormatLogic
import org.taktik.icure.dto.result.ResultInfo
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import java.io.IOException
import java.time.LocalDateTime
import java.util.function.Consumer

@Service
class MultiFormatLogicImpl(var engines: List<ResultFormatLogic>) : MultiFormatLogic {
    @Throws(IOException::class)
    override fun canHandle(doc: Document, enckeys: List<String>): Boolean {
        for (e in engines) {
            if (e.canHandle(doc, enckeys)) {
                return true
            }
        }
        return false
    }

    @Throws(IOException::class)
    override fun getInfos(doc: Document, full: Boolean, language: String, enckeys: List<String>): List<ResultInfo> {
        for (e in engines) {
            if (e.canHandle(doc, enckeys)) {
                val infos = e.getInfos(doc, full, language, enckeys)
                infos!!.forEach(Consumer { i: ResultInfo? -> i!!.engine = e.javaClass.name })
                return infos
            }
        }
        throw IllegalArgumentException("Invalid format")
    }

    @Throws(IOException::class)
    override suspend fun doImport(language: String, doc: Document, hcpId: String?, protocolIds: List<String>, formIds: List<String>, planOfActionId: String?, ctc: Contact, enckeys: List<String>): Contact? {
        for (e in engines) {
            if (e.canHandle(doc, enckeys)) {
                return e.doImport(language, doc, hcpId, protocolIds, formIds, planOfActionId, ctc, enckeys)
            }
        }
        throw IllegalArgumentException("Invalid format")
    }

    override fun doExport(sender: HealthcareParty?, recipient: HealthcareParty?, patient: Patient?, date: LocalDateTime?, ref: String?, text: String?) : Flow<DataBuffer> {
        throw UnsupportedOperationException()
    }

    override fun doExport(sender: HealthcareParty?, recipient: HealthcareParty?, patient: Patient?, date: LocalDateTime?, ref: String?, mimeType: String?, content: ByteArray?) : Flow<DataBuffer> {
        throw UnsupportedOperationException()
    }

}
