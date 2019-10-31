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
import ma.glasnost.orika.MapperFacade
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
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
import org.taktik.icure.logic.DocumentLogic
import org.taktik.icure.logic.HealthcarePartyLogic
import org.taktik.icure.logic.PatientLogic
import org.taktik.icure.logic.SessionLogic
import org.taktik.icure.services.external.rest.v1.dto.HealthElementDto
import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.ImportResultDto
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.*
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PartnershipDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PatientHealthCarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto
import java.util.stream.Collectors
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/rest/v1/be_kmehr")
@Api(tags = ["be_kmehr"])
class KmehrController(
        val mapper: MapperFacade,
        val sessionLogic: SessionLogic,
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
    @ApiOperation(nickname = "generateDiaryNote", value = "Generate diarynote")
    @PostMapping("/diarynote/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateDiaryNote(@PathVariable patientId: String,
                          @RequestParam language: String,
                          @RequestBody info: DiaryNoteExportInfoDto,
                          response: HttpServletResponse) {
        diaryNoteLogic.createDiaryNote(response.outputStream, patientLogic.getPatient(patientId), info.secretForeignKeys, healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId), mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.note, info.tags, info.contexts, info.psy, info.documentId, info.attachmentId, null)
    }

    @ApiOperation(nickname = "generateSumehr", value = "Generate sumehr")
    @PostMapping("/sumehr/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateSumehr(@PathVariable patientId: String,
                       @RequestParam language: String,
                       @RequestBody info: SumehrExportInfoDto,
                       response: HttpServletResponse) {
        sumehrLogicV1.createSumehr(response.outputStream, patientLogic.getPatient(patientId), info.secretForeignKeys, healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId), mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, info.includeIrrelevantInformation
                ?: false, null)
    }

    @ApiOperation(nickname = "validateSumehr", value = "Validate sumehr")
    @PostMapping("/sumehr/{patientId}/validate", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun validateSumehr(@PathVariable patientId: String,
                       @RequestParam language: String,
                       @RequestBody info: SumehrExportInfoDto,
                       response: HttpServletResponse) {
        sumehrLogicV1.validateSumehr(response.outputStream, patientLogic.getPatient(patientId), info.secretForeignKeys, healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId), mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, info.includeIrrelevantInformation
                ?: false, null)
    }

    @ApiOperation(nickname = "getSumehrContent", value = "Get sumehr elements")
    @PostMapping("/sumehr/{patientId}/content")
    fun getSumehrContent(@PathVariable patientId: String,
                         @RequestBody info: SumehrExportInfoDto): SumehrContentDto {
        val result = SumehrContentDto()

        result.services = sumehrLogicV1.getAllServices(sessionLogic.currentSessionContext.user.healthcarePartyId, info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                ?: false, null).stream().map { s -> mapper.map<Service, ServiceDto>(s, ServiceDto::class.java) }.collect(Collectors.toList<ServiceDto>())
        result.healthElements = sumehrLogicV1.getHealthElements(sessionLogic.currentSessionContext.user.healthcarePartyId, info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                ?: false).stream().map { h -> mapper.map<HealthElement, HealthElementDto>(h, HealthElementDto::class.java) }.collect(Collectors.toList<HealthElementDto>())

        return result
    }

    @ApiOperation(nickname = "getSumehrMd5", value = "Check sumehr signature")
    @PostMapping("/sumehr/{patientId}/md5")
    fun getSumehrMd5(@PathVariable patientId: String,
                     @RequestBody info: SumehrExportInfoDto): ContentDto {
        return ContentDto.fromStringValue(sumehrLogicV1.getSumehrMd5(sessionLogic.currentSessionContext.user.healthcarePartyId, patientLogic.getPatient(patientId), info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                ?: false))
    }

    @ApiOperation(nickname = "isSumehrValid", value = "Get sumehr validity")
    @PostMapping("/sumehr/{patientId}/valid")
    fun isSumehrValid(@PathVariable patientId: String,
                      @RequestBody info: SumehrExportInfoDto): SumehrValidityDto {
        return SumehrValidityDto(SumehrStatus.valueOf(sumehrLogicV1.isSumehrValid(sessionLogic.currentSessionContext.user.healthcarePartyId, patientLogic.getPatient(patientId), info.secretForeignKeys, info.excludedIds, false).name))
    }

    @ApiOperation(nickname = "generateSumehrV2", value = "Generate sumehr")
    @PostMapping("/sumehrv2/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateSumehrV2(@PathVariable patientId: String,
                         @RequestParam language: String,
                         @RequestBody info: SumehrExportInfoDto,
                         response: HttpServletResponse) {
        sumehrLogicV2.createSumehr(response.outputStream, patientLogic.getPatient(patientId), info.secretForeignKeys, healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId), mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, info.includeIrrelevantInformation
                ?: false, null)
    }

    @ApiOperation(nickname = "validateSumehrV2", value = "Validate sumehr")
    @PostMapping("/sumehrv2/{patientId}/validate", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun validateSumehrV2(@PathVariable patientId: String,
                         @RequestParam language: String,
                         @RequestBody info: SumehrExportInfoDto,
                         response: HttpServletResponse) {
        sumehrLogicV2.validateSumehr(response.outputStream, patientLogic.getPatient(patientId), info.secretForeignKeys, healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId), mapper.map<HealthcarePartyDto, HealthcareParty>(info.recipient, HealthcareParty::class.java), language, info.comment, info.excludedIds, info.includeIrrelevantInformation
                ?: false, null)
    }

    @ApiOperation(nickname = "getSumehrV2Content", value = "Get sumehr elements")
    @PostMapping("/sumehrv2/{patientId}/content")
    fun getSumehrV2Content(@PathVariable patientId: String,
                           @RequestBody info: SumehrExportInfoDto,
                           response: HttpServletResponse): SumehrContentDto {
        val result = SumehrContentDto()

        result.services = sumehrLogicV2.getAllServices(sessionLogic.currentSessionContext.user.healthcarePartyId, info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                ?: false, null).stream().map { s -> mapper.map<Service, ServiceDto>(s, ServiceDto::class.java) }.collect(Collectors.toList<ServiceDto>())
        result.healthElements = sumehrLogicV2.getHealthElements(sessionLogic.currentSessionContext.user.healthcarePartyId, info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                ?: false).stream().map { h -> mapper.map<HealthElement, HealthElementDto>(h, HealthElementDto::class.java) }.collect(Collectors.toList<HealthElementDto>())
        result.patientHealthcareParties = sumehrLogicV2.getPatientHealthcareParties(sessionLogic.currentSessionContext.user.healthcarePartyId, info.secretForeignKeys, info.excludedIds, patientId).stream().map { h -> mapper.map<PatientHealthCareParty, PatientHealthCarePartyDto>(h, PatientHealthCarePartyDto::class.java) }.collect(Collectors.toList<PatientHealthCarePartyDto>())
        result.partnerships = sumehrLogicV2.getContactPeople(sessionLogic.currentSessionContext.user.healthcarePartyId, info.secretForeignKeys, info.excludedIds, patientId).stream().map { h -> mapper.map<Partnership, PartnershipDto>(h, PartnershipDto::class.java) }.collect(Collectors.toList<PartnershipDto>())
        return result
    }

    @ApiOperation(nickname = "getSumehrV2Md5", value = "Check sumehr signature")
    @PostMapping("/sumehrv2/{patientId}/md5")
    fun getSumehrV2Md5(@PathVariable patientId: String,
                       @RequestBody info: SumehrExportInfoDto): ContentDto {
        return ContentDto.fromStringValue(sumehrLogicV2.getSumehrMd5(sessionLogic.currentSessionContext.user.healthcarePartyId, patientLogic.getPatient(patientId), info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                ?: false))
    }

    @ApiOperation(nickname = "isSumehrV2Valid", value = "Get sumehr validity")
    @PostMapping("/sumehrv2/{patientId}/valid")
    fun isSumehrV2Valid(@PathVariable patientId: String,
                        @RequestBody info: SumehrExportInfoDto): SumehrValidityDto {
        return SumehrValidityDto(SumehrStatus.valueOf(sumehrLogicV2.isSumehrValid(sessionLogic.currentSessionContext.user.healthcarePartyId, patientLogic.getPatient(patientId), info.secretForeignKeys, info.excludedIds, info.includeIrrelevantInformation
                ?: false).name))
    }

    @ApiOperation(nickname = "generateSmfExport", value = "Get SMF (Software Medical File) export")
    @PostMapping("/smf/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateSmfExport(@PathVariable patientId: String,
                          @RequestParam(required = false) language: String?,
                          @RequestBody smfExportParams: SoftwareMedicalFileExportDto,
                          response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId)
        softwareMedicalFileLogic.createSmfExport(response.outputStream, patientLogic.getPatient(patientId), smfExportParams.secretForeignKeys, userHealthCareParty, language
                ?: "fr", null, null)
    }

    @ApiOperation(nickname = "generateMedicationSchemeExport", value = "Get Medicationscheme export")
    @PostMapping("/medicationscheme/{patientId}/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateMedicationSchemeExport(@PathVariable patientId: String,
                                       @RequestParam(required = false) language: String?,
                                       @RequestParam version: Int,
                                       @RequestBody medicationSchemeExportParams: MedicationSchemeExportInfoDto,
                                       response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId)

        return if (medicationSchemeExportParams.services?.isEmpty() == true)
            medicationSchemeLogic.createMedicationSchemeExport(response.outputStream, patientLogic.getPatient(patientId), medicationSchemeExportParams.secretForeignKeys, userHealthCareParty, language
                    ?: "fr", version, null, null)
        else
            medicationSchemeLogic.createMedicationSchemeExport(response.outputStream, patientLogic.getPatient(patientId), userHealthCareParty, language
                    ?: "fr", version, medicationSchemeExportParams.services!!.map { s ->
                mapper.map(s, Service::class.java) as Service
            }, null)
    }

    @ApiOperation(nickname = "generateContactreportExport", value = "Get Kmehr contactreport")
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
                                    response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId)
        kmehrNoteLogic.createNote(response.outputStream, id, userHealthCareParty, date, recipientNihii, recipientFirstName, recipientLastName, patientLogic.getPatient(patientId), language, "contactreport", mimeType, document)
    }

    @ApiOperation(nickname = "generateLabresultExport", value = "Get Kmehr labresult")
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
                                response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId)
        kmehrNoteLogic.createNote(response.outputStream, id, userHealthCareParty, date, recipientNihii, recipientFirstName, recipientLastName, patientLogic.getPatient(patientId), language, "labresult", mimeType, document)
    }

    @ApiOperation(nickname = "generateNoteExport", value = "Get Kmehr note")
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
                           response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId)
        kmehrNoteLogic.createNote(response.outputStream, id, userHealthCareParty, date, recipientNihii, recipientFirstName, recipientLastName, patientLogic.getPatient(patientId), language, "note", mimeType, document)
    }

    @ApiOperation(nickname = "generatePrescriptionExport", value = "Get Kmehr prescription")
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
                                   response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId)
        kmehrNoteLogic.createNote(response.outputStream, id, userHealthCareParty, date, recipientNihii, recipientFirstName, recipientLastName, patientLogic.getPatient(patientId), language, "prescription", mimeType, document)
    }

    @ApiOperation(nickname = "generateReportExport", value = "Get Kmehr report")
    @PostMapping("/report/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateReportExport(@PathVariable patientId: String, @PathVariable id: String, @RequestParam date: Long, @RequestParam language: String, @RequestParam recipientNihii: String, @RequestParam recipientFirstName: String, @RequestParam recipientLastName: String, @RequestParam mimeType: String, document: ByteArray,
                             response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId)
        kmehrNoteLogic.createNote(response.outputStream, id, userHealthCareParty, date, recipientNihii, recipientFirstName, recipientLastName, patientLogic.getPatient(patientId), language, "report", mimeType, document)
    }

    @ApiOperation(nickname = "generateRequestExport", value = "Get Kmehr request")
    @PostMapping("/request/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateRequestExport(@PathVariable patientId: String, @PathVariable id: String, @RequestParam date: Long, @RequestParam language: String, @RequestParam recipientNihii: String, @RequestParam recipientFirstName: String, @RequestParam recipientLastName: String, @RequestParam mimeType: String, document: ByteArray,
                              response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId)
        kmehrNoteLogic.createNote(response.outputStream, id, userHealthCareParty, date, recipientNihii, recipientFirstName, recipientLastName, patientLogic.getPatient(patientId), language, "request", mimeType, document)
    }

    @ApiOperation(nickname = "generateResultExport", value = "Get Kmehr result")
    @PostMapping("/result/{patientId}/export/{id}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generateResultExport(@PathVariable patientId: String, @PathVariable id: String, @RequestParam date: Long, @RequestParam language: String, @RequestParam recipientNihii: String, @RequestParam recipientFirstName: String, @RequestParam recipientLastName: String, @RequestParam mimeType: String, document: ByteArray,
                             response: HttpServletResponse) {
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId)
        kmehrNoteLogic.createNote(response.outputStream, id, userHealthCareParty, date, recipientNihii, recipientFirstName, recipientLastName, patientLogic.getPatient(patientId), language, "result", mimeType, document)
    }

    @ApiOperation(nickname = "importSmf", value = "Import SMF into patient(s) using existing document")
    @PostMapping("/smf/{documentId}/import")
    fun importSmf(@PathVariable documentId: String, @RequestParam(required = false) documentKey: String?, @RequestParam(required = false) patientId: String?, @RequestParam(required = false) language: String?, mappings: HashMap<String, List<ImportMapping>>?): List<ImportResultDto> {
        val user = sessionLogic.currentSessionContext.user
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(user.healthcarePartyId)
        val document = documentLogic.get(documentId)

        return softwareMedicalFileLogic.importSmfFile(documentLogic.readAttachment(documentId, document.attachmentId), user, language
                ?: userHealthCareParty.languages?.firstOrNull() ?: "fr",
                patientId?.let { patientLogic.getPatient(patientId) },
                mappings ?: HashMap()).map { mapper.map(it, ImportResultDto::class.java) }
    }

    @ApiOperation(nickname = "checkIfSMFPatientsExists", value = "Check whether patients in SMF already exists in DB")
    @PostMapping("/smf/{documentId}/checkIfSMFPatientsExists")
    fun checkIfSMFPatientsExists(@PathVariable documentId: String, @RequestParam(required = false) documentKey: String?, @RequestParam(required = false) patientId: String?, @RequestParam(required = false) language: String?, mappings: HashMap<String, List<ImportMapping>>?): List<CheckSMFPatientResult> {
        val user = sessionLogic.currentSessionContext.user
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(user.healthcarePartyId)
        val document = documentLogic.get(documentId)

        return softwareMedicalFileLogic.checkIfSMFPatientsExists(documentLogic.readAttachment(documentId, document.attachmentId), user, language
                ?: userHealthCareParty.languages?.firstOrNull() ?: "fr",
                patientId?.let { patientLogic.getPatient(patientId) },
                mappings ?: HashMap()).map { mapper.map(it, CheckSMFPatientResult::class.java) }
    }

    @ApiOperation(nickname = "importSumehr", value = "Import sumehr into patient(s) using existing document")
    @PostMapping("/sumehr/{documentId}/import")
    fun importSumehr(@PathVariable documentId: String, @RequestParam(required = false) documentKey: String?, @RequestParam(required = false) patientId: String?, @RequestParam(required = false) language: String?, mappings: HashMap<String, List<ImportMapping>>?): List<ImportResultDto> {
        val user = sessionLogic.currentSessionContext.user
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(user.healthcarePartyId)
        val document = documentLogic.get(documentId)

        return sumehrLogicV1.importSumehr(documentLogic.readAttachment(documentId, document.attachmentId), user, language
                ?: userHealthCareParty.languages?.firstOrNull() ?: "fr",
                patientId?.let { patientLogic.getPatient(patientId) },
                mappings ?: HashMap()).map { mapper.map(it, ImportResultDto::class.java) }
    }

    @ApiOperation(nickname = "importSumehrByItemId", value = "Import sumehr into patient(s) using existing document")
    @PostMapping("/sumehr/{documentId}/importbyitemid")
    fun importSumehrByItemId(@PathVariable documentId: String, @RequestParam(required = false) documentKey: String?, @RequestParam(required = false) itemId: String?, @RequestParam(required = false) patientId: String?, @RequestParam(required = false) language: String?, mappings: HashMap<String, List<ImportMapping>>?): List<ImportResultDto> {
        val user = sessionLogic.currentSessionContext.user
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(user.healthcarePartyId)
        val document = documentLogic.get(documentId)

        return sumehrLogicV2.importSumehrByItemId(documentLogic.readAttachment(documentId, document.attachmentId), itemId!!, user, language
                ?: userHealthCareParty.languages?.firstOrNull() ?: "fr",
                patientId?.let { patientLogic.getPatient(patientId) },
                mappings ?: HashMap()).map { mapper.map(it, ImportResultDto::class.java) }
    }

    @ApiOperation(nickname = "importMedicationScheme", value = "Import MedicationScheme into patient(s) using existing document")
    @PostMapping("/medicationscheme/{documentId}/import")
    fun importMedicationScheme(@PathVariable documentId: String, @RequestParam(required = false) documentKey: String?, @RequestParam(required = false) patientId: String?, @RequestParam(required = false) language: String?, mappings: HashMap<String, List<ImportMapping>>?): List<ImportResultDto> {

        val user = sessionLogic.currentSessionContext.user
        val userHealthCareParty = healthcarePartyLogic.getHealthcareParty(user.healthcarePartyId)
        val document = documentLogic.get(documentId)

        return medicationSchemeLogic.importMedicationSchemeFile(documentLogic.readAttachment(documentId, document.attachmentId), user, language
                ?: userHealthCareParty.languages?.firstOrNull() ?: "fr",
                patientId?.let { patientLogic.getPatient(patientId) },
                mappings ?: HashMap()).map { mapper.map(it, ImportResultDto::class.java) }
    }
}
