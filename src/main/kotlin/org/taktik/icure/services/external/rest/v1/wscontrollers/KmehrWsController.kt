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
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.be.ehealth.dto.kmehr.v20110701.Utils.makeXGC
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.diarynote.DiaryNoteLogic
import org.taktik.icure.be.ehealth.logic.kmehr.medicationscheme.MedicationSchemeLogic
import org.taktik.icure.be.ehealth.logic.kmehr.smf.SoftwareMedicalFileLogic
import org.taktik.icure.be.ehealth.logic.kmehr.sumehr.SumehrLogic
import org.taktik.icure.services.external.http.websocket.KmehrFileOperation
import org.taktik.icure.services.external.http.websocket.WebSocketOperation
import org.taktik.icure.services.external.http.websocket.WebSocketParam
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.DiaryNoteExportInfoDto
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.MedicationSchemeExportInfoDto
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.SoftwareMedicalFileExportDto
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.SumehrExportInfoDto
import org.taktik.icure.services.external.rest.v1.mapper.HealthcarePartyMapper
import reactor.core.publisher.Mono
import java.time.Instant

@RestController("/ws/be_kmehr")
class KmehrWsController(private val sessionLogic: AsyncSessionLogic,
                        private val sumehrLogicV1: SumehrLogic,
                        private val sumehrLogicV2: SumehrLogic,
                        private val diaryNoteLogic: DiaryNoteLogic,
                        private val softwareMedicalFileLogic: SoftwareMedicalFileLogic,
                        private val medicationSchemeLogic: MedicationSchemeLogic,
                        private val healthcarePartyLogic: HealthcarePartyLogic,
                        private val patientLogic: PatientLogic,
                        private val healthcarePartyMapper: HealthcarePartyMapper
) {

    @Value("\${icure.version}")
    internal val ICUREVERSION: String = "4.0.0"
    internal val log = LogFactory.getLog(KmehrWsController::class.java)

    @RequestMapping("/generateDiaryNote")
    @WebSocketOperation(adapterClass = KmehrFileOperation::class)
    fun generateDiaryNote(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: DiaryNoteExportInfoDto, operation: KmehrFileOperation) = mono {
        try {
            val patient = patientLogic.getPatient(patientId)
            val healthcareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
            patient?.let {
                healthcareParty?.let { it1 ->
                    operation.binaryResponse(
                            diaryNoteLogic.createDiaryNote(it, info.secretForeignKeys, it1, healthcarePartyMapper.map(info.recipient!!), language, info.note, info.tags, info.contexts, info.psy ?: false, info.documentId, info.attachmentId, operation)
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
                            healthcarePartyMapper.map(info.recipient!!), language, info.comment, info.excludedIds, info.includeIrrelevantInformation ?: false, operation, null, null, Config(
                            "" + System.currentTimeMillis(),
                            makeXGC(Instant.now().toEpochMilli(), true),
                            makeXGC(Instant.now().toEpochMilli(), true),
                            Config.Software(info.softwareName ?: "iCure", info.softwareVersion ?: ICUREVERSION),
                            "",
                            "en",
                            Config.Format.SUMEHR
                    ))
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
            patient?.let { healthcareParty?.let { it1 -> operation.binaryResponse(sumehrLogicV1.validateSumehr( it, info.secretForeignKeys, it1, healthcarePartyMapper.map(info.recipient!!), language, info.comment, info.excludedIds, info.includeIrrelevantInformation ?: false, operation, null, null, Config(
                    "" + System.currentTimeMillis(),
                    makeXGC(Instant.now().toEpochMilli(), true),
                    makeXGC(Instant.now().toEpochMilli(), true),
                    Config.Software(info.softwareName ?: "iCure", info.softwareVersion ?: ICUREVERSION),
                    "",
                    "en",
                    Config.Format.SUMEHR
            ))) } }
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
                                    healthcarePartyMapper.map(info.recipient!!), language, info.comment, info.excludedIds, info.includeIrrelevantInformation ?: false, operation, null, null, Config(
                                    "" + System.currentTimeMillis(),
                                    makeXGC(Instant.now().toEpochMilli(), true),
                                    makeXGC(Instant.now().toEpochMilli(), true),
                                    Config.Software(info.softwareName ?: "iCure", info.softwareVersion ?: ICUREVERSION),
                                    "",
                                    "en",
                                    Config.Format.SUMEHR
                            ))
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
                                    healthcarePartyMapper.map(info.recipient!!),
                                    language,
                                    info.comment,
                                    info.excludedIds,
                                    info.includeIrrelevantInformation ?: false,
                                    operation, null, null, Config(
                                    "" + System.currentTimeMillis(),
                                    makeXGC(Instant.now().toEpochMilli(), true),
                                    makeXGC(Instant.now().toEpochMilli(), true),
                                    Config.Software(info.softwareName ?: "iCure", info.softwareVersion ?: ICUREVERSION),
                                    "",
                                    "en",
                                    Config.Format.SUMEHR
                            )
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
                                    healthcarePartyMapper.map(info.recipient!!),
                                    language,
                                    info.comment,
                                    info.excludedIds,
                                    info.includeIrrelevantInformation ?: false,
                                    operation, null, null, Config(
                                    "" + System.currentTimeMillis(),
                                    makeXGC(Instant.now().toEpochMilli(), true),
                                    makeXGC(Instant.now().toEpochMilli(), true),
                                    Config.Software(info.softwareName ?: "iCure", info.softwareVersion ?: ICUREVERSION),
                                    "",
                                    "en",
                                    Config.Format.SUMEHR
                            )
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
    fun generateSmfExport(@WebSocketParam("patientId") patientId: String, @WebSocketParam("language") language: String, @WebSocketParam("info") info: SoftwareMedicalFileExportDto, operation: KmehrFileOperation): Mono<Unit> {
        return mono {
            try {
                val patient = patientLogic.getPatient(patientId)
                val healthcareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
                patient?.let { pat ->
                    healthcareParty?.let { hcp ->
                        try {
                            val smfExport = softwareMedicalFileLogic.createSmfExport(
                                    pat,
                                    info.secretForeignKeys,
                                    hcp,
                                    language,
                                    operation,
                                    operation,
                                    Config(
                                            "" + System.currentTimeMillis(),
                                            makeXGC(Instant.now().toEpochMilli(), true),
                                            makeXGC(Instant.now().toEpochMilli(), true),
                                            Config.Software(info.softwareName ?: "iCure", info.softwareVersion
                                                    ?: ICUREVERSION),
                                            "",
                                            "en",
                                            Config.Format.SUMEHR
                                    )
                            )
                            operation.binaryResponse(smfExport)
                        } catch(e:Exception) {
                            log.error("Cannot generate SMF", e)
                        }
                    }
                }

            } catch (e: Exception) {
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
}
