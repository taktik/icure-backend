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

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ma.glasnost.orika.MapperFacade
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
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
import reactor.core.publisher.Flux
import java.time.Instant
import java.util.stream.Collectors
import javax.servlet.http.HttpServletResponse

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/be_kmehr")
@Api(tags = ["be_kmehr"])
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

    @ApiOperation(nickname = "generateDiaryNote", value = "Generate diarynote")
    @PostMapping("/diarynote/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun generateDiaryNote(@PathVariable patientId: String,
                                  @RequestParam language: String,
                                  @RequestBody info: DiaryNoteExportInfoDto): Flux<DataBuffer>? {
        return patientLogic.getPatient(patientId)?.let {
            healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())?.let { it1 ->
                diaryNoteLogic.createDiaryNote(
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
                ).injectReactorContext()
            }
        }
    }

    @ApiOperation(nickname = "generateSumehr", value = "Generate sumehr")
    @PostMapping("/sumehr/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun generateSumehr(@PathVariable patientId: String,
                               @RequestParam language: String,
                               @RequestBody info: SumehrExportInfoDto): Flux<DataBuffer>? {
        return patientLogic.getPatient(patientId)?.let {
            healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())?.let { hcp ->
                 sumehrLogicV1.createSumehr(it, info.secretForeignKeys, hcp, mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, info.includeIrrelevantInformation
                        ?: false, null, mapServices(info.services), mapHealthElements(info.healthElements),
                         Config(_kmehrId = System.currentTimeMillis().toString(),
                                 date = Utils.makeXGC(Instant.now().toEpochMilli())!!,
                                 time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
                                 soft = Config.Software(name = info.softwareName ?: "iCure", version = info.softwareVersion ?: ICUREVERSION),
                                 clinicalSummaryType = "",
                                 defaultLanguage = "en",
                                 format = Config.Format.SUMEHR
                         )).injectReactorContext()
            }
        }
    }

    @ApiOperation(nickname = "validateSumehr", value = "Validate sumehr")
    @PostMapping("/sumehr/{patientId}/validate", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun validateSumehr(@PathVariable patientId: String,
                               @RequestParam language: String,
                               @RequestBody info: SumehrExportInfoDto): Flux<DataBuffer>? {
        return patientLogic.getPatient(patientId)?.let {
             healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())?.let { hcp ->
                sumehrLogicV1.validateSumehr(it, info.secretForeignKeys, hcp, mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, info.includeIrrelevantInformation
                        ?: false, null, mapServices(info.services), mapHealthElements(info.healthElements),
                        Config(_kmehrId = System.currentTimeMillis().toString(),
                                date = Utils.makeXGC(Instant.now().toEpochMilli())!!,
                                time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
                                soft = Config.Software(name = info.softwareName ?: "iCure", version = info.softwareVersion ?: ICUREVERSION),
                                clinicalSummaryType = "",
                                defaultLanguage = "en",
                                format = Config.Format.SUMEHR
                        )).injectReactorContext()

            }
        }
    }

    @ApiOperation(nickname = "getSumehrContent", value = "Get sumehr elements")
    @PostMapping("/sumehr/{patientId}/content")
    suspend fun getSumehrContent(@PathVariable patientId: String,
                                 @RequestBody info: SumehrExportInfoDto): SumehrContentDto {
        val result = SumehrContentDto()

        result.services = sumehrLogicV1.getAllServices(sessionLogic.getCurrentHealthcarePartyId(), info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                ?: false, null).stream().map { s -> mapper.map<Service, ServiceDto>(s, ServiceDto::class.java) }.collect(Collectors.toList<ServiceDto>())
        result.healthElements = sumehrLogicV1.getHealthElements(sessionLogic.getCurrentHealthcarePartyId(), info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                ?: false).stream().map { h -> mapper.map<HealthElement, HealthElementDto>(h, HealthElementDto::class.java) }.collect(Collectors.toList<HealthElementDto>())

        return result
    }

    @ApiOperation(nickname = "getSumehrMd5", value = "Check sumehr signature")
    @PostMapping("/sumehr/{patientId}/md5")
    suspend fun getSumehrMd5(@PathVariable patientId: String,
                             @RequestBody info: SumehrExportInfoDto): ContentDto {
        return ContentDto.fromStringValue(patientLogic.getPatient(patientId)?.let {
            sumehrLogicV1.getSumehrMd5(sessionLogic.getCurrentHealthcarePartyId(), it, info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                    ?: false)
        })
    }

    @ApiOperation(nickname = "isSumehrValid", value = "Get sumehr validity")
    @PostMapping("/sumehr/{patientId}/valid")
    suspend fun isSumehrValid(@PathVariable patientId: String,
                              @RequestBody info: SumehrExportInfoDto): SumehrValidityDto {
        return SumehrValidityDto(patientLogic.getPatient(patientId)?.let { sumehrLogicV1.isSumehrValid(sessionLogic.getCurrentHealthcarePartyId(), it, info.secretForeignKeys, info.excludedIds, false, mapServices(info.services), mapHealthElements(info.healthElements)).name }?.let { SumehrStatus.valueOf(it) })
    }

    @ApiOperation(nickname = "generateSumehrV2", value = "Generate sumehr")
    @PostMapping("/sumehrv2/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun generateSumehrV2(@PathVariable patientId: String,
                                 @RequestParam language: String,
                                 @RequestBody info: SumehrExportInfoDto): Flux<DataBuffer>? {
        return patientLogic.getPatient(patientId)?.let {
            healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())?.let { hcp ->
                sumehrLogicV2.createSumehr(it, info.secretForeignKeys, hcp, mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, info.includeIrrelevantInformation
                        ?: false, null, mapServices(info.services), mapHealthElements(info.healthElements),
                        Config(_kmehrId = System.currentTimeMillis().toString(),
                                date = Utils.makeXGC(Instant.now().toEpochMilli())!!,
                                time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
                                soft = Config.Software(name = info.softwareName ?: "iCure", version = info.softwareVersion ?: ICUREVERSION),
                                clinicalSummaryType = "",
                                defaultLanguage = "en",
                                format = Config.Format.SUMEHR
                        )).injectReactorContext()
            }
        }
    }

    @ApiOperation(nickname = "validateSumehrV2", value = "Validate sumehr")
    @PostMapping("/sumehrv2/{patientId}/validate", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun validateSumehrV2(@PathVariable patientId: String,
                                 @RequestParam language: String,
                                 @RequestBody info: SumehrExportInfoDto): Flux<DataBuffer>? {
        return patientLogic.getPatient(patientId)?.let {
            healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())?.let { hcp ->
                sumehrLogicV2.validateSumehr(it, info.secretForeignKeys, hcp, mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, info.includeIrrelevantInformation
                        ?: false, null, mapServices(info.services), mapHealthElements(info.healthElements),
                        Config(_kmehrId = System.currentTimeMillis().toString(),
                                date = Utils.makeXGC(Instant.now().toEpochMilli())!!,
                                time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
                                soft = Config.Software(name = info.softwareName ?: "iCure", version = info.softwareVersion ?: ICUREVERSION),
                                clinicalSummaryType = "",
                                defaultLanguage = "en",
                                format = Config.Format.SUMEHR
                        )).injectReactorContext()
            }
        }
    }

    @ApiOperation(nickname = "getSumehrV2Content", value = "Get sumehr elements")
    @PostMapping("/sumehrv2/{patientId}/content")
    suspend fun getSumehrV2Content(@PathVariable patientId: String,
                                   @RequestBody info: SumehrExportInfoDto): SumehrContentDto {
        val result = SumehrContentDto()

        result.services = sumehrLogicV2.getAllServices(sessionLogic.getCurrentHealthcarePartyId(), info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                ?: false, null).stream().map { s -> mapper.map<Service, ServiceDto>(s, ServiceDto::class.java) }.collect(Collectors.toList<ServiceDto>())
        result.healthElements = sumehrLogicV2.getHealthElements(sessionLogic.getCurrentHealthcarePartyId(), info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                ?: false).stream().map { h -> mapper.map<HealthElement, HealthElementDto>(h, HealthElementDto::class.java) }.collect(Collectors.toList<HealthElementDto>())
        result.patientHealthcareParties = sumehrLogicV2.getPatientHealthcareParties(sessionLogic.getCurrentHealthcarePartyId(), info.secretForeignKeys, info.excludedIds, patientId).stream().map { h -> mapper.map<PatientHealthCareParty, PatientHealthCarePartyDto>(h, PatientHealthCarePartyDto::class.java) }.collect(Collectors.toList<PatientHealthCarePartyDto>())
        result.partnerships = sumehrLogicV2.getContactPeople(sessionLogic.getCurrentHealthcarePartyId(), info.secretForeignKeys, info.excludedIds, patientId).stream().map { h -> mapper.map<Partnership, PartnershipDto>(h, PartnershipDto::class.java) }.collect(Collectors.toList<PartnershipDto>())
        return result
    }

    @ApiOperation(nickname = "getSumehrV2Md5", value = "Check sumehr signature")
    @PostMapping("/sumehrv2/{patientId}/md5")
    suspend fun getSumehrV2Md5(@PathVariable patientId: String,
                               @RequestBody info: SumehrExportInfoDto): ContentDto {
        return ContentDto.fromStringValue(patientLogic.getPatient(patientId)?.let {
            sumehrLogicV2.getSumehrMd5(sessionLogic.getCurrentHealthcarePartyId(), it, info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                    ?: false)
        })
    }

    @ApiOperation(nickname = "isSumehrV2Valid", value = "Get sumehr validity")
    @PostMapping("/sumehrv2/{patientId}/valid")
    suspend fun isSumehrV2Valid(@PathVariable patientId: String,
                                @RequestBody info: SumehrExportInfoDto): SumehrValidityDto {
        return SumehrValidityDto(patientLogic.getPatient(patientId)
                ?.let {
                    sumehrLogicV2.isSumehrValid(sessionLogic.getCurrentHealthcarePartyId(), it, info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                            ?: false, mapServices(info.services), mapHealthElements(info.healthElements)).name
                }
                ?.let { SumehrStatus.valueOf(it) })
    }

    @ApiOperation(nickname = "generateSmfExport", value = "Get SMF (Software Medical File) export")
    @PostMapping("/smf/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun generateSmfExport(@PathVariable patientId: String,
                                  @RequestParam(required = false) language: String?,
                                  @RequestBody smfExportParams: SoftwareMedicalFileExportDto): Flux<DataBuffer>? {
        return patientLogic.getPatient(patientId)
                ?.let {
                    healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
                            ?.let { it1 ->
                                softwareMedicalFileLogic.createSmfExport(it, smfExportParams.secretForeignKeys, it1, language
                                        ?: "fr", null, null,
                                        Config(_kmehrId = System.currentTimeMillis().toString(),
                                                date = Utils.makeXGC(Instant.now().toEpochMilli())!!,
                                                time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
                                                soft = Config.Software(name = smfExportParams.softwareName ?: "iCure", version = smfExportParams.softwareVersion ?: ICUREVERSION),
                                                clinicalSummaryType = "",
                                                defaultLanguage = "en",
                                                format = Config.Format.SMF
                                        )).injectReactorContext()
                            }
                }
    }

    @ApiOperation(nickname = "generateMedicationSchemeExport", value = "Get Medicationscheme export")
    @PostMapping("/medicationscheme/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun generateMedicationSchemeExport(@PathVariable patientId: String,
                                               @RequestParam(required = false) language: String?,
                                               @RequestParam recipientSafe: String,
                                               @RequestParam version: Int,
                                               @RequestBody medicationSchemeExportParams: MedicationSchemeExportInfoDto) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)

        patient?.let {
            userHealthCareParty?.let {
                if (medicationSchemeExportParams.services?.isEmpty() == true)
                    medicationSchemeLogic.createMedicationSchemeExport(patient, medicationSchemeExportParams.secretForeignKeys, userHealthCareParty, language
                            ?: "fr", recipientSafe, version, null, null).injectReactorContext()
                else
                    medicationSchemeLogic.createMedicationSchemeExport(patient, userHealthCareParty, language ?: "fr", recipientSafe, version, medicationSchemeExportParams.services!!.map { s -> mapper.map(s, Service::class.java) as Service }, null)
            }
        }
    }

    @ApiOperation(nickname = "generateContactreportExport", value = "Get Kmehr contactreport")
    @PostMapping("/contactreport/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun generateContactreportExport(@PathVariable patientId: String,
                                            @PathVariable id: String,
                                            @RequestParam date: Long,
                                            @RequestParam language: String,
                                            @RequestParam recipientNihii: String,
                                            @RequestParam recipientFirstName: String,
                                            @RequestParam recipientLastName: String,
                                            @RequestParam mimeType: String,
                                            @RequestBody document: ByteArray,
                                            response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)
        userHealthCareParty?.let { patient?.let { it1 -> kmehrNoteLogic.createNote(response.outputStream, id, it, date, recipientNihii, recipientFirstName, recipientLastName, it1, language, "contactreport", mimeType, document) } }
    }

    @ApiOperation(nickname = "generateLabresultExport", value = "Get Kmehr labresult")
    @PostMapping("/labresult/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun generateLabresultExport(@PathVariable patientId: String,
                                        @PathVariable id: String,
                                        @RequestParam date: Long,
                                        @RequestParam language: String,
                                        @RequestParam recipientNihii: String,
                                        @RequestParam recipientFirstName: String,
                                        @RequestParam recipientLastName: String,
                                        @RequestParam mimeType: String,
                                        @RequestBody document: ByteArray,
                                        response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)

        userHealthCareParty?.let { patient?.let { it1 -> kmehrNoteLogic.createNote(response.outputStream, id, it, date, recipientNihii, recipientFirstName, recipientLastName, it1, language, "labresult", mimeType, document) } }
    }

    @ApiOperation(nickname = "generateNoteExport", value = "Get Kmehr note")
    @PostMapping("/note/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun generateNoteExport(@PathVariable patientId: String,
                                   @PathVariable id: String,
                                   @RequestParam date: Long,
                                   @RequestParam language: String,
                                   @RequestParam recipientNihii: String,
                                   @RequestParam recipientFirstName: String,
                                   @RequestParam recipientLastName: String,
                                   @RequestParam mimeType: String,
                                   @RequestBody document: ByteArray,
                                   response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)
        userHealthCareParty?.let { patient?.let { it1 -> kmehrNoteLogic.createNote(response.outputStream, id, it, date, recipientNihii, recipientFirstName, recipientLastName, it1, language, "note", mimeType, document) } }
    }

    @ApiOperation(nickname = "generatePrescriptionExport", value = "Get Kmehr prescription")
    @PostMapping("/prescription/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun generatePrescriptionExport(@PathVariable patientId: String,
                                           @PathVariable id: String,
                                           @RequestParam date: Long,
                                           @RequestParam language: String,
                                           @RequestParam recipientNihii: String,
                                           @RequestParam recipientFirstName: String,
                                           @RequestParam recipientLastName: String,
                                           @RequestParam mimeType: String,
                                           @RequestBody document: ByteArray,
                                           response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)
        userHealthCareParty?.let { patient?.let { it1 -> kmehrNoteLogic.createNote(response.outputStream, id, it, date, recipientNihii, recipientFirstName, recipientLastName, it1, language, "prescription", mimeType, document) } }
    }

    @ApiOperation(nickname = "generateReportExport", value = "Get Kmehr report")
    @PostMapping("/report/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun generateReportExport(@PathVariable patientId: String,
                                     @PathVariable id: String,
                                     @RequestParam date: Long,
                                     @RequestParam language: String,
                                     @RequestParam recipientNihii: String,
                                     @RequestParam recipientFirstName: String,
                                     @RequestParam recipientLastName: String,
                                     @RequestParam mimeType: String,
                                     @RequestBody document: ByteArray,
                                     response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)
        userHealthCareParty?.let { patient?.let { it1 -> kmehrNoteLogic.createNote(response.outputStream, id, it, date, recipientNihii, recipientFirstName, recipientLastName, it1, language, "report", mimeType, document) } }
    }

    @ApiOperation(nickname = "generateRequestExport", value = "Get Kmehr request")
    @PostMapping("/request/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun generateRequestExport(@PathVariable patientId: String,
                                      @PathVariable id: String,
                                      @RequestParam date: Long,
                                      @RequestParam language: String,
                                      @RequestParam recipientNihii: String,
                                      @RequestParam recipientFirstName: String,
                                      @RequestParam recipientLastName: String,
                                      @RequestParam mimeType: String,
                                      @RequestBody document: ByteArray,
                                      response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)
        userHealthCareParty?.let { patient?.let { it1 -> kmehrNoteLogic.createNote(response.outputStream, id, it, date, recipientNihii, recipientFirstName, recipientLastName, it1, language, "request", mimeType, document) } }
    }

    @ApiOperation(nickname = "generateResultExport", value = "Get Kmehr result")
    @PostMapping("/result/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun generateResultExport(@PathVariable patientId: String,
                                     @PathVariable id: String,
                                     @RequestParam date: Long,
                                     @RequestParam language: String,
                                     @RequestParam recipientNihii: String,
                                     @RequestParam recipientFirstName: String,
                                     @RequestParam recipientLastName: String,
                                     @RequestParam mimeType: String,
                                     @RequestBody document: ByteArray,
                                     response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val patient = patientLogic.getPatient(patientId)

        userHealthCareParty?.let { patient?.let { it1 -> kmehrNoteLogic.createNote(response.outputStream, id, it, date, recipientNihii, recipientFirstName, recipientLastName, it1, language, "result", mimeType, document) } }
    }

    @ApiOperation(nickname = "importSmf", value = "Import SMF into patient(s) using existing document")
    @PostMapping("/smf/{documentId}/import")
    suspend fun importSmf(@PathVariable documentId: String,
                          @RequestParam(required = false) documentKey: String?,
                          @RequestParam(required = false) patientId: String?,
                          @RequestParam(required = false) language: String?,
                          @RequestBody(required = false) mappings: HashMap<String, List<ImportMapping>>?): List<ImportResultDto>? {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val document = documentLogic.get(documentId)

        val attachmentId = document?.attachmentId

        return attachmentId?.let {
            softwareMedicalFileLogic.importSmfFile(documentLogic.readAttachment(documentId, attachmentId), sessionLogic.getCurrentSessionContext().getUser(), language
                    ?: userHealthCareParty?.languages?.firstOrNull() ?: "fr",
                    false,
                    patientId?.let { patientLogic.getPatient(patientId) },
                    mappings ?: HashMap())
        }?.map { mapper.map(it, ImportResultDto::class.java) }
    }

    @ApiOperation(nickname = "checkIfSMFPatientsExists", value = "Check whether patients in SMF already exists in DB")
    @PostMapping("/smf/{documentId}/checkIfSMFPatientsExists")
    suspend fun checkIfSMFPatientsExists(@PathVariable documentId: String,
                                         @RequestParam(required = false) documentKey: String?,
                                         @RequestParam(required = false) patientId: String?,
                                         @RequestParam(required = false) language: String?,
                                         @RequestBody(required = false) mappings: HashMap<String, List<ImportMapping>>?): List<CheckSMFPatientResult>? {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val document = documentLogic.get(documentId)

        val attachmentId = document?.attachmentId

        return attachmentId?.let {
            softwareMedicalFileLogic.checkIfSMFPatientsExists(
                    documentLogic.readAttachment(documentId, attachmentId),
                    sessionLogic.getCurrentSessionContext().getUser(),
                    language ?: userHealthCareParty?.languages?.firstOrNull() ?: "fr",
                    patientId?.let { patientLogic.getPatient(patientId) },
                    mappings ?: HashMap()
            )
        }?.map { mapper.map(it, CheckSMFPatientResult::class.java) }
    }

    @ApiOperation(nickname = "importSumehr", value = "Import sumehr into patient(s) using existing document")
    @PostMapping("/sumehr/{documentId}/import")
    suspend fun importSumehr(@PathVariable documentId: String,
                             @RequestParam(required = false) documentKey: String?,
                             @ApiParam(value = "Dry run: do not save in database")
                             @RequestParam(required = false) dryRun: Boolean?,
                             @RequestParam(required = false) patientId: String?,
                             @RequestParam(required = false) language: String?,
                             @RequestBody(required = false) mappings: HashMap<String, List<ImportMapping>>?): List<ImportResultDto>? {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val document = documentLogic.get(documentId)

        val attachmentId = document?.attachmentId

        return attachmentId?.let {
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

    @ApiOperation(nickname = "importSumehrByItemId", value = "Import sumehr into patient(s) using existing document")
    @PostMapping("/sumehr/{documentId}/importbyitemid")
    suspend fun importSumehrByItemId(@PathVariable documentId: String,
                                     @RequestParam(required = false) documentKey: String?,
                                     @ApiParam(value = "Dry run: do not save in database")
                                     @RequestParam(required = false) dryRun: Boolean?,
                                     @RequestParam itemId: String,
                                     @RequestParam(required = false) patientId: String?,
                                     @RequestParam(required = false) language: String?,
                                     @RequestBody(required = false) mappings: HashMap<String, List<ImportMapping>>?): List<ImportResultDto>? {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val document = documentLogic.get(documentId)

        val attachmentId = document?.attachmentId
        return attachmentId?.let {
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

    @ApiOperation(nickname = "importMedicationScheme", value = "Import MedicationScheme into patient(s) using existing document")
    @PostMapping("/medicationscheme/{documentId}/import")
    suspend fun importMedicationScheme(@PathVariable documentId: String,
                                       @RequestParam(required = false) documentKey: String?,
                                       @ApiParam(value = "Dry run: do not save in database")
                                       @RequestParam(required = false) dryRun: Boolean?,
                                       @RequestParam(required = false) patientId: String?,
                                       @RequestParam(required = false) language: String?,
                                       @RequestBody(required = false) mappings: HashMap<String, List<ImportMapping>>?): List<ImportResultDto>? {

        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val document = documentLogic.get(documentId)

        val attachmentId = document?.attachmentId
        return attachmentId?.let {
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
