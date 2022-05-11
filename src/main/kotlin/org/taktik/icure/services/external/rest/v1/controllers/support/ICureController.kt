/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
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
import org.taktik.icure.services.external.rest.v1.dto.ContactDto
import org.taktik.icure.services.external.rest.v1.dto.DocumentDto
import org.taktik.icure.services.external.rest.v1.dto.FormDto
import org.taktik.icure.services.external.rest.v1.dto.HealthElementDto
import org.taktik.icure.services.external.rest.v1.dto.IndexingInfoDto
import org.taktik.icure.services.external.rest.v1.dto.InvoiceDto
import org.taktik.icure.services.external.rest.v1.dto.MessageDto
import org.taktik.icure.services.external.rest.v1.dto.PatientDto
import org.taktik.icure.services.external.rest.v1.mapper.ContactMapper
import org.taktik.icure.services.external.rest.v1.mapper.DocumentMapper
import org.taktik.icure.services.external.rest.v1.mapper.FormMapper
import org.taktik.icure.services.external.rest.v1.mapper.HealthElementMapper
import org.taktik.icure.services.external.rest.v1.mapper.InvoiceMapper
import org.taktik.icure.services.external.rest.v1.mapper.MessageMapper
import org.taktik.icure.services.external.rest.v1.mapper.PatientMapper
import org.taktik.icure.services.external.rest.v1.mapper.UserMapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/icure")
@Tag(name = "icure")
class ICureController(
	private val iCureLogic: ICureLogicImpl,
	private val patientLogic: PatientLogic,
	private val userLogic: UserLogic,
	private val contactLogic: ContactLogic,
	private val messageLogic: MessageLogic,
	private val invoiceLogic: InvoiceLogic,
	private val documentLogic: DocumentLogic,
	private val healthElementLogic: HealthElementLogic,
	private val formLogic: FormLogic,
	private val sessionLogic: AsyncSessionLogic,
	private val userMapper: UserMapper,
	private val patientMapper: PatientMapper,
	private val contactMapper: ContactMapper,
	private val healthElementMapper: HealthElementMapper,
	private val formMapper: FormMapper,
	private val invoiceMapper: InvoiceMapper,
	private val messageMapper: MessageMapper,
	private val documentMapper: DocumentMapper,
) {

	@Operation(summary = "Get version")
	@GetMapping("/v", produces = [MediaType.TEXT_PLAIN_VALUE])
	fun getVersion(): String = iCureLogic.getVersion()

	@Operation(summary = "Check if a user exists")
	@GetMapping("/ok", produces = [MediaType.TEXT_PLAIN_VALUE])
	fun isReady() = "true"

	@Operation(summary = "Get process info")
	@GetMapping("/p", produces = [MediaType.TEXT_PLAIN_VALUE])
	fun getProcessInfo(): String = java.lang.management.ManagementFactory.getRuntimeMXBean().name

	@Operation(summary = "Get index info")
	@GetMapping("/i")
	fun getIndexingInfo() = mono {
		IndexingInfoDto(iCureLogic.getIndexingStatus())
	}

	@Operation(summary = "Get replication info")
	@GetMapping("/r")
	fun getReplicationInfo() = mono {
		iCureLogic.getReplicationInfo()
	}

	@Operation(summary = "Force update design doc")
	@PostMapping("/dd/{entityName}")
	fun updateDesignDoc(@PathVariable entityName: String, @RequestParam(required = false) warmup: Boolean? = null) = mono {
		iCureLogic.modifyDesignDoc(entityName, warmup ?: false)
		true
	}

	@Operation(summary = "Resolve patients conflicts")
	@PostMapping("/conflicts/patient")
	fun resolvePatientsConflicts(): Flux<PatientDto> = patientLogic.solveConflicts().map { patientMapper.map(it) }.injectReactorContext()

	@Operation(summary = "Resolve contacts conflicts")
	@PostMapping("/conflicts/contact")
	fun resolveContactsConflicts(): Flux<ContactDto> = contactLogic.solveConflicts().map { contactMapper.map(it) }.injectReactorContext()

	@Operation(summary = "resolve forms conflicts")
	@PostMapping("/conflicts/form")
	fun resolveFormsConflicts(): Flux<FormDto> = formLogic.solveConflicts().map { formMapper.map(it) }.injectReactorContext()

	@Operation(summary = "resolve healthcare elements conflicts")
	@PostMapping("/conflicts/healthelement")
	fun resolveHealthElementsConflicts(): Flux<HealthElementDto> = healthElementLogic.solveConflicts().map { healthElementMapper.map(it) }.injectReactorContext()

	@Operation(summary = "resolve invoices conflicts")
	@PostMapping("/conflicts/invoice")
	fun resolveInvoicesConflicts(): Flux<InvoiceDto> = invoiceLogic.solveConflicts().map { invoiceMapper.map(it) }.injectReactorContext()

	@Operation(summary = "resolve messages conflicts")
	@PostMapping("/conflicts/message")
	fun resolveMessagesConflicts(): Flux<MessageDto> = messageLogic.solveConflicts().map { messageMapper.map(it) }.injectReactorContext()

	@Operation(summary = "resolve documents conflicts")
	@PostMapping("/conflicts/document")
	fun resolveDocumentsConflicts(@RequestParam(required = false) ids: String?): Flux<DocumentDto> = documentLogic.solveConflicts(ids?.split(",")).map { documentMapper.map(it) }.injectReactorContext()

	@PostMapping("/loglevel/{loglevel}", produces = [MediaType.TEXT_PLAIN_VALUE])
	@Throws(Exception::class)
	fun loglevel(@PathVariable("loglevel") logLevel: String, @RequestParam(value = "package") packageName: String) = mono {
		iCureLogic.setLogLevel(logLevel, packageName)
	}
}
