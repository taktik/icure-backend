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

package org.taktik.icure.services.external.rest.v1.controllers.support

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.FormLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.InvoiceLogic
import org.taktik.icure.asynclogic.MessageLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.impl.ICureLogicImpl
import org.taktik.icure.constants.PropertyTypes
import org.taktik.icure.services.external.rest.v1.dto.IndexingInfoDto
import org.taktik.icure.services.external.rest.v1.mapper.UserMapper
import org.taktik.icure.utils.injectReactorContext

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/icure")
@Tag(name = "icure")
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
                      private val userMapper: UserMapper) {

    @Operation(summary = "Get version")
    @GetMapping("/v", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getVersion(): String = iCureLogic.getVersion()

    @Operation(summary = "Check if a user exists")
    @GetMapping("/ok", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun isReady() = "true"

    @Operation(summary = "Check if a patient exists")
    @GetMapping("/pok", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun isPatientReady() = "true"

    @Operation(summary = "Get users stubs")
    @GetMapping("/u")
    fun getUsers() = userLogic.getAllEntities().map { u -> userMapper.map(u) }.injectReactorContext()

    @Operation(summary = "Get process info")
    @GetMapping("/p", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getProcessInfo(): String = java.lang.management.ManagementFactory.getRuntimeMXBean().name

    @Operation(summary = "Get index info")
    @GetMapping("/i")
    fun getIndexingInfo() = mono {
        IndexingInfoDto(iCureLogic.getIndexingStatus())
    }

    @Operation(summary = "Get index info")
    @GetMapping("/r")
    fun getReplicationInfo() = mono {
        iCureLogic.getReplicationInfo()
    }

    @Operation(summary = "Get property types")
    @GetMapping("/propertytypes/{type}")
    fun getPropertyTypes(@PathVariable type: String): List<String> {
        return if (type == "system") PropertyTypes.System.identifiers() else PropertyTypes.User.identifiers()
    }

    @Operation(summary = "Force update design doc")
    @PostMapping("/dd/{entityName}")
    fun updateDesignDoc(@PathVariable entityName: String, @RequestParam(required = false) warmup: Boolean? = null) = mono {
        iCureLogic.updateDesignDoc(entityName, warmup ?: false)
        true
    }

    @Operation(summary = "Resolve patients conflicts")
    @PostMapping("/conflicts/patient")
    fun resolvePatientsConflicts() = mono {
        patientLogic.solveConflicts()
    }

    @Operation(summary = "Resolve contacts conflicts")
    @PostMapping("/conflicts/contact")
    fun resolveContactsConflicts() = mono {
        contactLogic.solveConflicts()
    }

    @Operation(summary = "resolve forms conflicts")
    @PostMapping("/conflicts/form")
    fun resolveFormsConflicts() = mono {
        formLogic.solveConflicts()
    }

    @Operation(summary = "resolve health elements conflicts")
    @PostMapping("/conflicts/healthelement")
    fun resolveHealthElementsConflicts() = mono {
        healthElementLogic.solveConflicts()
    }

    @Operation(summary = "resolve invoices conflicts")
    @PostMapping("/conflicts/invoice")
    fun resolveInvoicesConflicts() = mono {
        invoiceLogic.solveConflicts()
    }

    @Operation(summary = "resolve messages conflicts")
    @PostMapping("/conflicts/message")
    fun resolveMessagesConflicts() = mono {
        messageLogic.solveConflicts()
    }

    @Operation(summary = "resolve documents conflicts")
    @PostMapping("/conflicts/document")
    fun resolveDocumentsConflicts(@RequestParam(required = false) ids: String?) = mono {
        documentLogic.solveConflicts(ids?.split(','))
    }
}
