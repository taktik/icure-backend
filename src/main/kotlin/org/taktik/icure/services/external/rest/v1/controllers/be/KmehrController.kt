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

package org.taktik.icure.services.external.rest.v1.controllers.be

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.diarynote.DiaryNoteLogic
import org.taktik.icure.be.ehealth.logic.kmehr.medex.KmehrNoteLogic
import org.taktik.icure.be.ehealth.logic.kmehr.medicationscheme.MedicationSchemeLogic
import org.taktik.icure.be.ehealth.logic.kmehr.smf.SoftwareMedicalFileLogic
import org.taktik.icure.be.ehealth.logic.kmehr.sumehr.SumehrLogic
import org.taktik.icure.dto.mapping.ImportMapping
import org.taktik.icure.dto.result.CheckSMFPatientResult
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.embed.Partnership
import org.taktik.icure.entities.embed.PatientHealthCareParty
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.services.external.rest.v1.dto.HealthElementDto
import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.ImportResultDto
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.*
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PartnershipDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PatientHealthCarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto
import org.taktik.icure.utils.injectReactorContext
import java.time.Instant
import java.util.stream.Collectors

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/be_kmehr")
@Tag(name = "bekmehr")
class KmehrController(
        val mapper: MapperFacade,
        val sessionLogic: AsyncSessionLogic,
        @Qualifier("sumehrLogicV1") val sumehrLogicV1: SumehrLogic,
        @Qualifier("sumehrLogicV2") val sumehrLogicV2: SumehrLogic,
        val softwareMedicalFileLogic: SoftwareMedicalFileLogic,
        val medicationSchemeLogic: MedicationSchemeLogic,
        val diaryNoteLogic: DiaryNoteLogic,
        val kmehrNoteLogic: KmehrNoteLogic,
        val healthcarePartyLogic: HealthcarePartyLogic,
        val patientLogic: PatientLogic,
        val documentLogic: DocumentLogic
) {
    @Value("\${icure.version}")
    internal val ICUREVERSION: String = "4.0.0"

    @Operation(summary = "Generate diarynote", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/diarynote/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateDiaryNote(@PathVariable patientId: String,
                          @RequestParam language: String,
                          @RequestBody info: DiaryNoteExportInfoDto,
                          response: ServerHttpResponse) = response.writeWith(flow {
        patientLogic.getPatient(patientId)?.let {
            healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())?.let { it1 ->
                emitAll(diaryNoteLogic.createDiaryNote(
                        it,
                        info.secretForeignKeys,
                        it1,
                        mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java),
                        language,
                        info.note,
                        info.tags,
                        info.contexts,
                        info.psy,
                        info.documentId,
                        info.attachmentId,
                        null
                ))
            }
        }
    }.injectReactorContext())

    @Operation(summary = "Generate sumehr", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/sumehr/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateSumehr(@PathVariable patientId: String,
                               @RequestParam language: String,
                               @RequestBody info: SumehrExportInfoDto,
                       response: ServerHttpResponse) = response.writeWith(flow {
        patientLogic.getPatient(patientId)?.let {
            healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())?.let { hcp ->
                emitAll(sumehrLogicV1.createSumehr(it, info.secretForeignKeys, hcp, mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, info.includeIrrelevantInformation
                        ?: false, null, mapServices(info.services), mapHealthElements(info.healthElements),
                         Config(_kmehrId = System.currentTimeMillis().toString(),
                                 date = Utils.makeXGC(Instant.now().toEpochMilli())!!,
                                 time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
                                 soft = Config.Software(name = info.softwareName ?: "iCure", version = info.softwareVersion ?: ICUREVERSION),
                                 clinicalSummaryType = "",
                                 defaultLanguage = "en",
                                 format = Config.Format.SUMEHR
                         )))
            }
        }
    }.injectReactorContext())

    @Operation(summary = "Validate sumehr", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/sumehr/{patientId}/validate", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun validateSumehr(@PathVariable patientId: String,
                               @RequestParam language: String,
                               @RequestBody info: SumehrExportInfoDto,
                       response: ServerHttpResponse) = response.writeWith(flow {
        patientLogic.getPatient(patientId)?.let {
             healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())?.let { hcp ->
                 emitAll(sumehrLogicV1.validateSumehr(it, info.secretForeignKeys, hcp, mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, info.includeIrrelevantInformation
                        ?: false, null, mapServices(info.services), mapHealthElements(info.healthElements),
                        Config(_kmehrId = System.currentTimeMillis().toString(),
                                date = Utils.makeXGC(Instant.now().toEpochMilli())!!,
                                time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
                                soft = Config.Software(name = info.softwareName ?: "iCure", version = info.softwareVersion ?: ICUREVERSION),
                                clinicalSummaryType = "",
                                defaultLanguage = "en",
                                format = Config.Format.SUMEHR
                        )
                 ))
            }
        }
    }.injectReactorContext())

    @Operation(summary = "Get sumehr elements")
    @PostMapping("/sumehr/{patientId}/content")
    fun getSumehrContent(@PathVariable patientId: String,
                                 @RequestBody info: SumehrExportInfoDto) = mono {
        SumehrContentDto().apply {
            services = sumehrLogicV1.getAllServices(sessionLogic.getCurrentHealthcarePartyId(), info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                    ?: false, null).stream().map { s -> mapper.map<Service, ServiceDto>(s, ServiceDto::class.java) }.collect(Collectors.toList<ServiceDto>())
            healthElements = sumehrLogicV1.getHealthElements(sessionLogic.getCurrentHealthcarePartyId(), info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                    ?: false).stream().map { h -> mapper.map<HealthElement, HealthElementDto>(h, HealthElementDto::class.java) }.collect(Collectors.toList<HealthElementDto>())

        }
    }

    @Operation(summary = "Check sumehr signature")
    @PostMapping("/sumehr/{patientId}/md5")
    fun getSumehrMd5(@PathVariable patientId: String,
                             @RequestBody info: SumehrExportInfoDto) = mono {
        ContentDto.fromStringValue(patientLogic.getPatient(patientId)?.let {
            sumehrLogicV1.getSumehrMd5(sessionLogic.getCurrentHealthcarePartyId(), it, info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                    ?: false)
        })
    }

    @Operation(summary = "Get sumehr validity")
    @PostMapping("/sumehr/{patientId}/valid")
    fun isSumehrValid(@PathVariable patientId: String,
                              @RequestBody info: SumehrExportInfoDto) = mono {
        SumehrValidityDto(patientLogic.getPatient(patientId)?.let { sumehrLogicV1.isSumehrValid(sessionLogic.getCurrentHealthcarePartyId(), it, info.secretForeignKeys, info.excludedIds, false, mapServices(info.services), mapHealthElements(info.healthElements)).name }?.let { SumehrStatus.valueOf(it) })
    }

    @Operation(summary = "Generate sumehr", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/sumehrv2/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateSumehrV2(@PathVariable patientId: String,
                                 @RequestParam language: String,
                                 @RequestBody info: SumehrExportInfoDto,
                         response: ServerHttpResponse) = response.writeWith(flow {
    patientLogic.getPatient(patientId)?.let {
            healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())?.let { hcp ->
                emitAll(sumehrLogicV2.createSumehr(it, info.secretForeignKeys, hcp, mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, info.includeIrrelevantInformation
                        ?: false, null, mapServices(info.services), mapHealthElements(info.healthElements),
                        Config(_kmehrId = System.currentTimeMillis().toString(),
                                date = Utils.makeXGC(Instant.now().toEpochMilli())!!,
                                time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
                                soft = Config.Software(name = info.softwareName ?: "iCure", version = info.softwareVersion ?: ICUREVERSION),
                                clinicalSummaryType = "",
                                defaultLanguage = "en",
                                format = Config.Format.SUMEHR
                        )))
            }
        }
    }.injectReactorContext())

    @Operation(summary = "Validate sumehr", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/sumehrv2/{patientId}/validate", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun validateSumehrV2(@PathVariable patientId: String,
                                 @RequestParam language: String,
                                 @RequestBody info: SumehrExportInfoDto,
                         response: ServerHttpResponse) = response.writeWith(flow {
    patientLogic.getPatient(patientId)?.let {
            healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())?.let { hcp ->
                emitAll(sumehrLogicV2.validateSumehr(it, info.secretForeignKeys, hcp, mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, info.includeIrrelevantInformation
                        ?: false, null, mapServices(info.services), mapHealthElements(info.healthElements),
                        Config(_kmehrId = System.currentTimeMillis().toString(),
                                date = Utils.makeXGC(Instant.now().toEpochMilli())!!,
                                time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
                                soft = Config.Software(name = info.softwareName ?: "iCure", version = info.softwareVersion ?: ICUREVERSION),
                                clinicalSummaryType = "",
                                defaultLanguage = "en",
                                format = Config.Format.SUMEHR
                        ))
                )
            }
        }
    }.injectReactorContext())

    @Operation(summary = "Get sumehr elements")
    @PostMapping("/sumehrv2/{patientId}/content")
    fun getSumehrV2Content(@PathVariable patientId: String,
                                   @RequestBody info: SumehrExportInfoDto) = mono {
        SumehrContentDto().apply {
            services = sumehrLogicV2.getAllServices(sessionLogic.getCurrentHealthcarePartyId(), info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                    ?: false, null).stream().map { s -> mapper.map<Service, ServiceDto>(s, ServiceDto::class.java) }.collect(Collectors.toList<ServiceDto>())
            healthElements = sumehrLogicV2.getHealthElements(sessionLogic.getCurrentHealthcarePartyId(), info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                    ?: false).stream().map { h -> mapper.map<HealthElement, HealthElementDto>(h, HealthElementDto::class.java) }.collect(Collectors.toList<HealthElementDto>())
            patientHealthcareParties = sumehrLogicV2.getPatientHealthcareParties(sessionLogic.getCurrentHealthcarePartyId(), info.secretForeignKeys, info.excludedIds, patientId).stream().map { h -> mapper.map<PatientHealthCareParty, PatientHealthCarePartyDto>(h, PatientHealthCarePartyDto::class.java) }.collect(Collectors.toList<PatientHealthCarePartyDto>())
            partnerships = sumehrLogicV2.getContactPeople(sessionLogic.getCurrentHealthcarePartyId(), info.secretForeignKeys, info.excludedIds, patientId).stream().map { h -> mapper.map<Partnership, PartnershipDto>(h, PartnershipDto::class.java) }.collect(Collectors.toList<PartnershipDto>())
        }
    }

    @Operation(summary = "Check sumehr signature")
    @PostMapping("/sumehrv2/{patientId}/md5")
    fun getSumehrV2Md5(@PathVariable patientId: String,
                               @RequestBody info: SumehrExportInfoDto) = mono {
        ContentDto.fromStringValue(patientLogic.getPatient(patientId)?.let {
            sumehrLogicV2.getSumehrMd5(sessionLogic.getCurrentHealthcarePartyId(), it, info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                    ?: false)
        })
    }

    @Operation(summary = "Get sumehr validity")
    @PostMapping("/sumehrv2/{patientId}/valid")
    fun isSumehrV2Valid(@PathVariable patientId: String,
                                @RequestBody info: SumehrExportInfoDto) = mono {
        SumehrValidityDto(patientLogic.getPatient(patientId)
                ?.let {
                    sumehrLogicV2.isSumehrValid(sessionLogic.getCurrentHealthcarePartyId(), it, info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                            ?: false, mapServices(info.services), mapHealthElements(info.healthElements)).name
                }
                ?.let { SumehrStatus.valueOf(it) })
    }

    @Operation(summary = "Get SMF (Software Medical File) export", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/smf/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateSmfExport(@PathVariable patientId: String,
                                  @RequestParam language: String,
                                  @RequestBody smfExportParams: SoftwareMedicalFileExportDto,
                          response: ServerHttpResponse) = response.writeWith(flow {
    patientLogic.getPatient(patientId)
                ?.let {
                    healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
                            ?.let { it1 ->
                                emitAll(softwareMedicalFileLogic.createSmfExport(it, smfExportParams.secretForeignKeys, it1, language, null, null,
                                        Config(_kmehrId = System.currentTimeMillis().toString(),
                                                date = Utils.makeXGC(Instant.now().toEpochMilli())!!,
                                                time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
                                                soft = Config.Software(name = smfExportParams.softwareName ?: "iCure", version = smfExportParams.softwareVersion ?: ICUREVERSION),
                                                clinicalSummaryType = "",
                                                defaultLanguage = "en",
                                                format = Config.Format.SMF
                                        )))
                            }
                }
    }.injectReactorContext())

    @Operation(summary = "Get Medicationscheme export", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/medicationscheme/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateMedicationSchemeExport(@PathVariable patientId: String,
                                               @RequestParam language: String,
                                               @RequestParam recipientSafe: String,
                                               @RequestParam version: Int,
                                               @RequestBody medicationSchemeExportParams: MedicationSchemeExportInfoDto,
                                       response: ServerHttpResponse) = response.writeWith(flow {

    val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)

        patient?.let {
            userHealthCareParty?.let {
                emitAll((
                        if (medicationSchemeExportParams.services?.isEmpty() == true)
                            medicationSchemeLogic.createMedicationSchemeExport(patient, medicationSchemeExportParams.secretForeignKeys, userHealthCareParty, language, recipientSafe, version, null, null)
                        else
                            medicationSchemeLogic.createMedicationSchemeExport(patient, userHealthCareParty, language, recipientSafe, version, medicationSchemeExportParams.services!!.map { s -> mapper.map(s, Service::class.java) as Service }, null)
                        ))
            }
        }
    }.injectReactorContext())


    @Operation(summary = "Get Kmehr contactreport", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/contactreport/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateContactreportExport(@PathVariable patientId: String,
                                            @PathVariable id: String,
                                            @RequestParam date: Long,
                                            @RequestParam language: String,
                                            @RequestParam recipientNihii: String,
                                            @RequestParam recipientFirstName: String,
                                            @RequestParam recipientLastName: String,
                                            @RequestParam mimeType: String,
                                            @RequestBody document: ByteArray,
                                            response: ServerHttpResponse) = response.writeWith(flow {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)

        if (userHealthCareParty != null && patient != null) {
            emitAll(kmehrNoteLogic.createNote(id, userHealthCareParty, date, recipientNihii, recipientFirstName, recipientLastName, patient, language, "contactreport", mimeType, document))
        }
    }.injectReactorContext())

    @Operation(summary = "Get Kmehr labresult", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/labresult/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateLabresultExport(@PathVariable patientId: String,
                                        @PathVariable id: String,
                                        @RequestParam date: Long,
                                        @RequestParam language: String,
                                        @RequestParam recipientNihii: String,
                                        @RequestParam recipientFirstName: String,
                                        @RequestParam recipientLastName: String,
                                        @RequestParam mimeType: String,
                                        @RequestBody document: ByteArray,
                                        response: ServerHttpResponse) = response.writeWith(flow {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)

        if (userHealthCareParty != null && patient != null) {
            emitAll(kmehrNoteLogic.createNote(id, userHealthCareParty, date, recipientNihii, recipientFirstName, recipientLastName, patient, language, "labresult", mimeType, document))
        }
    }.injectReactorContext())

    @Operation(summary = "Get Kmehr note", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/note/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateNoteExport(@PathVariable patientId: String,
                                   @PathVariable id: String,
                                   @RequestParam date: Long,
                                   @RequestParam language: String,
                                   @RequestParam recipientNihii: String,
                                   @RequestParam recipientFirstName: String,
                                   @RequestParam recipientLastName: String,
                                   @RequestParam mimeType: String,
                                   @RequestBody document: ByteArray,
                           response: ServerHttpResponse) = response.writeWith(flow {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)
        if (userHealthCareParty != null && patient != null) {
            emitAll(kmehrNoteLogic.createNote(id, userHealthCareParty, date, recipientNihii, recipientFirstName, recipientLastName, patient, language, "note", mimeType, document))
        }
    }.injectReactorContext())

    @Operation(summary = "Get Kmehr prescription", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/prescription/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generatePrescriptionExport(@PathVariable patientId: String,
                                           @PathVariable id: String,
                                           @RequestParam date: Long,
                                           @RequestParam language: String,
                                           @RequestParam recipientNihii: String,
                                           @RequestParam recipientFirstName: String,
                                           @RequestParam recipientLastName: String,
                                           @RequestParam mimeType: String,
                                           @RequestBody document: ByteArray,
                                   response: ServerHttpResponse) = response.writeWith(flow {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)
        if (userHealthCareParty != null && patient != null) {
            emitAll(kmehrNoteLogic.createNote(id, userHealthCareParty, date, recipientNihii, recipientFirstName, recipientLastName, patient, language, "prescription", mimeType, document))
        }
    }.injectReactorContext())

    @Operation(summary = "Get Kmehr report", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/report/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateReportExport(@PathVariable patientId: String,
                                     @PathVariable id: String,
                                     @RequestParam date: Long,
                                     @RequestParam language: String,
                                     @RequestParam recipientNihii: String,
                                     @RequestParam recipientFirstName: String,
                                     @RequestParam recipientLastName: String,
                                     @RequestParam mimeType: String,
                                     @RequestBody document: ByteArray,
                             response: ServerHttpResponse) = response.writeWith(flow {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)
        if (userHealthCareParty != null && patient != null) {
            emitAll(kmehrNoteLogic.createNote(id, userHealthCareParty, date, recipientNihii, recipientFirstName, recipientLastName, patient, language, "report", mimeType, document))
        }
    }.injectReactorContext())

    @Operation(summary = "Get Kmehr request", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/request/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateRequestExport(@PathVariable patientId: String,
                                      @PathVariable id: String,
                                      @RequestParam date: Long,
                                      @RequestParam language: String,
                                      @RequestParam recipientNihii: String,
                                      @RequestParam recipientFirstName: String,
                                      @RequestParam recipientLastName: String,
                                      @RequestParam mimeType: String,
                                      @RequestBody document: ByteArray,
                              response: ServerHttpResponse) = response.writeWith(flow {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)
        if (userHealthCareParty != null && patient != null) {
            emitAll(kmehrNoteLogic.createNote(id, userHealthCareParty, date, recipientNihii, recipientFirstName, recipientLastName, patient, language, "request", mimeType, document))
        }
    }.injectReactorContext())

    @Operation(summary = "Get Kmehr result", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/result/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateResultExport(@PathVariable patientId: String,
                                     @PathVariable id: String,
                                     @RequestParam date: Long,
                                     @RequestParam language: String,
                                     @RequestParam recipientNihii: String,
                                     @RequestParam recipientFirstName: String,
                                     @RequestParam recipientLastName: String,
                                     @RequestParam mimeType: String,
                                     @RequestBody document: ByteArray,
                             response: ServerHttpResponse) = response.writeWith(flow {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)
        if (userHealthCareParty != null && patient != null) {
            emitAll(kmehrNoteLogic.createNote(id, userHealthCareParty, date, recipientNihii, recipientFirstName, recipientLastName, patient, language, "result", mimeType, document))
        }
    }.injectReactorContext())

    @Operation(summary = "Import SMF into patient(s) using existing document")
    @PostMapping("/smf/{documentId}/import")
    fun importSmf(@PathVariable documentId: String,
                          @RequestParam(required = false) documentKey: String?,
                          @RequestParam(required = false) patientId: String?,
                          @RequestParam(required = false) language: String?,
                          @RequestBody(required = false) mappings: HashMap<String, List<ImportMapping>>?) = mono {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val document = documentLogic.get(documentId)

        val attachmentId = document?.attachmentId

        attachmentId?.let {
            softwareMedicalFileLogic.importSmfFile(documentLogic.readAttachment(documentId, attachmentId), sessionLogic.getCurrentSessionContext().getUser(), language
                    ?: userHealthCareParty?.languages?.firstOrNull() ?: "fr",
                    false,
                    patientId?.let { patientLogic.getPatient(patientId) },
                    mappings ?: HashMap())
        }?.map { mapper.map(it, ImportResultDto::class.java) }
    }

    @Operation(summary = "Check whether patients in SMF already exists in DB")
    @PostMapping("/smf/{documentId}/checkIfSMFPatientsExists")
    fun checkIfSMFPatientsExists(@PathVariable documentId: String,
                                         @RequestParam(required = false) documentKey: String?,
                                         @RequestParam(required = false) patientId: String?,
                                         @RequestParam(required = false) language: String?,
                                         @RequestBody(required = false) mappings: HashMap<String, List<ImportMapping>>?) = mono {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val document = documentLogic.get(documentId)

        val attachmentId = document?.attachmentId

        attachmentId?.let {
            softwareMedicalFileLogic.checkIfSMFPatientsExists(
                    documentLogic.readAttachment(documentId, attachmentId),
                    sessionLogic.getCurrentSessionContext().getUser(),
                    language ?: userHealthCareParty?.languages?.firstOrNull() ?: "fr",
                    patientId?.let { patientLogic.getPatient(patientId) },
                    mappings ?: HashMap()
            )
        }?.map { mapper.map(it, CheckSMFPatientResult::class.java) }
    }

    @Operation(summary = "Import sumehr into patient(s) using existing document")
    @PostMapping("/sumehr/{documentId}/import")
    fun importSumehr(@PathVariable documentId: String,
                             @RequestParam(required = false) documentKey: String?,
                             @Parameter(description = "Dry run: do not save in database")
                             @RequestParam(required = false) dryRun: Boolean?,
                             @RequestParam(required = false) patientId: String?,
                             @RequestParam(required = false) language: String?,
                             @RequestBody(required = false) mappings: HashMap<String, List<ImportMapping>>?) = mono {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val document = documentLogic.get(documentId)

        val attachmentId = document?.attachmentId

        attachmentId?.let {
            sumehrLogicV1.importSumehr(
                    documentLogic.readAttachment(documentId, attachmentId),
                    sessionLogic.getCurrentSessionContext().getUser(),
                    language ?: userHealthCareParty?.languages?.firstOrNull() ?: "fr",
                    patientId?.let { patientLogic.getPatient(patientId) },
                    mappings ?: HashMap(),
                    dryRun != true
            )
        }?.map { mapper.map(it, ImportResultDto::class.java) }
    }

    @Operation(summary = "Import sumehr into patient(s) using existing document")
    @PostMapping("/sumehr/{documentId}/importbyitemid")
    fun importSumehrByItemId(@PathVariable documentId: String,
                             @RequestParam itemId: String,
                                     @RequestParam(required = false) documentKey: String?,
                                     @Parameter(description = "Dry run: do not save in database")
                                     @RequestParam(required = false) dryRun: Boolean?,
                                     @RequestParam(required = false) patientId: String?,
                                     @RequestParam(required = false) language: String?,
                                     @RequestBody(required = false) mappings: HashMap<String, List<ImportMapping>>?) = mono {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val document = documentLogic.get(documentId)

        val attachmentId = document?.attachmentId
        attachmentId?.let {
            sumehrLogicV2.importSumehrByItemId(
                    documentLogic.readAttachment(documentId, attachmentId),
                    itemId,
                    sessionLogic.getCurrentSessionContext().getUser(),
                    language ?: userHealthCareParty?.languages?.firstOrNull() ?: "fr",
                    patientId?.let { patientLogic.getPatient(patientId) },
                    mappings ?: HashMap(),
                    dryRun != true
            )
        }?.map { mapper.map(it, ImportResultDto::class.java) }
    }

    @Operation(summary = "Import MedicationScheme into patient(s) using existing document")
    @PostMapping("/medicationscheme/{documentId}/import")
    fun importMedicationScheme(@PathVariable documentId: String,
                                       @RequestParam(required = false) documentKey: String?,
                                       @Parameter(description = "Dry run: do not save in database")
                                       @RequestParam(required = false) dryRun: Boolean?,
                                       @RequestParam(required = false) patientId: String?,
                                       @RequestParam(required = false) language: String?,
                                       @RequestBody(required = false) mappings: HashMap<String, List<ImportMapping>>?) = mono {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val document = documentLogic.get(documentId)

        val attachmentId = document?.attachmentId
        attachmentId?.let {
            medicationSchemeLogic.importMedicationSchemeFile(
                    documentLogic.readAttachment(documentId, attachmentId),
                    sessionLogic.getCurrentSessionContext().getUser(),
                    language
                            ?: userHealthCareParty?.languages?.firstOrNull() ?: "fr",
                    patientId?.let { patientLogic.getPatient(patientId) },
                    mappings ?: HashMap(),
                    dryRun != true
            )
        }?.map { mapper.map(it, ImportResultDto::class.java) }
    }

    private fun mapServices(services: List<ServiceDto>?) =
            services?.map { s -> mapper.map(s, Service::class.java) as Service }

    private fun mapHealthElements(healthElements: List<HealthElementDto>?) =
            healthElements?.map { s -> mapper.map(s, HealthElement::class.java) as HealthElement }

}
