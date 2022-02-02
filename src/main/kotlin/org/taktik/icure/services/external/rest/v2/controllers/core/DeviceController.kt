package org.taktik.icure.services.external.rest.v2.controllers.core

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
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
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.DeviceLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.entities.Device
import org.taktik.icure.services.external.rest.v2.dto.DeviceDto
import org.taktik.icure.services.external.rest.v2.dto.IdWithRevDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.dto.filter.AbstractFilterDto
import org.taktik.icure.services.external.rest.v2.dto.filter.chain.FilterChain
import org.taktik.icure.services.external.rest.v2.mapper.DeviceV2Mapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController("deviceControllerV2")
@RequestMapping("/rest/v2/device")
@Tag(name = "device")
class DeviceController(private val filters: Filters,
                       private val deviceLogic: DeviceLogic,
                       private val deviceV2Mapper: DeviceV2Mapper) {

    companion object {
        private val log = LoggerFactory.getLogger(javaClass)
        private const val DEFAULT_LIMIT = 1000
    }

    @Operation(summary = "Get Device", description = "It gets device administrative data.")
    @GetMapping("/{deviceId}")
    fun getDevice(@PathVariable deviceId: String) = mono {
        deviceLogic.getDevice(deviceId)?.let { deviceV2Mapper.map(it)}
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting device failed. Possible reasons: no such device exists, or server error. Please try again or read the server log.")
    }

    @Operation(summary = "Get devices by id", description = "It gets device administrative data.")
    @PostMapping("/byIds")
    fun getDevices(@RequestBody deviceIds: ListOfIdsDto): Flux<DeviceDto> {
        return deviceLogic.getDevices(deviceIds.ids)
                .map { deviceV2Mapper.map(it) }
                .injectReactorContext()
    }

    @Operation(summary = "Create a device", description = "Name, last name, date of birth, and gender are required. After creation of the device and obtaining the ID, you need to create an initial delegation.")
    @PostMapping
    fun createDevice(@RequestBody p: DeviceDto) = mono {
        deviceLogic.createDevice(deviceV2Mapper.map(p))?.let { deviceV2Mapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Device creation failed.")
    }

    @Operation(summary = "Modify a device", description = "Returns the updated device")
    @PutMapping
    fun modifyDevice(@RequestBody deviceDto: DeviceDto) = mono {
        deviceLogic.modifyDevice(deviceV2Mapper.map(deviceDto))?.let { deviceV2Mapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting device failed. Possible reasons: no such device exists, or server error. Please try again or read the server log.").also { log.error(it.message) }
    }

    @Operation(summary = "Create devices in bulk", description = "Returns the id and _rev of created devices")
    @PostMapping("/bulk", "/batch")
    fun createDevices(@RequestBody deviceDtos: List<DeviceDto>) = mono {
        try {
            val devices = deviceLogic.createDevices(deviceDtos.map { p -> deviceV2Mapper.map(p) }.toList())
            devices.map { p -> IdWithRevDto(id = p.id, rev = p.rev) }.toList()
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @Operation(summary = "Modify devices in bulk", description = "Returns the id and _rev of modified devices")
    @PutMapping("/bulk", "/batch")
    fun bulkUpdateDevices(@RequestBody deviceDtos: List<DeviceDto>) = mono {
        try {
            val devices = deviceLogic.modifyDevices(deviceDtos.map { p -> deviceV2Mapper.map(p) }.toList())
            devices.map { p -> IdWithRevDto(id = p.id, rev = p.rev) }.toList()
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @Operation(summary = "Filter devices for the current user (HcParty) ", description = "Returns a list of devices along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/filter")
    fun filterDevicesBy(
            @Parameter(description = "The start key for pagination, depends on the filters used") @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A device document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
            @Parameter(description = "Skip rows") @RequestParam(required = false) skip: Int?,
            @Parameter(description = "Sort key") @RequestParam(required = false) sort: String?,
            @Parameter(description = "Descending") @RequestParam(required = false) desc: Boolean?,
            @RequestBody filterChain: FilterChain<Device>) = mono {
        TODO("Not Implemented yet")
    }

    @Operation(summary = "Get ids of devices matching the provided filter for the current user (HcParty) ")
    @PostMapping("/match")
    fun matchDevicesBy(@RequestBody filter: AbstractFilterDto<Device>) = mono {
        filters.resolve(filter).toList()
    }

    @Operation(summary = "Delete device.", description = "Response contains the id/rev of deleted device.")
    @DeleteMapping("/{deviceId}")
    fun deleteDevice(@PathVariable deviceId: String) = mono {
        try {
            deviceLogic.deleteDevice(deviceId) ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Device deletion failed")
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { log.error(it.message) }
        }
    }

    @Operation(summary = "Delete devices.", description = "Response is an array containing the id/rev of deleted devices.")
    @DeleteMapping("/delete/batch")
    fun deleteDevices(@RequestBody deviceIds: ListOfIdsDto): Flux<DocIdentifier> {
        return try{
            deviceLogic.deleteDevices(deviceIds.ids.toSet()).injectReactorContext()
        }
        catch (e: Exception){
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Devices deletion failed").also { log.error(it.message) }
        }
    }
}

