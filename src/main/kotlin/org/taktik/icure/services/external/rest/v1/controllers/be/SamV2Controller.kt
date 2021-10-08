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

package org.taktik.icure.services.external.rest.v1.controllers.be

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.icure.asynclogic.samv2.SamV2Logic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Nmp
import org.taktik.icure.entities.samv2.Vmp
import org.taktik.icure.entities.samv2.VmpGroup
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.AmpDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.NmpDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.VmpDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.VmpGroupDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.PharmaceuticalFormDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.SubstanceDto
import org.taktik.icure.services.external.rest.v1.mapper.samv2.AmpMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.NmpMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.SamVersionMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.VmpGroupMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.VmpMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.PharmaceuticalFormMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.SubstanceMapper
import org.taktik.icure.services.external.rest.v1.utils.paginatedList
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux
import java.util.*

@RestController
@RequestMapping("/rest/v1/be_samv2")
@Tag(name = "besamv2")
class SamV2Controller(
        private val samV2Logic: SamV2Logic,
        private val ampMapper: AmpMapper,
        private val vmpMapper: VmpMapper,
        private val nmpMapper: NmpMapper,
        private val substanceMapper: SubstanceMapper,
        private val pharmaceuticalFormMapper: PharmaceuticalFormMapper,
        private val vmpGroupMapper: VmpGroupMapper,
        private val samVersionMapper: SamVersionMapper,
        private val objectMapper: ObjectMapper
) {
    private val DEFAULT_LIMIT = 1000
    private val ampToAmpDto = { it: Amp -> ampMapper.map(it) }
    private val vmpToVmpDto = { it: Vmp -> vmpMapper.map(it) }
    private val nmpToNmpDto = { it: Nmp -> nmpMapper.map(it) }
    private val vmpGroupToVmpGroupDto = { it: VmpGroup -> vmpGroupMapper.map(it) }

    @Operation(summary = "Get Samv2 version.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/v")
    fun getSamVersion() = mono { samV2Logic.getVersion()?.let { samVersionMapper.map(it) } }

    @Operation(summary = "Finding AMPs by label with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp")
    fun findPaginatedAmpsByLabel(
            @Parameter(description = "language") @RequestParam(required = false) language: String?,
            @Parameter(description = "label") @RequestParam(required = false) label: String?,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
            @Parameter(description = "An amp document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements: List<String>? = if (startKey == null) null else objectMapper.readValue<List<String>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java))
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)

        samV2Logic.findAmpsByLabel(language, label, paginationOffset).paginatedList<Amp, AmpDto>(ampToAmpDto, realLimit).let {
            it.copy(rows = addProductIdsToAmps(it.rows))
        }
    }

    @Operation(summary = "Finding VMPs by label with pagination.", description = "Returns a paginated list of VMPs by matching label. Matches occur per word")
    @GetMapping("/vmp")
    fun findPaginatedVmpsByLabel(
            @Parameter(description = "language") @RequestParam(required = false) language: String?,
            @Parameter(description = "label") @RequestParam(required = false) label: String?,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = if (startKey == null) null else objectMapper.readValue<List<String>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java))
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)

        samV2Logic.findVmpsByLabel(language, label, paginationOffset).paginatedList<Vmp, VmpDto>(vmpToVmpDto, realLimit)
    }

    @Operation(summary = "Finding VMPs by group with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmp/byGroupCode/{vmpgCode}")
    fun findPaginatedVmpsByGroupCode(
            @Parameter(description = "vmpgCode", required = true) @PathVariable vmpgCode: String,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        samV2Logic.findVmpsByGroupCode(vmpgCode, paginationOffset).paginatedList<Vmp, VmpDto>(vmpToVmpDto, realLimit)
    }

    @Operation(summary = "Finding VMPs by group with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmp/byVmpCode/{vmpCode}")
    fun findPaginatedVmpsByVmpCode(
            @Parameter(description = "vmpCode", required = true) @PathVariable vmpCode: String,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam("startKey") startKey: String?,
            @Parameter(description = "A vmp document ID", required = false) @RequestParam("startDocumentId") startDocumentId: String?,
            @Parameter(description = "Number of rows", required = false) @RequestParam("limit") limit: Int?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        samV2Logic.findVmpsByVmpCode(vmpCode, paginationOffset).paginatedList<Vmp, VmpDto>(vmpToVmpDto, realLimit)
    }

    @Operation(summary = "Finding NMPs by label with pagination.", description = "Returns a paginated list of NMPs by matching label. Matches occur per word")
    @GetMapping("/nmp")
    fun findPaginatedNmpsByLabel(
            @Parameter(description = "language") @RequestParam(required = false) language: String?,
            @Parameter(description = "label") @RequestParam(required = false) label: String?,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = if (startKey == null) null else objectMapper.readValue<List<String>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java))
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)

        samV2Logic.findNmpsByLabel(language, label, paginationOffset).paginatedList<Nmp, NmpDto>(nmpToNmpDto, realLimit).let {
            it.copy(rows = addProductIdsToNmps(it.rows))
        }
    }

    @Operation(summary = "Finding VMPs by group with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmp/byGroupId/{vmpgId}")
    fun findPaginatedVmpsByGroupId(
            @Parameter(description = "vmpgId", required = true) @PathVariable vmpgId: String,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit + 1)

        samV2Logic.findVmpsByGroupId(vmpgId, paginationOffset).paginatedList<Vmp, VmpDto>(vmpToVmpDto, realLimit)
    }

    @Operation(summary = "Finding AMPs by group with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byGroupCode/{vmpgCode}")
    fun findPaginatedAmpsByGroupCode(
            @Parameter(description = "vmpgCode", required = true) @PathVariable vmpgCode: String,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        samV2Logic.findAmpsByVmpGroupCode(vmpgCode, paginationOffset).paginatedList<Amp, AmpDto>(ampToAmpDto, realLimit).let {
            it.copy(rows = addProductIdsToAmps(it.rows))
        }
    }

    @Operation(summary = "Finding AMPs by group with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byGroupId/{vmpgId}")
    fun findPaginatedAmpsByGroupId(
            @Parameter(description = "vmpgCode", required = true) @PathVariable vmpgId: String,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit + 1)

        samV2Logic.findAmpsByVmpGroupId(vmpgId, paginationOffset).paginatedList<Amp, AmpDto>(ampToAmpDto, realLimit).let {
            it.copy(rows = addProductIdsToAmps(it.rows))
        }
    }

    @Operation(summary = "Finding AMPs by vmp code with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byVmpCode/{vmpCode}")
    fun findPaginatedAmpsByVmpCode(
            @Parameter(description = "vmpCode", required = true) @PathVariable vmpCode: String,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A amp document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        samV2Logic.findAmpsByVmpCode(vmpCode, paginationOffset).paginatedList<Amp, AmpDto>(ampToAmpDto, realLimit).let {
            it.copy(rows = addProductIdsToAmps(it.rows))
        }
    }

    @Operation(summary = "Finding AMPs by atc code with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmp/byAtc/{atcCode}")
    fun findPaginatedAmpsByAtc(
            @Parameter(description = "atcCode", required = true) @PathVariable atcCode: String,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam("startKey") startKey: String?,
            @Parameter(description = "A amp document ID", required = false) @RequestParam("startDocumentId") startDocumentId: String?,
            @Parameter(description = "Number of rows", required = false) @RequestParam("limit") limit: Int?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        samV2Logic.findAmpsByAtcCode(atcCode, paginationOffset).paginatedList<Amp, AmpDto>(ampToAmpDto, realLimit).let {
            it.copy(rows = addProductIdsToAmps(it.rows))
        }
    }

    @Operation(summary = "Finding AMPs by vmp id with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byVmpId/{vmpId}")
    fun findPaginatedAmpsByVmpId(
            @Parameter(description = "vmpgCode", required = true) @PathVariable vmpId: String,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A amp document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        samV2Logic.findAmpsByVmpId(vmpId, paginationOffset).paginatedList<Amp, AmpDto>(ampToAmpDto, realLimit).let {
            it.copy(rows = addProductIdsToAmps(it.rows))
        }
    }

    @Operation(summary = "Finding AMPs by dmpp code", description = "Returns a list of amps matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byDmppCode/{dmppCode}")
    fun findAmpsByDmppCode(
            @Parameter(description = "dmppCode", required = true) @PathVariable dmppCode: String
    ) = addProductIdsToAmps(samV2Logic.findAmpsByDmppCode(dmppCode).filterIsInstance<ViewRowWithDoc<String, String, Amp>>().map { ampMapper.map(it.doc) }).injectReactorContext()


    @Operation(summary = "Finding VMP groups by language label with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmpgroup")
    fun findPaginatedVmpGroupsByLabel(
            @Parameter(description = "language") @RequestParam(required = false) language: String?,
            @Parameter(description = "label") @RequestParam(required = false) label: String?,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A vmpgroup document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = if (startKey == null) null else objectMapper.readValue<List<String>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java))
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)

        samV2Logic.findVmpGroupsByLabel(language, label, paginationOffset).paginatedList<VmpGroup, VmpGroupDto>(vmpGroupToVmpGroupDto, realLimit).let {
            it.copy(rows = addProductIdsToVmpGroups(it.rows))
        }
    }

    @Operation(summary = "Finding VMP groups by cmpgCode with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmpgroup/byGroupCode/{vmpgCode}")
    fun findPaginatedVmpGroupsByVmpGroupCode(
            @Parameter(description = "vmpgCode", required = true) @PathVariable vmpgCode: String,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A vmpgroup document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        samV2Logic.findVmpGroupsByVmpGroupCode(vmpgCode, paginationOffset).paginatedList<VmpGroup, VmpGroupDto>(vmpGroupToVmpGroupDto, realLimit).let {
            it.copy(rows = addProductIdsToVmpGroups(it.rows))
        }
    }

    @Operation(summary = "Finding VMPs by group.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @PostMapping("/vmp/byVmpCodes")
    fun listVmpsByVmpCodes(
            @RequestBody vmpCodes: ListOfIdsDto
    ) = samV2Logic.listVmpsByVmpCodes(vmpCodes.ids).map { vmpToVmpDto(it) }.injectReactorContext()

    @Operation(summary = "Finding VMPs by group.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @PostMapping("/vmp/byGroupIds")
    fun listVmpsByGroupIds(
            @RequestBody vmpgIds: ListOfIdsDto
    ) = samV2Logic.listVmpsByGroupIds(vmpgIds.ids).map { vmpToVmpDto(it) }.injectReactorContext()

    @Operation(summary = "Finding AMPs by group.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @PostMapping("/amp/byGroupCodes")
    fun listAmpsByGroupCodes(
            @RequestBody vmpgCodes: ListOfIdsDto
    ) = samV2Logic.listAmpsByGroupCodes(vmpgCodes.ids).map { ampToAmpDto(it) }.let { addProductIdsToAmps(it) }.injectReactorContext()

    @Operation(summary = "Finding AMPs by dmpp code", description = "Returns a list of amps matched with given input. If several types are provided, paginantion is not supported")
    @PostMapping("/amp/byDmppCodes")
    fun listAmpsByDmppCodes(
            @RequestBody dmppCodes: ListOfIdsDto
    ) = samV2Logic.listAmpsByDmppCodes(dmppCodes.ids).map { ampToAmpDto(it) }.let { addProductIdsToAmps(it) }.injectReactorContext()

    @Operation(summary = "Finding AMPs by group.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @PostMapping("/amp/byGroupIds")
    fun listAmpsByGroupIds(
            @RequestBody groupIds: ListOfIdsDto
    ) = samV2Logic.listAmpsByGroupIds(groupIds.ids).map { ampToAmpDto(it) }.let { addProductIdsToAmps(it) }.injectReactorContext()

    @Operation(summary = "Finding AMPs by vmp code.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @PostMapping("/amp/byVmpCodes")
    fun listAmpsByVmpCodes(
            @RequestBody vmpgCodes: ListOfIdsDto
    ) = samV2Logic.listAmpsByVmpCodes(vmpgCodes.ids).map { ampToAmpDto(it) }.let { addProductIdsToAmps(it) }.injectReactorContext()

    @Operation(summary = "Finding AMPs by vmp id.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @PostMapping("/amp/byVmpIds")
    fun listAmpsByVmpIds(
            @RequestBody vmpIds: ListOfIdsDto
    ) = samV2Logic.listAmpsByVmpIds(vmpIds.ids).map { ampToAmpDto(it) }.let { addProductIdsToAmps(it) }.injectReactorContext()

    @Operation(summary = "Finding AMPs by group.", description = "Returns a list of group codes matched with given input. If several types are provided, paginantion is not supported")
    @PostMapping("/vmpgroup/byGroupCodes")
    fun listVmpGroupsByVmpGroupCodes(
            @RequestBody vmpgCodes: ListOfIdsDto
    ) = samV2Logic.listVmpGroupsByVmpGroupCodes(vmpgCodes.ids).map { vmpGroupToVmpGroupDto(it) }.let { addProductIdsToVmpGroups(it) }.injectReactorContext()

    @Operation(summary = "Finding NMPs by cnk id.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @PostMapping("/nmp/byCnks")
    fun listNmpsByCnks(
            @RequestBody cnks: ListOfIdsDto
    ) = samV2Logic.listNmpsByCnks(cnks.ids).map { nmpToNmpDto(it) }.let { addProductIdsToNmps(it) }.injectReactorContext()

    @Operation(summary = "List all substances.")
    @GetMapping("/substance")
    fun listSubstances(): Flux<SubstanceDto> {
        return samV2Logic.listSubstances().map { substanceMapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "List all pharmaceutical forms.")
    @GetMapping("/pharmaform")
    fun listPharmaceuticalForms(): Flux<PharmaceuticalFormDto> {
        return samV2Logic.listPharmaceuticalForms().map { pharmaceuticalFormMapper.map(it) }.injectReactorContext()
    }

    private suspend fun addProductIdsToVmpGroups(vmpGroups: Collection<VmpGroupDto>): List<VmpGroupDto> {
        val productIds = samV2Logic.listProductIds(vmpGroups.map { "SAMID:${it.id}" }).toList()
        return vmpGroups.mapIndexed { index, g ->
            g.copy(productId = if (index < productIds.size && productIds[index].id == "SAMID:${g.id}") productIds[index].productId else productIds.find { it.id == "SAMID:${g.id}"}?.productId)
        }
    }

    private suspend fun addProductIdsToAmps(amps: Collection<AmpDto>): List<AmpDto> {
        val dmpps = amps.flatMap { it.ampps.flatMap { it.dmpps } }
        val productIds = samV2Logic.listProductIds(dmpps.map { "SAMID:${it.id}" }).toList()
        return amps.map { if (it.ampps.any { it.dmpps.isNotEmpty() }) it.copy(ampps = it.ampps.map { it.copy(dmpps = it.dmpps.map {
            it.copy(productId = productIds.find { it.id == "SAMID:${it.id}"}?.productId)
        }) }) else it }
    }

    private suspend fun addProductIdsToNmps(nmps: Collection<NmpDto>) : List<NmpDto> {
        val productIds = samV2Logic.listProductIds(nmps.map { "SAMID:${it.id}" }).toList()
        return nmps.mapIndexed { index, n ->
            n.copy(productId = if (index < productIds.size && productIds[index].id == "SAMID:${n.id}") productIds[index].productId else productIds.find { it.id == "SAMID:${n.id}"}?.productId)
        }
    }

    private fun addProductIdsToAmps(amps: Flow<AmpDto>) = flow {
        val acc = ArrayDeque<AmpDto>(20)
        amps.collect {
            acc.add(it)
            if (acc.size == 20) {
                addProductIdsToAmps(acc).forEach { emit(it) }
                acc.clear()
            }
        }
        addProductIdsToAmps(acc).forEach { emit(it) }
    }

    private fun addProductIdsToVmpGroups(vmpGroups: Flow<VmpGroupDto>) = flow {
        val acc = ArrayDeque<VmpGroupDto>(20)
        vmpGroups.collect {
            acc.add(it)
            if (acc.size == 20) {
                addProductIdsToVmpGroups(acc).forEach { emit(it) }
                acc.clear()
            }
        }
        addProductIdsToVmpGroups(acc).forEach { emit(it) }
    }

    private fun addProductIdsToNmps(nmps: Flow<NmpDto>) = flow {
        val acc = ArrayDeque<NmpDto>(20)
        nmps.collect {
            acc.add(it)
            if (acc.size == 20) {
                addProductIdsToNmps(acc).forEach { emit(it) }
                acc.clear()
            }
        }
        addProductIdsToNmps(acc).forEach { emit(it) }
    }


}
