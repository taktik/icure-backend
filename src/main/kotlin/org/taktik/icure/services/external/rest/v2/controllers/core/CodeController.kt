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

package org.taktik.icure.services.external.rest.v2.controllers.core

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.predicate.Predicate
import org.taktik.icure.entities.base.Code
import org.taktik.icure.services.external.rest.v2.dto.CodeDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.dto.filter.AbstractFilterDto
import org.taktik.icure.services.external.rest.v2.dto.filter.chain.FilterChain
import org.taktik.icure.services.external.rest.v2.mapper.base.CodeV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.filter.FilterChainV2Mapper
import org.taktik.icure.services.external.rest.v2.utils.paginatedList
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
@FlowPreview
@RestController("codeControllerV2")
@RequestMapping("/rest/v2/code")
@Tag(name = "code")
class CodeController(
	private val filters: Filters,
	private val codeLogic: CodeLogic,
	private val codeV2Mapper: CodeV2Mapper,
	private val filterChainV2Mapper: FilterChainV2Mapper,
	private val objectMapper: ObjectMapper
) {
	private val logger = LoggerFactory.getLogger(javaClass)
	private val DEFAULT_LIMIT = 1000
	private val codeToCodeDto = { it: Code -> codeV2Mapper.map(it) }

	@Operation(summary = "Finding codes by code, type and version with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, pagination is not supported")
	@GetMapping("/byLabel")
	fun findCodesByLabel(
		@RequestParam(required = false) region: String?,
		@RequestParam(required = false) types: String?,
		@RequestParam(required = false) language: String?,
		@RequestParam(required = false) label: String?,
		@RequestParam(required = false) version: String?,
		@Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
		@Parameter(description = "A code document ID") @RequestParam(required = false) startDocumentId: String?,
		@Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?
	) = mono {

		val realLimit = limit ?: DEFAULT_LIMIT

		val startKeyElements: List<String?>? = if (startKey == null) null else objectMapper.readValue<List<String>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java))
		val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit + 1)

		types?.let {
			val typesList = types.split(',')
			val wordsList = label?.split(' ') ?: listOf()
			if (typesList.size > 1 || wordsList.size > 1) {
				typesList.asFlow()
					.map { type -> codeLogic.findCodesByLabel(region, language, type, label, paginationOffset) }
					.flattenMerge()
					.paginatedList<Code, CodeDto>(
						codeToCodeDto, realLimit,
						object : Predicate {
							override fun apply(input: Identifiable<String>): Boolean {
								return typesList.contains(input.toString())
							}
						}
					)
			} else {
				codeLogic.findCodesByLabel(region, language, typesList[0], label, paginationOffset)
					.paginatedList<Code, CodeDto>(codeToCodeDto, realLimit)
			}
		} ?: codeLogic.findCodesByLabel(region, language, label, version, paginationOffset)
			.paginatedList<Code, CodeDto>(codeToCodeDto, realLimit)
	}

	@Operation(summary = "Finding codes by code, type and version with pagination.", description = "Returns a list of codes matched with given input.")
	@GetMapping
	fun findCodesByType(
		@RequestParam(required = false) region: String?,
		@RequestParam(required = false) type: String?,
		@RequestParam(required = false) code: String?,
		@RequestParam(required = false) version: String?,
		@Parameter(description = "The start key for pagination") @RequestParam(required = false) startKey: String?,
		@Parameter(description = "A code document ID") @RequestParam(required = false) startDocumentId: String?,
		@Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?
	) = mono {

		val realLimit = limit ?: DEFAULT_LIMIT
		val startKeyElements = if (startKey == null) null else objectMapper.readValue<List<String?>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java))
		val paginationOffset = PaginationOffset(
			startKeyElements,
			startDocumentId, null,
			realLimit + 1
		)

		codeLogic.findCodesBy(region, type, code, version, paginationOffset)
			.paginatedList(codeToCodeDto, realLimit)
	}

	private fun getStartKey(startKeyRegion: String?, startKeyType: String?, startKeyCode: String?, startKeyVersion: String?): List<String?>? {
		return if (startKeyRegion != null && startKeyType != null && startKeyCode != null && startKeyVersion != null) {
			listOf(startKeyRegion, startKeyType, startKeyCode, startKeyVersion)
		} else {
			null
		}
	}

	@Operation(summary = "Finding codes by code, type and version with pagination.", description = "Returns a list of codes matched with given input.")
	@GetMapping("/byLink/{linkType}")
	fun findCodesByLink(
		@PathVariable linkType: String,
		@RequestParam(required = false) linkedId: String,
		@Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey")
		@RequestParam(required = false) startKey: String?,
		@Parameter(description = "A code document ID") @RequestParam(required = false) startDocumentId: String?,
		@Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?
	) = mono {

		val realLimit = limit ?: DEFAULT_LIMIT
		val startKeyElements: List<String>? = if (startKey == null) null else objectMapper.readValue<List<String>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java))
		val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit + 1)
		codeLogic.findCodesByQualifiedLinkId(null, linkType, linkedId, paginationOffset)
			.paginatedList<Code, CodeDto>(codeToCodeDto, realLimit)
	}

	@Operation(summary = "Finding codes by code, type and version", description = "Returns a list of codes matched with given input.")
	@GetMapping("/byRegionTypeCode")
	fun listCodesByRegionTypeCodeVersion(
		@Parameter(description = "Code region") @RequestParam(required = false) region: String?,
		@Parameter(description = "Code type") @RequestParam(required = false) type: String?,
		@Parameter(description = "Code code") @RequestParam(required = false) code: String?,
		@Parameter(description = "Code version") @RequestParam(required = false) version: String?
	): Flux<CodeDto> {

		return codeLogic.findCodesBy(region, type, code, version)
			.map { c -> codeV2Mapper.map(c) }
			.injectReactorContext()
	}

	@Operation(summary = "Finding code types.", description = "Returns a list of code types matched with given input.")
	@GetMapping("/codetype/byRegionType")
	fun listCodeTypesBy(
		@Parameter(description = "Code region") @RequestParam(required = false) region: String?,
		@Parameter(description = "Code type") @RequestParam(required = false) type: String?
	): Flux<String> {
		return codeLogic.findCodeTypes(region, type)
			.injectReactorContext()
	}

	@Operation(summary = "Finding tag types.", description = "Returns a list of tag types matched with given input.")
	@GetMapping("/tagtype/byRegionType")
	fun listTagTypesBy(
		@Parameter(description = "Code region") @RequestParam(required = false) region: String?,
		@Parameter(description = "Code type") @RequestParam(required = false) type: String?
	): Flux<String> {
		val tagTypeCandidates = codeLogic.getTagTypeCandidates()
		return codeLogic.findCodeTypes(region, type)
			.filter { tagTypeCandidates.contains(it) }
			.injectReactorContext()
	}

	@Operation(summary = "Create a Code", description = "Type, Code and Version are required.")
	@PostMapping
	fun createCode(@RequestBody c: CodeDto) = mono {
		val code = codeLogic.create(codeV2Mapper.map(c))
		code?.let { codeV2Mapper.map(it) }
	}

	@Operation(summary = "Create a batch of codes", description = "Create a batch of code entities. Fields Type, Code and Version are required for each code.")
	@PostMapping("/batch")
	fun createCodes(@RequestBody codeBatch: List<CodeDto>) = mono {
		val codes = codeBatch.map { codeV2Mapper.map(it) }
		try {
			codeLogic.create(codes)?.map { codeV2Mapper.map(it) }
		} catch (e: IllegalStateException) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
		}
	}

	@Operation(summary = "Get a list of codes by ids", description = "Keys must be delimited by coma")
	@PostMapping("/byIds")
	fun getCodes(@RequestBody codeIds: ListOfIdsDto): Flux<CodeDto> {
		return codeIds.ids.takeIf { it.isNotEmpty() }
			?.let { ids ->
				try {
					codeLogic
						.getCodes(ids)
						.map { f -> codeV2Mapper.map(f) }
						.injectReactorContext()
				} catch (e: java.lang.Exception) {
					throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
				}
			}
			?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
	}

	@Operation(summary = "Get a code", description = "Get a code based on ID or (code,type,version) as query strings. (code,type,version) is unique.")
	@GetMapping("/{codeId}")
	fun getCode(@Parameter(description = "Code id") @PathVariable codeId: String) = mono {
		val c = codeLogic.get(codeId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "A problem regarding fetching the code. Read the app logs.")
		codeV2Mapper.map(c)
	}

	@Operation(summary = "Get a code", description = "Get a code based on ID or (code,type,version) as query strings. (code,type,version) is unique.")
	@GetMapping("/{type}/{code}/{version}")
	fun getCodeWithParts(
		@Parameter(description = "Code type") @PathVariable type: String,
		@Parameter(description = "Code code") @PathVariable code: String,
		@Parameter(description = "Code version") @PathVariable version: String
	) = mono {

		val c = codeLogic.get(type, code, version)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "A problem regarding fetching the code with parts. Read the app logs.")
		codeV2Mapper.map(c)
	}

	@Operation(summary = "Modify a code", description = "Modification of (type, code, version) is not allowed.")
	@PutMapping
	fun modifyCode(@RequestBody codeDto: CodeDto) = mono {
		val modifiedCode = try {
			codeLogic.modify(codeV2Mapper.map(codeDto))
		} catch (e: Exception) {
			throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "A problem regarding modification of the code. Read the app logs: " + e.message)
		}
		modifiedCode?.let { codeV2Mapper.map(it) }
	}

	@Operation(summary = "Modify a batch of codes", description = "Modification of (type, code, version) is not allowed.")
	@PutMapping("/batch")
	fun modifyCodes(@RequestBody codeBatch: List<CodeDto>) =
		codeLogic.modify(codeBatch.map { codeV2Mapper.map(it) })
			.catch { e ->
				if (e is IllegalStateException) throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
				else throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "A problem regarding modification of the code. Read the app logs: " + e.message)
			}
			.map { codeV2Mapper.map(it) }
			.injectReactorContext()

	@Operation(summary = "Filter codes ", description = "Returns a list of codes along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
	@PostMapping("/filter")
	fun filterCodesBy(
		@Parameter(description = "The start key for pagination, depends on the filters used") @RequestParam(required = false) startKey: String?,
		@Parameter(description = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
		@Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
		@Parameter(description = "Skip rows") @RequestParam(required = false) skip: Int?,
		@Parameter(description = "Sort key") @RequestParam(required = false) sort: String?,
		@Parameter(description = "Descending") @RequestParam(required = false) desc: Boolean?,
		@RequestBody(required = false) filterChain: FilterChain<Code>
	) = mono {

		val realLimit = limit ?: DEFAULT_LIMIT
		val startKeyList = startKey?.split(',')?.filter { it.isNotBlank() }?.map { it.trim() } ?: listOf()
		val paginationOffset = PaginationOffset(startKeyList, startDocumentId, skip, realLimit + 1)

		var codes: Flow<ViewQueryResultEvent>? = null
		val timing = System.currentTimeMillis()
		filterChain.let {
			codes = codeLogic.listCodes(paginationOffset, filterChainV2Mapper.map(filterChain), sort, desc)
		}
		logger.info("Filter codes in " + (System.currentTimeMillis() - timing) + " ms.")
		codes?.let {
			it.paginatedList<Code, CodeDto>(codeToCodeDto, realLimit)
		} ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Listing codes failed.")
	}

	@Operation(summary = "Get ids of code matching the provided filter for the current user (HcParty) ")
	@PostMapping("/match")
	fun matchCodesBy(@RequestBody filter: AbstractFilterDto<Code>) = filters.resolve(filter).injectReactorContext()
}
