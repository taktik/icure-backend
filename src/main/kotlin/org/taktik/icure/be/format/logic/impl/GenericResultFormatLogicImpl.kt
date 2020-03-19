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
import org.taktik.icure.asynclogic.FormLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.be.format.logic.ResultFormatLogic
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.db.StringUtils.detectFrenchCp850Cp1252
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.entities.embed.ServiceLink
import org.taktik.icure.entities.embed.SubContact
import org.xml.sax.SAXException
import java.io.*
import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

abstract class GenericResultFormatLogicImpl(val healthcarePartyLogic: HealthcarePartyLogic, val formLogic: FormLogic) : ResultFormatLogic {
    protected var uuidGen = UUIDGenerator()

    override fun doExport(sender: HealthcareParty?, recipient: HealthcareParty?, patient: Patient?, date: LocalDateTime?, ref: String?, mimeType: String?, content: ByteArray?) : Flow<DataBuffer> {
        throw UnsupportedOperationException("Not implemented")
    }

    protected fun fillContactWithLines(ctc: Contact, lls: List<LaboLine>, planOfActionId: String?, hcpId: String?,
                                       protocolIds: List<String?>, formIds: List<String?>) {
        lls.forEach(Consumer { ll: LaboLine ->
            var formId: String? = null
            for (i in protocolIds.indices) {
                if ((protocolIds[i] == ll.ril?.protocol ?: ll.resultReference) || protocolIds.size == 1 && protocolIds[i]?.startsWith("***") == true) {
                    formId = formIds[i]
                }
            }
            val ssc = SubContact()
            ssc.responsible = hcpId
            ssc.descr = ll.labo
            ssc.protocol = ll.resultReference
            ssc.planOfActionId = planOfActionId
            ssc.status = ((if (ll.isResultLabResult) SubContact.STATUS_LABO_RESULT else SubContact.STATUS_PROTOCOL_RESULT)
                    or SubContact.STATUS_UNREAD or if (ll.ril != null && ll.ril!!.isComplete) SubContact.STATUS_COMPLETE else 0)
            ssc.formId = formId
            ssc.services = ll.services.stream().map { s: Service? -> ServiceLink(s!!.id) }.collect(Collectors.toList())
            ctc.services.addAll(ll.services)
            ctc.subContacts.add(ssc)
        })
    }

    @Throws(IOException::class)
    protected fun decodeRawData(rawData: ByteArray?): String? {
        var text: String? = null
        // Test BOM
// Test utf-16 byte order mark presence
        if (rawData != null) {
            val utf8Decoder = StandardCharsets.UTF_8.newDecoder()
            text = try {
                val decodedChars = utf8Decoder.decode(ByteBuffer.wrap(rawData))
                decodedChars.toString()
            } catch (e: CharacterCodingException) {
                val frenchCp850OrCp1252 = detectFrenchCp850Cp1252(rawData)
                val charset = if ("cp850" == frenchCp850OrCp1252) Charset.forName("cp850") else Charset.forName("cp1252")
                String(rawData, charset)
            }
        }
        return text
    }

    @Throws(ParserConfigurationException::class, IOException::class, SAXException::class)
    protected fun getXmlDocument(doc: Document, enckeys: List<String?>?): org.w3c.dom.Document {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        return dBuilder.parse(ByteArrayInputStream(doc.decryptAttachment(enckeys)))
    }

    @Throws(IOException::class)
    protected fun getBufferedReader(doc: Document, enckeys: List<String?>?): BufferedReader? {
        return decodeRawData(doc.decryptAttachment(enckeys))?. let { BufferedReader(StringReader(it)) }
    }

    class LaboLine {
        var labo: String? = null
        var resultReference: String? = null
        var fullLine: String? = null
        var labosList: MutableList<LaboResultLine?> = ArrayList()
        var protoList: MutableList<ProtocolLine?> = ArrayList()
        var ril: ResultsInfosLine? = null
        var pal: PatientAddressLine? = null
        var services: MutableList<Service?> = ArrayList()
        var isResultLabResult = false
    }

    inner class PatientLine {
        var lastName: String? = null
        var firstName: String? = null
        var dn: Timestamp? = null
        var sex: String? = null
        var protocol: String? = null

    }

    inner class PatientAddressLine {
        var protocol: String? = null
        var address: String? = null
        var number: String? = null
        var zipCode: String? = null
        var locality: String? = null

    }

    inner class PatientSSINLine {
        var protocol: String? = null
        var ssin: String? = null

    }

    inner class ProtocolLine {
        var protocol: String? = null
        var code: String? = null
        var text: String? = null

    }

    inner class LaboResultLine {
        var protocol: String? = null
        var analysisCode: String? = null
        var analysisType: String? = null
        var referenceValues: String? = null
        var unit: String? = null
        var severity: String? = null
        var value: String? = null

    }

    inner class ResultsInfosLine {
        var protocol: String? = null
        var demandDate: Instant? = null
        var isComplete = false

    }

    inner class Reference {
        var minValue: Double? = null
        var maxValue: Double? = null
        var unit: String? = null

    }
}
