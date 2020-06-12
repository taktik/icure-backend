package org.taktik.icure.be.ehealth.logic.kmehr

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream
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
import java.io.OutputStreamWriter
import java.nio.ByteBuffer
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller

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

suspend fun byteBufferArrayToInputStream(bytes : Flow<ByteBuffer>): ByteBufferBackedInputStream {
    val toList = bytes.toList()
    var sum = 0
    toList.forEach { l -> sum += l.limit()}
    val fullB = ByteBuffer.allocate(sum)
    toList.forEach { l -> fullB.put(l) }
    return ByteBufferBackedInputStream(fullB)
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
