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

package org.taktik.icure.services.external.rest.v1.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import ma.glasnost.orika.MapperFacade
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.taktik.icure.asynclogic.*
import org.taktik.icure.asynclogic.impl.ICureLogicImpl
import org.taktik.icure.constants.PropertyTypes
import org.taktik.icure.services.external.rest.v1.dto.IndexingInfoDto
import org.taktik.icure.services.external.rest.v1.dto.ReplicationInfoDto
import org.taktik.icure.services.external.rest.v1.dto.UserStubDto
import org.taktik.icure.utils.injectReactorContext

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/icure")
@Api(tags = ["icure"])
class ICureController(private val iCureLogic: ICureLogicImpl,
                      private val patientLogic: PatientLogic,
                      private val userLogic: UserLogic,
                      private val contactLogic: ContactLogic,
                      private val messageLogic: MessageLogic,
                      private val invoiceLogic: InvoiceLogic,
                      private val documentLogic: DocumentLogic,
                      private val healthElementLogic: HealthElementLogic,
                      private val formLogic: FormLogic,
                      private val sessionLogic: AsyncSessionLogic,
                      private val mapper: MapperFacade) {

    @ApiOperation(nickname = "getVersion", value = "Get version")
    @GetMapping("/v", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getVersion(): String = iCureLogic.getVersion()

    @ApiOperation(nickname = "isReady", value = "Check if a user exists")
    @GetMapping("/ok", produces = [MediaType.TEXT_PLAIN_VALUE])
    suspend fun isReady() = if (userLogic.hasEntities()) "true" else "false"

    @ApiOperation(nickname = "isPatientReady", value = "Check if a patient exists")
    @GetMapping("/pok", produces = [MediaType.TEXT_PLAIN_VALUE])
    suspend fun isPatientReady(): String = if (patientLogic.hasEntities()) "true" else "false"

    @ApiOperation(nickname = "getUsers", value = "Get users stubs")
    @GetMapping("/u")
    fun getUsers() = userLogic.getAllEntities().map { u -> mapper.map(u, UserStubDto::class.java) }.injectReactorContext()


    @ApiOperation(nickname = "getProcessInfo", value = "Get process info")
    @GetMapping("/p", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getProcessInfo(): String = java.lang.management.ManagementFactory.getRuntimeMXBean().name

    @ApiOperation(nickname = "getIndexingInfo", value = "Get index info")
    @GetMapping("/i")
    suspend fun getIndexingInfo(): IndexingInfoDto =
            IndexingInfoDto(iCureLogic.getIndexingStatus(sessionLogic.getCurrentSessionContext().getGroupId()))

    @ApiOperation(nickname = "getPropertyTypes", value = "Get property types")
    @GetMapping("/propertytypes/{type}")
    fun getPropertyTypes(@PathVariable type: String): List<String> {
        return if (type == "system") PropertyTypes.System.identifiers() else PropertyTypes.User.identifiers()
    }

    @ApiOperation(nickname = "updateDesignDoc", value = "Force update design doc")
    @GetMapping("/dd/{entityName}")
    suspend fun updateDesignDoc(@PathVariable entityName: String): Boolean {
        iCureLogic.updateDesignDoc(sessionLogic.getCurrentSessionContext().getGroupId(), entityName)
        return true
    }

    @ApiOperation(nickname = "resolvePatientsConflicts", value = "Resolve patients conflicts")
    @PostMapping("/conflicts/patient")
    suspend fun resolvePatientsConflicts() {
        patientLogic.solveConflicts()
    }

    @ApiOperation(nickname = "resolveContactsConflicts", value = "Resolve contacts conflicts")
    @PostMapping("/conflicts/contact")
    suspend fun resolveContactsConflicts() {
        contactLogic.solveConflicts()
    }

    @ApiOperation(nickname = "resolveFormsConflicts", value = "resolve forms conflicts")
    @PostMapping("/conflicts/form")
    suspend fun resolveFormsConflicts() {
        formLogic.solveConflicts()
    }

    @ApiOperation(nickname = "resolveHealthElementsConflicts", value = "resolve health elements conflicts")
    @PostMapping("/conflicts/healthelement")
    suspend fun resolveHealthElementsConflicts() {
        healthElementLogic.solveConflicts()
    }

    @ApiOperation(nickname = "resolveInvoicesConflicts", value = "resolve invoices conflicts")
    @PostMapping("/conflicts/invoice")
    suspend fun resolveInvoicesConflicts() {
        invoiceLogic.solveConflicts()
    }

    @ApiOperation(nickname = "resolveMessagesConflicts", value = "resolve messages conflicts")
    @PostMapping("/conflicts/message")
    suspend fun resolveMessagesConflicts() {
        messageLogic.solveConflicts()
    }

    @ApiOperation(nickname = "resolveDocumentsConflicts", value = "resolve documents conflicts")
    @PostMapping("/conflicts/document")
    suspend fun resolveDocumentsConflicts(@RequestParam(required = false) ids: String?) {
        documentLogic.solveConflicts(ids?.split(','))
    }
}
