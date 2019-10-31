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

package org.taktik.icure.services.external .rest.v1.wscontrollers

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer

import javax.ws.rs.Path

import ma.glasnost.orika.MapperFacade
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.be.ehealth.logic.kmehr.smf.SoftwareMedicalFileLogic
import org.taktik.icure.be.ehealth.logic.kmehr.sumehr.SumehrLogic
import org.taktik.icure.be.ehealth.logic.kmehr.diarynote.DiaryNoteLogic
import org.taktik.icure.be.ehealth.logic.kmehr.medicationscheme.MedicationSchemeLogic
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.logic.HealthcarePartyLogic
import org.taktik.icure.logic.PatientLogic
import org.taktik.icure.logic.SessionLogic
import org.taktik.icure.services.external.http.websocket.KmehrFileOperation
import org.taktik.icure.services.external.http.websocket.WebSocketOperation
import org.taktik.icure.services.external.http.websocket.WebSocketParam
import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.SoftwareMedicalFileExportDto
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.SumehrExportInfoDto
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.DiaryNoteExportInfoDto
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.MedicationSchemeExportInfoDto


@RestController("/ws/be_kmehr")
class KmehrWsController(private var mapper: MapperFacade,
    private val sessionLogic: SessionLogic,
    private val sumehrLogicV1: SumehrLogic,
    private val sumehrLogicV2: SumehrLogic,
    private val diaryNoteLogic: DiaryNoteLogic,
    private val softwareMedicalFileLogic: SoftwareMedicalFileLogic,
    private val medicationSchemeLogic: MedicationSchemeLogic,
    private val healthcarePartyLogic: HealthcarePartyLogic,
    private val patientLogic: PatientLogic) {

    @Path("/generateDiaryNote")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun generateDiaryNote(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: DiaryNoteExportInfoDto, operation: KmehrFileOperation) {
        val bos = ByteArrayOutputStream(10000)
        try {
            diaryNoteLogic.createDiaryNote(bos, patientLogic.getPatient(patientId), info.secretForeignKeys,
                    healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId),
                    mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.note, info.tags, info.contexts, info.psy, info.documentId, info.attachmentId, operation)
            operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()))
            bos.close()
        } catch (e: Exception) {
            operation.errorResponse(e)
        }
    }

    @Path("/generateSumehr")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun generateSumehr(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: SumehrExportInfoDto, operation: KmehrFileOperation) {
        val bos = ByteArrayOutputStream(10000)
        try {
            sumehrLogicV1.createSumehr(bos, patientLogic.getPatient(patientId), info.secretForeignKeys,
                    healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId),
                    mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, if (info.includeIrrelevantInformation == null) false else info.includeIrrelevantInformation, operation)
            operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()))
            bos.close()
        } catch (e: Exception) {
            operation.errorResponse(e)
        }
    }

    @Path("/validateSumehr")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun validateSumehr(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: SumehrExportInfoDto, operation: KmehrFileOperation) {
        val bos = ByteArrayOutputStream(10000)
        try {
            sumehrLogicV1.validateSumehr(bos, patientLogic.getPatient(patientId), info.secretForeignKeys, healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId), mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, if (info.includeIrrelevantInformation == null) false else info.includeIrrelevantInformation, operation)
            operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()))
            bos.close()
        } catch (e: Exception) {
            operation.errorResponse(e)
        }
    }

    @Path("/generateSumehrV2")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun generateSumehrV2(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: SumehrExportInfoDto, operation: KmehrFileOperation) {
        val bos = ByteArrayOutputStream(10000)
        try {
            sumehrLogicV2.createSumehr(bos, patientLogic.getPatient(patientId), info.secretForeignKeys,
                    healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId),
                    mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, if (info.includeIrrelevantInformation == null) false else info.includeIrrelevantInformation, operation)
            operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()))
            bos.close()
        } catch (e: Exception) {
            operation.errorResponse(e)
        }
    }

    @Path("/generateSumehrV2JSON")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun generateSumehrV2JSON(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: SumehrExportInfoDto, @WebSocketParam("asJson") asJson: Boolean?, operation: KmehrFileOperation) {
        val bos = ByteArrayOutputStream(10000)
        try {
            sumehrLogicV2.createSumehr(bos, patientLogic.getPatient(patientId), info.secretForeignKeys,
                    healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId),
                    mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, if (info.includeIrrelevantInformation == null) false else info.includeIrrelevantInformation, operation)
            operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()))
            bos.close()
        } catch (e: Exception) {
            operation.errorResponse(e)
        }
    }

    @Path("/validateSumehrV2")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun validateSumehrV2(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: SumehrExportInfoDto, operation: KmehrFileOperation) {
        val bos = ByteArrayOutputStream(10000)
        try {
            sumehrLogicV2.validateSumehr(bos, patientLogic.getPatient(patientId), info.secretForeignKeys, healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId), mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, if (info.includeIrrelevantInformation == null) false else info.includeIrrelevantInformation, operation)
            operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()))
            bos.close()
        } catch (e: Exception) {
            operation.errorResponse(e)
        }
    }

    @Path("/generateSmf")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun generateSmfExport(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: SoftwareMedicalFileExportDto, operation: KmehrFileOperation) {
        val bos = ByteArrayOutputStream(10000)
        try {
            softwareMedicalFileLogic.createSmfExport(bos, patientLogic.getPatient(patientId), info.secretForeignKeys, healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId), language, operation, operation)
            operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()))
            bos.close()
        } catch (e: Exception) {
            operation.errorResponse(e)
        }
    }

    @Path("/generateMedicationScheme")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun generateMedicationSchemeExport(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: MedicationSchemeExportInfoDto, @WebSocketParam("version") version: Int, operation: KmehrFileOperation) {
        val bos = ByteArrayOutputStream(10000)
        try {
            medicationSchemeLogic.createMedicationSchemeExport(bos, patientLogic.getPatient(patientId), info.secretForeignKeys,
                    healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId),
                    language, version, operation, null)
            operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()))
            bos.close()
        } catch (e: Exception) {
            operation.errorResponse(e)
        }
    }
}
