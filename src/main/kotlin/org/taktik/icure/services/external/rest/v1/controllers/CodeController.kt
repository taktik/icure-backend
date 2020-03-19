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

import com.google.gson.Gson
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.metadata.TypeBuilder
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.dto.filter.Filter
import org.taktik.icure.dto.filter.predicate.Predicate
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.services.external.rest.v1.dto.CodeDto
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain
import org.taktik.icure.utils.injectReactorContext
import org.taktik.icure.utils.paginatedList
import reactor.core.publisher.Flux
import java.io.Serializable

@ExperimentalCoroutinesApi
@FlowPreview
@RestController
@RequestMapping("/rest/v1/code")
@Api(tags = ["code"])
class CodeController(private val mapper: MapperFacade,
                     private val codeLogic: CodeLogic) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val DEFAULT_LIMIT = 1000

    @ApiOperation(nickname = "", value = "Finding codes by code, type and version with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, pagination is not supported")
    @GetMapping("/byLabel")
    fun findPaginatedCodesByLabel(
            @RequestParam(required = false) region: String?,
            @RequestParam(required = false) types: String?,
            @RequestParam(required = false) language: String?,
            @RequestParam(required = false) label: String?,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A code document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT

        val startKeyElements: List<String?>? = if (startKey == null) null else Gson().fromJson<List<String?>>(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit + 1)

        types?.let {
            val typesList = types.split(',')
            val wordsList = label?.split(' ') ?: listOf()
            if (typesList.size > 1 || wordsList.size > 1) {
                typesList.asFlow()
                        .map { type -> codeLogic.findCodesByLabel(region, language, type, label, paginationOffset) }
                        .flattenMerge()
                        .paginatedList<Code, CodeDto>(mapper, realLimit, object : Predicate {
                            override fun apply(input: Identifiable<String>?): Boolean {
                                return typesList.contains(input.toString())
                            }
                        })
            } else {
                codeLogic.findCodesByLabel(region, language, typesList[0], label, paginationOffset)
                        .paginatedList<Code, CodeDto>(mapper, realLimit)
            }
        } ?: codeLogic.findCodesByLabel(region, language, label, paginationOffset)
                .paginatedList<Code, CodeDto>(mapper, realLimit)

    }

    @ApiOperation(nickname = "findPaginatedCodes", value = "Finding codes by code, type and version with pagination.", notes = "Returns a list of codes matched with given input.")
    @GetMapping
    fun findPaginatedCodes(
            @RequestParam(required = false) region: String?,
            @RequestParam(required = false) type: String?,
            @RequestParam(required = false) code: String?,
            @RequestParam(required = false) version: String?,
            @ApiParam(value = "A code document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(
                getStartKey(region, type, code, version),
                startDocumentId, null,
                realLimit + 1
        )

        codeLogic.findCodesBy(region, type, code, version, paginationOffset)
                .paginatedList<Code, CodeDto>(mapper, realLimit)


    }
    private fun getStartKey(startKeyRegion: String?, startKeyType: String?, startKeyCode: String?, startKeyVersion: String?): List<String?>? {
        return if (startKeyRegion != null && startKeyType != null && startKeyCode != null && startKeyVersion != null) {
            listOf(startKeyRegion, startKeyType, startKeyCode, startKeyVersion)
        } else {
            null
        }
    }
    @ApiOperation(nickname = "findPaginatedCodesWithLink", value = "Finding codes by code, type and version with pagination.", notes = "Returns a list of codes matched with given input.")
    @GetMapping("link/{linkType}")
    fun findPaginatedCodesWithLink(
            @PathVariable linkType: String,
            @RequestParam(required = false) linkedId: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A code document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements : List<String>? = if (startKey == null) null else Gson().fromJson<List<String>>(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit + 1)
        codeLogic.findCodesByQualifiedLinkId(null, linkType, linkedId, paginationOffset)
                .paginatedList<Code, CodeDto>(mapper, realLimit)
    }



    @ApiOperation(nickname = "findCodes", value = "Finding codes by code, type and version", notes = "Returns a list of codes matched with given input.")
    @GetMapping("/byRegionTypeCode")
    fun findCodes(
            @ApiParam(value = "Code region") @RequestParam(required = false) region: String?,
            @ApiParam(value = "Code type") @RequestParam(required = false) type: String?,
            @ApiParam(value = "Code code") @RequestParam(required = false) code: String?,
            @ApiParam(value = "Code version") @RequestParam(required = false) version: String?): Flux<CodeDto> {

        return codeLogic.findCodesBy(region, type, code, version)
                .map { c -> mapper.map(c, CodeDto::class.java) }
                .injectReactorContext()
    }

    @ApiOperation(nickname = "findCodeTypes", value = "Finding code types.", notes = "Returns a list of code types matched with given input.")
    @GetMapping("/codetype/byRegionType")
    fun findCodeTypes(
            @ApiParam(value = "Code region") @RequestParam(required = false) region: String?,
            @ApiParam(value = "Code type") @RequestParam(required = false) type: String?): Flux<String> {
        return codeLogic.findCodeTypes(region, type)
                .injectReactorContext()
    }

    @ApiOperation(nickname = "findTagTypes", value = "Finding tag types.", notes = "Returns a list of tag types matched with given input.")
    @GetMapping("/tagtype/byRegionType")
    fun findTagTypes(
            @ApiParam(value = "Code region") @RequestParam(required = false) region: String?,
            @ApiParam(value = "Code type") @RequestParam(required = false) type: String?): Flux<String> {
        val tagTypeCandidates = codeLogic.getTagTypeCandidates()
        return codeLogic.findCodeTypes(region, type)
                .filter { tagTypeCandidates.contains(it) }
                .injectReactorContext()
    }

    @ApiOperation(nickname = "createCode", value = "Create a Code", notes = "Type, Code and Version are required.")
    @PostMapping
    fun createCode(@RequestBody c: CodeDto) = mono {
        val code = codeLogic.create(mapper.map(c, Code::class.java))
        mapper.map(code, CodeDto::class.java)
    }

    @ApiOperation(nickname = "getCodes", value = "Get a list of codes by ids", notes = "Keys must be delimited by coma")
    @GetMapping("/byIds/{codeIds}")
    fun getCodes(@PathVariable codeIds: String): Flux<CodeDto> {
        val codes = codeLogic.get(codeIds.split(','))
        return codes
                .map { f -> mapper.map(f, CodeDto::class.java) }
                .injectReactorContext()
    }

    @ApiOperation(nickname = "getCode", value = "Get a code", notes = "Get a code based on ID or (code,type,version) as query strings. (code,type,version) is unique.")
    @GetMapping("/{codeId}")
    fun getCode(@ApiParam(value = "Code id") @PathVariable codeId: String) = mono {
        val c = codeLogic.get(codeId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "A problem regarding fetching the code. Read the app logs.")
        mapper.map(c, CodeDto::class.java)
    }

    @ApiOperation(nickname = "getCodeWithParts", value = "Get a code", notes = "Get a code based on ID or (code,type,version) as query strings. (code,type,version) is unique.")
    @GetMapping("/{type}/{code}/{version}")
    fun getCodeWithParts(
            @ApiParam(value = "Code type") @PathVariable type: String,
            @ApiParam(value = "Code code") @PathVariable code: String,
            @ApiParam(value = "Code version") @PathVariable version: String) = mono {

        val c = codeLogic.get(type, code, version)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "A problem regarding fetching the code with parts. Read the app logs.")
        mapper.map(c, CodeDto::class.java)
    }

    @ApiOperation(nickname = "modifyCode", value = "Modify a code", notes = "Modification of (type, code, version) is not allowed.")
    @PutMapping
    fun modifyCode(@RequestBody codeDto: CodeDto) = mono {
        val modifiedCode = try {
            codeLogic.modify(mapper.map(codeDto, Code::class.java))
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "A problem regarding modification of the code. Read the app logs: " + e.message)
        }
        mapper.map(modifiedCode, CodeDto::class.java)
    }

    @ApiOperation(nickname = "filterBy", value = "Filter codes ", notes = "Returns a list of codes along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/filter")
    fun filterBy(
            @ApiParam(value = "The start key for pagination, depends on the filters used") @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
            @ApiParam(value = "Skip rows") @RequestParam(required = false) skip: Int?,
            @ApiParam(value = "Sort key") @RequestParam(required = false) sort: String?,
            @ApiParam(value = "Descending") @RequestParam(required = false) desc: Boolean?,
            @RequestBody(required = false) filterChain: FilterChain?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyList = startKey?.split(',')?.filter { it.isNotBlank() }?.map { it.trim() } ?: listOf()
        val paginationOffset = PaginationOffset(startKeyList, startDocumentId, skip, realLimit + 1)

        var codes: Flow<ViewQueryResultEvent>? = null
        val timing = System.currentTimeMillis()
        filterChain?.let {
            codes = codeLogic.listCodes(paginationOffset, org.taktik.icure.dto.filter.chain.FilterChain(filterChain.filter as Filter<String, Patient>, mapper.map(filterChain.predicate, Predicate::class.java)), sort, desc)
        }
        logger.info("Filter codes in " + (System.currentTimeMillis() - timing) + " ms.")
        codes?.let {
            it.paginatedList<Code, CodeDto>(mapper, realLimit)
        } ?:throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Listing codes failed.")
    }
}
