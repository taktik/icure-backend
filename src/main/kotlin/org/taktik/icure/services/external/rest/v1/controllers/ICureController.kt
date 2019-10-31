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
import ma.glasnost.orika.MapperFacade
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.taktik.icure.constants.PropertyTypes
import org.taktik.icure.logic.*
import org.taktik.icure.logic.impl.ICureLogicImpl
import org.taktik.icure.services.external.rest.v1.dto.IndexingInfoDto
import org.taktik.icure.services.external.rest.v1.dto.ReplicationInfoDto
import org.taktik.icure.services.external.rest.v1.dto.UserStubDto

@RestController
@RequestMapping("/rest/v1/icure")
@Api(tags = ["icure"])
class ICureController(private val replicationLogic: ReplicationLogic,
                      private val iCureLogic: ICureLogicImpl,
                      private val patientLogic: PatientLogic,
                      private val userLogic: UserLogic,
                      private val contactLogic: ContactLogic,
                      private val messageLogic: MessageLogic,
                      private val invoiceLogic: InvoiceLogic,
                      private val documentLogic: DocumentLogic,
                      private val healthElementLogic: HealthElementLogic,
                      private val formLogic: FormLogic,
                      private val sessionLogic: ICureSessionLogic,
                      private val mapper: MapperFacade) {

    @ApiOperation(nickname = "getVersion", value = "Get version")
    @GetMapping("/v", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getVersion(): String = iCureLogic.version

    @ApiOperation(nickname = "isReady", value = "Check if a user exists")
    @GetMapping("/ok", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun isReady() = if (userLogic.hasEntities()) "true" else "false"

    @ApiOperation(nickname = "isPatientReady", value = "Check if a patient exists")
    @GetMapping("/pok", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun isPatientReady(): String = if (patientLogic.hasEntities()) "true" else "false"

    @ApiOperation(nickname = "getUsers", value = "Get users stubs")
    @GetMapping("/u")
    fun getUsers(): List<UserStubDto> =
            userLogic.allEntities.map { u -> mapper.map(u, UserStubDto::class.java) }

    @ApiOperation(nickname = "getProcessInfo", value = "Get process info")
    @GetMapping("/p", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getProcessInfo(): String = java.lang.management.ManagementFactory.getRuntimeMXBean().name

    @ApiOperation(nickname = "getReplicationInfo", value = "Get replication info")
    @GetMapping("/r")
    fun getReplicationInfo(): ReplicationInfoDto {
        val ri = ReplicationInfoDto()

        ri.pendingFrom = 0
        ri.pendingTo = 0

        ri.active = replicationLogic.hasEntities()
        if (ri.active) {
            val pendingChanges = replicationLogic.pendingChanges
            ri.running = pendingChanges.isNotEmpty()
            for ((key, value) in pendingChanges) {
                val src = key.source
                if (src.contains("127.0.0.1") || src.contains("localhost")) {
                    if (value != null) {
                        ri.pendingFrom = if (ri.pendingFrom != null) ri.pendingFrom + value.toInt() else value.toInt()
                    } else {
                        ri.pendingFrom = null
                    }
                } else {
                    if (value != null) {
                        ri.pendingTo = if (ri.pendingTo != null) ri.pendingTo + value.toInt() else value.toInt()
                    } else {
                        ri.pendingTo = null
                    }
                }
            }
        }
        return ri
    }


    @ApiOperation(nickname = "getIndexingInfo", value = "Get index info")
    @GetMapping("/i")
    fun getIndexingInfo(): IndexingInfoDto =
            IndexingInfoDto(iCureLogic.getIndexingStatus(sessionLogic.currentSessionContext.groupId))

    @ApiOperation(nickname = "getPropertyTypes", value = "Get property types")
    @GetMapping("/propertytypes/{type}")
    fun getPropertyTypes(@PathVariable type: String): List<String> {
        return if (type == "system") PropertyTypes.System.identifiers() else PropertyTypes.User.identifiers()
    }

    @ApiOperation(nickname = "updateDesignDoc", value = "Force update design doc")
    @GetMapping("/dd/{entityName}")
    fun updateDesignDoc(@PathVariable entityName: String): Boolean {
        iCureLogic.updateDesignDoc(sessionLogic.currentSessionContext.groupId, entityName)
        return true
    }

    @ApiOperation(nickname = "resolvePatientsConflicts")
    @PostMapping("/conflicts/patient")
    fun resolvePatientsConflicts() {
        patientLogic.solveConflicts()
    }

    @ApiOperation(nickname = "resolveContactsConflicts")
    @PostMapping("/conflicts/contact")
    fun resolveContactsConflicts() {
        contactLogic.solveConflicts()
    }

    @ApiOperation(nickname = "resolveFormsConflicts")
    @PostMapping("/conflicts/form")
    fun resolveFormsConflicts() {
        formLogic.solveConflicts()
    }

    @ApiOperation(nickname = "resolveHealthElementsConflicts")
    @PostMapping("/conflicts/healthelement")
    fun resolveHealthElementsConflicts() {
        healthElementLogic.solveConflicts()
    }

    @ApiOperation(nickname = "resolveInvoicesConflicts")
    @PostMapping("/conflicts/invoice")
    fun resolveInvoicesConflicts() {
        invoiceLogic.solveConflicts()
    }

    @ApiOperation(nickname = "resolveMessagesConflicts")
    @PostMapping("/conflicts/message")
    fun resolveMessagesConflicts() {
        messageLogic.solveConflicts()
    }

    @ApiOperation(nickname = "resolveDocumentsConflicts")
    @PostMapping("/conflicts/document")
    fun resolveDocumentsConflicts(@RequestParam(required = false) ids: String?) {
        documentLogic.solveConflicts(ids?.split(','))
    }
}
