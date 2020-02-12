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

package org.taktik.icure.services.external.rest.v1.wscontrollers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import ma.glasnost.orika.MapperFacade
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.be.ehealth.logic.kmehr.diarynote.DiaryNoteLogic
import org.taktik.icure.be.ehealth.logic.kmehr.medicationscheme.MedicationSchemeLogic
import org.taktik.icure.be.ehealth.logic.kmehr.smf.SoftwareMedicalFileLogic
import org.taktik.icure.be.ehealth.logic.kmehr.sumehr.SumehrLogic
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.services.external.http.websocket.KmehrFileOperation
import org.taktik.icure.services.external.http.websocket.WebSocketOperation
import org.taktik.icure.services.external.http.websocket.WebSocketParam
import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.DiaryNoteExportInfoDto
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.MedicationSchemeExportInfoDto
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.SoftwareMedicalFileExportDto
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.SumehrExportInfoDto
import reactor.core.publisher.toFlux
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

@RestController("/ws/be_kmehr")
class KmehrWsController(private var mapper: MapperFacade,
                        private val sessionLogic: AsyncSessionLogic,
                        private val sumehrLogicV1: SumehrLogic,
                        private val sumehrLogicV2: SumehrLogic,
                        private val diaryNoteLogic: DiaryNoteLogic,
                        private val softwareMedicalFileLogic: SoftwareMedicalFileLogic,
                        private val medicationSchemeLogic: MedicationSchemeLogic,
                        private val healthcarePartyLogic: HealthcarePartyLogic,
                        private val patientLogic: PatientLogic) {

    @RequestMapping("/generateDiaryNote")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun generateDiaryNote(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: DiaryNoteExportInfoDto, operation: KmehrFileOperation) = mono {
        try {
            val patient = patientLogic.getPatient(patientId)
            val healthcareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
            patient?.let {
                healthcareParty?.let { it1 ->
                    operation.binaryResponse(
                            diaryNoteLogic.createDiaryNote(it, info.secretForeignKeys, it1, mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.note, info.tags, info.contexts, info.psy, info.documentId, info.attachmentId, operation)
                    )
                }
            }
        } catch (e: Exception) {
            operation.errorResponse(e)
        }
    }

    @RequestMapping("/generateSumehr")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun generateSumehr(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: SumehrExportInfoDto, operation: KmehrFileOperation) = mono {
        try {
            val patient = patientLogic.getPatient(patientId)
            val healthcareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
            patient?.let {
                healthcareParty?.let { it1 ->
                    operation.binaryResponse(sumehrLogicV1.createSumehr( it, info.secretForeignKeys,
                            it1,
                            mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, if (info.includeIrrelevantInformation == null) false else info.includeIrrelevantInformation, operation)
                    )
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                operation.errorResponse(e)
            }
        }
    }

    @RequestMapping("/validateSumehr")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun validateSumehr(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: SumehrExportInfoDto, operation: KmehrFileOperation) = mono {
        try {
            val patient = patientLogic.getPatient(patientId)
            val healthcareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
            patient?.let { healthcareParty?.let { it1 -> operation.binaryResponse(sumehrLogicV1.validateSumehr( it, info.secretForeignKeys, it1, mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, if (info.includeIrrelevantInformation == null) false else info.includeIrrelevantInformation, operation)) } }
        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                operation.errorResponse(e)
            }
        }
    }

    @RequestMapping("/generateSumehrV2")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun generateSumehrV2(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: SumehrExportInfoDto, operation: KmehrFileOperation) = mono {
        try {
            val patient = patientLogic.getPatient(patientId)
            val healthcareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
            patient?.let {
                healthcareParty?.let { it1 ->
                    operation.binaryResponse(
                            sumehrLogicV2.createSumehr( it, info.secretForeignKeys,
                                    it1,
                                    mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, if (info.includeIrrelevantInformation == null) false else info.includeIrrelevantInformation, operation)
                    )
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                operation.errorResponse(e)
            }
        }
    }

    @RequestMapping("/generateSumehrV2JSON")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun generateSumehrV2JSON(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: SumehrExportInfoDto, @WebSocketParam("asJson") asJson: Boolean?, operation: KmehrFileOperation) = mono {
        try {
            val patient = patientLogic.getPatient(patientId)
            val healthcareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
            patient?.let {
                healthcareParty?.let { it1 ->
                    operation.binaryResponse(
                            sumehrLogicV2.createSumehr(
                                    it,
                                    info.secretForeignKeys,
                                    it1,
                                    mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java),
                                    language,
                                    info.comment,
                                    info.excludedIds,
                                    if (info.includeIrrelevantInformation == null) false else info.includeIrrelevantInformation,
                                    operation
                            )
                    )
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                operation.errorResponse(e)
            }
        }
    }

    @RequestMapping("/validateSumehrV2")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun validateSumehrV2(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: SumehrExportInfoDto, operation: KmehrFileOperation) = mono {
        try {
            val patient = patientLogic.getPatient(patientId)
            val healthcareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
            patient?.let {
                healthcareParty?.let { it1 ->
                    operation.binaryResponse(
                            sumehrLogicV2.validateSumehr(
                                    it,
                                    info.secretForeignKeys,
                                    it1,
                                    mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java),
                                    language,
                                    info.comment,
                                    info.excludedIds,
                                    if (info.includeIrrelevantInformation == null) false else info.includeIrrelevantInformation,
                                    operation
                            )
                    )
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                operation.errorResponse(e)
            }
        }
    }

    @RequestMapping("/generateSmf")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun generateSmfExport(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: SoftwareMedicalFileExportDto, operation: KmehrFileOperation) = mono {
        try {
            val patient = patientLogic.getPatient(patientId)
            val healthcareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
            patient?.let { pat ->
                healthcareParty?.let { hcp ->
                    operation.binaryResponse(
                            softwareMedicalFileLogic.createSmfExport(
                                    pat,
                                    info.secretForeignKeys,
                                    hcp,
                                    language,
                                    operation,
                                    operation
                            )

                    )
                }
            }

        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                operation.errorResponse(e)
            }
        }
    }

    @RequestMapping("/generateMedicationScheme")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun generateMedicationSchemeExport(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: MedicationSchemeExportInfoDto, @WebSocketParam("recipientSafe") recipientSafe: String, @WebSocketParam("version") version: Int, operation: KmehrFileOperation) = mono {
        try {
            val patient = patientLogic.getPatient(patientId)
            val hcParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
            patient?.let {
                hcParty?.let { it1 ->
                    operation.binaryResponse(
                            medicationSchemeLogic.createMedicationSchemeExport(
                                    it,
                                    info.secretForeignKeys,
                                    it1,
                                    language,
                                    recipientSafe,
                                    version,
                                    operation,
                                    null
                            )
                    )
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                operation.errorResponse(e)
            }
        }
    }
}
