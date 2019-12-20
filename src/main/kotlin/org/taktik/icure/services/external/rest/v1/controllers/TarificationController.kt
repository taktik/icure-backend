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
import io.swagger.annotations.ApiParam
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.ViewRow
import org.taktik.icure.asynclogic.impl.TarificationLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Tarification
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.TarificationDto
import org.taktik.icure.utils.paginatedList

@RestController
@RequestMapping("/rest/v1/tarification")
@Api(tags = ["tarification"])
class TarificationController(private val mapper: MapperFacade,
                             private val tarificationLogic: TarificationLogic) {

    private val DEFAULT_LIMIT = 1000

    @ApiOperation(nickname = "findPaginatedTarificationsByLabel", value = "Finding tarifications by tarification, type and version with pagination.", notes = "Returns a list of tarifications matched with given input.")
    @GetMapping("/byLabel")
    suspend fun findPaginatedTarificationsByLabel(
            @RequestParam(required = false) region: String?,
            @RequestParam(required = false) types: String?,
            @RequestParam(required = false) language: String?,
            @RequestParam(required = false) label: String?,
            @ApiParam(value = "A tarification document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<TarificationDto> {
        val realLimit = limit ?: DEFAULT_LIMIT
        val tarificationsList = tarificationLogic.findTarificationsByLabel(region, language, label, PaginationOffset(listOf(region, language, label), startDocumentId, null, realLimit+1))

        types?.let {
            val splits = it.split(',')
            tarificationsList.filter { d ->
                if (d is ViewRow<*, *, *> && d.value is Tarification) {
                    splits.contains(d.value)
                } else true
            }
        }

        return tarificationsList.paginatedList<Tarification, TarificationDto>(mapper, realLimit)
    }

    @ApiOperation(nickname = "findPaginatedTarifications", value = "Finding tarifications by tarification, type and version with pagination.", notes = "Returns a list of tarifications matched with given input.")
    @GetMapping
    suspend fun findPaginatedTarifications(
            @RequestParam(required = false) region: String?,
            @RequestParam(required = false) type: String?,
            @RequestParam(required = false) tarification: String?,
            @RequestParam(required = false) version: String?,
            @ApiParam(value = "A tarification document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<TarificationDto> {
        val realLimit = limit ?: DEFAULT_LIMIT
        fun getStartKey(startKeyRegion: String?, startKeyType: String?, startKeyTarification: String?, startKeyVersion: String?): List<String?>? =
                if (startKeyRegion != null && startKeyType != null && startKeyTarification != null && startKeyVersion != null) {
                    listOf(startKeyRegion, startKeyType, startKeyTarification, startKeyVersion)
                } else {
                    null
                }


        val tarificationsList = tarificationLogic.findTarificationsBy(
                region, type, tarification, version,
                PaginationOffset(getStartKey(region, type, tarification, version), startDocumentId, null, realLimit+1)
        )

        return tarificationsList.paginatedList<Tarification, TarificationDto>(mapper, realLimit)
    }


    @ApiOperation(nickname = "findTarifications", value = "Finding tarifications by tarification, type and version", notes = "Returns a list of tarifications matched with given input.")
    @GetMapping("/byRegionTypeTarification")
    fun findTarifications(
            @ApiParam(value = "Tarification region") @RequestParam(required = false) region: String?,
            @ApiParam(value = "Tarification type") @RequestParam(required = false) type: String?,
            @ApiParam(value = "Tarification tarification") @RequestParam(required = false) tarification: String?,
            @ApiParam(value = "Tarification version") @RequestParam(required = false) version: String?) = flow<TarificationDto> {
        emitAll(tarificationLogic.findTarificationsBy(region, type, tarification, version).map { mapper.map(it, TarificationDto::class.java) })
    }

    @ApiOperation(nickname = "createTarification", value = "Create a Tarification", notes = "Type, Tarification and Version are required.")
    @PostMapping
    suspend fun createTarification(@RequestBody c: TarificationDto) =
            tarificationLogic.create(mapper.map(c, Tarification::class.java))?.let { mapper.map(it, TarificationDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Tarification creation failed.")


    @ApiOperation(nickname = "getTarifications", value = "Get a list of tarifications by ids", notes = "Keys must be delimited by coma")
    @PostMapping("/byIds")
    fun getTarifications(@RequestBody tarificationIds: ListOfIdsDto) =
            tarificationLogic.get(tarificationIds.ids).map { f -> mapper.map(f, TarificationDto::class.java) }


    @ApiOperation(nickname = "getTarification", value = "Get a tarification", notes = "Get a tarification based on ID or (tarification,type,version) as query strings. (tarification,type,version) is unique.")
    @GetMapping("/{tarificationId}")
    suspend fun getTarification(@ApiParam(value = "Tarification id") @PathVariable tarificationId: String) =
            tarificationLogic.get(tarificationId)?.let { mapper.map(it, TarificationDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "A problem regarding fetching the tarification. Read the app logs.")

    @ApiOperation(nickname = "getTarificationWithParts", value = "Get a tarification", notes = "Get a tarification based on ID or (tarification,type,version) as query strings. (tarification,type,version) is unique.")
    @GetMapping("/{type}/{tarification}/{version}")
    suspend fun getTarificationWithParts(
            @ApiParam(value = "Tarification type", required = true) @PathVariable type: String,
            @ApiParam(value = "Tarification tarification", required = true) @PathVariable tarification: String,
            @ApiParam(value = "Tarification version", required = true) @PathVariable version: String) =
            tarificationLogic.get(type, tarification, version)?.let { mapper.map(it, TarificationDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "A problem regarding fetching the tarification. Read the app logs.")


    @ApiOperation(nickname = "modifyTarification", value = "Modify a tarification", notes = "Modification of (type, tarification, version) is not allowed.")
    @PutMapping
    suspend fun modifyTarification(@RequestBody tarificationDto: TarificationDto): TarificationDto =
            try {
                tarificationLogic.modify(mapper.map(tarificationDto, Tarification::class.java))?.let { mapper.map(it, TarificationDto::class.java) }
                        ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Modification of the tarification failed. Read the server log.")
            } catch (e: Exception) {
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "A problem regarding modification of the tarification. Read the app logs: ")
            }
}