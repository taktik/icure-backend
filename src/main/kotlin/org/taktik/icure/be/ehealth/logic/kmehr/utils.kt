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

package org.taktik.icure.be.ehealth.logic.kmehr

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.taktik.icure.be.ehealth.dto.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.FolderType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.PatientHealthCareParty
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter
import java.nio.ByteBuffer
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import kotlin.experimental.and

fun validSsinOrNull(ssin : String?): String? {
    val res : String
    return if(ssin == null) {
        null
    } else {
        res = ssin.replace(" ", "").replace("-", "").replace(".", "").replace("/", "")
        if(res.length == 11) {
            res
        } else {
            null
        }
    }
}

fun validNihiiOrNull(nihii : String?): String? {
    val res : String
    return if(nihii == null) {
        null
    } else {
        res = nihii.replace(" ", "").replace("-", "").replace(".", "").replace("/", "")
        if(res.length == 11) {
            res
        } else {
            null
        }
    }
}

suspend fun Flow<ByteBuffer>.toInputStream(): InputStream {
    val buffers = this.toList()

    return object : InputStream() {
        var idx = 0
        val ff: Byte = 0xFF.toByte()
        override fun available(): Int = buffers.subList(idx, buffers.size).fold(0) { sum, bb -> sum + bb.remaining() }

        @Throws(IOException::class)
        override fun read(): Int = if (buffers[idx].hasRemaining()) (buffers[idx].get() and ff).toInt() else {
            if (idx < buffers.size - 1) {
                idx++
                read()
            } else -1
        }

        @Throws(IOException::class)
        override fun read(bytes: ByteArray?, off: Int, len: Int): Int = buffers[idx].let { buf ->
            when {
                len == 0 -> 0
                !buf.hasRemaining() -> {
                    if (idx < buffers.size - 1) {
                        idx++
                        read(bytes, off, len)
                    } else -1
                }
                else -> {
                    val read = len.coerceAtMost(buf.remaining())
                    buf.get(bytes, off, read)
                    if (len == read) read else read + read(bytes, off + read, len - read).coerceAtLeast(0)
                }
            }
        }
    }
}

fun emitMessage(folder : FolderType, message: Kmehrmessage): Flow<DataBuffer>{
    val os = ByteArrayOutputStream(10000)
    message.folders.add(folder)

    val jaxbMarshaller = JAXBContext.newInstance(Kmehrmessage::class.java).createMarshaller()
    // output pretty printed
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8")
    jaxbMarshaller.marshal(message, OutputStreamWriter(os, "UTF-8"))
    return DataBufferUtils.read(ByteArrayResource(os.toByteArray()), DefaultDataBufferFactory(), 10000).asFlow()
}

fun emitMessage(folder : org.taktik.icure.be.ehealth.dto.kmehr.v20110701.be.fgov.ehealth.standards.kmehr.schema.v1.FolderType, message: org.taktik.icure.be.ehealth.dto.kmehr.v20110701.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage): Flow<DataBuffer>{
    val os = ByteArrayOutputStream(10000)
    message.folders.add(folder)

    val jaxbMarshaller = JAXBContext.newInstance(org.taktik.icure.be.ehealth.dto.kmehr.v20110701.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java).createMarshaller()
    // output pretty printed
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8")
    jaxbMarshaller.marshal(message, OutputStreamWriter(os, "UTF-8"))
    return DataBufferUtils.read(ByteArrayResource(os.toByteArray()), DefaultDataBufferFactory(), 10000).asFlow()
}


fun emitMessage(folder: org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.FolderType, message: org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage): Flow<DataBuffer> {
    val os = ByteArrayOutputStream(10000)
    message.folders.add(folder)

    val jaxbMarshaller = JAXBContext.newInstance(org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java).createMarshaller()
    // output pretty printed
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8")
    jaxbMarshaller.marshal(message, OutputStreamWriter(os, "UTF-8"))
    return DataBufferUtils.read(ByteArrayResource(os.toByteArray()), DefaultDataBufferFactory(), 10000).asFlow()
}

fun emitMessage(folder: org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.FolderType, message: org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage): Flow<DataBuffer> {
    val os = ByteArrayOutputStream(10000)
    message.folders.add(folder)

    val jaxbMarshaller = JAXBContext.newInstance(org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java).createMarshaller()
    // output pretty printed
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8")
    jaxbMarshaller.marshal(message, OutputStreamWriter(os, "UTF-8"))
    return DataBufferUtils.read(ByteArrayResource(os.toByteArray()), DefaultDataBufferFactory(), 10000).asFlow()
}

fun Patient.getSignature() = DigestUtils.md5Hex(
        "${this.firstName}:${this.lastName}:${this.patientHealthCareParties.find(PatientHealthCareParty::referral)?.let { "" + it.healthcarePartyId + it.referralPeriods.last().startDate + it.referralPeriods.last().endDate } ?: ""}:${this.dateOfBirth}:${this.dateOfDeath}:${this.ssin}"
)
