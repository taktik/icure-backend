/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.controllers.support

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.MaintenanceTaskLogic
import org.taktik.icure.entities.MaintenanceTask
import org.taktik.icure.services.external.rest.v1.dto.MaintenanceTaskDto
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain
import org.taktik.icure.services.external.rest.v1.mapper.MaintenanceTaskMapper
import org.taktik.icure.services.external.rest.v1.mapper.filter.FilterChainMapper
import org.taktik.icure.services.external.rest.v1.utils.paginatedList
import org.taktik.icure.utils.injectReactorContext

@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/maintenancetask")
@Tag(name = "maintenanceTask")
class MaintenanceTaskController(
	private val maintenanceTaskLogic: MaintenanceTaskLogic,
	private val maintenanceTaskMapper: MaintenanceTaskMapper,
	private val filterChainMapper: FilterChainMapper,
) {
	private val log = LoggerFactory.getLogger(javaClass)

	private val maintenanceTaskToMaintenanceTaskDto = { it: MaintenanceTask -> maintenanceTaskMapper.map(it) }

	@Operation(summary = "Creates a maintenanceTask")
	@PostMapping
	fun createMaintenanceTask(@RequestBody maintenanceTaskDto: MaintenanceTaskDto) = mono {
		maintenanceTaskLogic.createEntities(listOf(maintenanceTaskMapper.map(maintenanceTaskDto)))
			.catch { e ->
				if (e is Exception)
					throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "MaintenanceTask creation failed.")
			}
			.firstOrNull()
			?.let {
				maintenanceTaskMapper.map(it)
			} ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "MaintenanceTask creation failed.")
	}

	@Operation(summary = "Delete maintenanceTasks")
	@DeleteMapping("/{maintenanceTaskIds}")
	fun deleteMaintenanceTask(@PathVariable maintenanceTaskIds: String) =
		try {
			maintenanceTaskLogic.deleteEntities(maintenanceTaskIds.split(',')).injectReactorContext()
		} catch (e: Exception) {
			throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "MaintenanceTask deletion failed")
		}

	@Operation(summary = "Gets a maintenanceTask")
	@GetMapping("/{maintenanceTaskId}")
	fun getMaintenanceTask(@PathVariable maintenanceTaskId: String) = mono {
		maintenanceTaskLogic.getEntity(maintenanceTaskId)?.let { maintenanceTaskMapper.map(it) }
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "MaintenanceTask not found")
	}

	@Operation(summary = "Updates a maintenanceTask")
	@PutMapping
	fun modifyMaintenanceTask(@RequestBody maintenanceTaskDto: MaintenanceTaskDto) = mono {
		val maintenanceTask = maintenanceTaskMapper.map(maintenanceTaskDto)
		try {
			maintenanceTaskLogic.modifyEntities(listOf(maintenanceTask)).map { maintenanceTaskMapper.map(it) }.firstOrNull()
				?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update maintenanceTask")
		} catch (e: Exception) {
			log.error("Cannot update maintenanceTask", e)
			throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "MaintenanceTask modification failed")
		}
	}

	@Operation(summary = "Filter maintenanceTasks for the current user (HcParty) ", description = "Returns a list of maintenanceTasks along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
	@PostMapping("/filter")
	fun filterMaintenanceTasksBy(
		@Parameter(description = "A maintenanceTask document ID") @RequestParam(required = false) startDocumentId: String?,
		@Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
		@RequestBody filterChain: FilterChain<MaintenanceTask>
	) = mono {
		val realLimit = limit ?: DEFAULT_LIMIT

		maintenanceTaskLogic
			.filterMaintenanceTasks(filterChainMapper.map(filterChain), realLimit + 1, startDocumentId)
			.paginatedList(maintenanceTaskToMaintenanceTaskDto, realLimit)
	}

	companion object {
		const val DEFAULT_LIMIT: Int = 1000
	}
}
