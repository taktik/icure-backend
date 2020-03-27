package org.taktik.icure.services.external.rest.v1.controllers.be

import com.google.gson.Gson
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.springframework.web.bind.annotation.*
import org.taktik.icure.asynclogic.samv2.SamV2Logic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Vmp
import org.taktik.icure.entities.samv2.VmpGroup
import org.taktik.icure.services.external.rest.v1.dto.be.samv2.AmpDto
import org.taktik.icure.services.external.rest.v1.dto.be.samv2.VmpDto
import org.taktik.icure.services.external.rest.v1.dto.be.samv2.VmpGroupDto
import org.taktik.icure.utils.ResponseUtils
import org.taktik.icure.utils.injectReactorContext
import org.taktik.icure.utils.paginatedList
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response

@RestController
@RequestMapping("/rest/v1/be_samv2")
@Tag(name = "besamv2")
class SamV2Controller(val mapper: MapperFacade,
                      val samV2Logic: SamV2Logic) {
    private val DEFAULT_LIMIT = 1000

    @Operation(summary = "Finding AMPs by label with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp")
    fun findPaginatedAmpsByLabel(
            @Parameter(description = "language") @RequestParam(required = false) language: String?,
            @Parameter(description = "label") @RequestParam(required = false) label: String?,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
            @Parameter(description = "An amp document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements: List<String>? = if (startKey == null) null else Gson().fromJson<List<String>>(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)

        samV2Logic.findAmpsByLabel(language, label, paginationOffset).paginatedList<Amp, AmpDto>(mapper, realLimit)
    }

    @Operation(summary = "Finding VMPs by label with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmp")
    fun findPaginatedVmpsByLabel(
            @Parameter(description = "language") @RequestParam(required = false) language: String?,
            @Parameter(description = "label") @RequestParam(required = false) label: String?,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = if (startKey == null) null else Gson().fromJson<List<String>>(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)

        samV2Logic.findVmpsByLabel(language, label, paginationOffset).paginatedList<Vmp, VmpDto>(mapper, realLimit)
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

        samV2Logic.findVmpsByGroupCode(vmpgCode, paginationOffset).paginatedList<Vmp, VmpDto>(mapper, realLimit)
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

        samV2Logic.findVmpsByGroupId(vmpgId, paginationOffset).paginatedList<Vmp, VmpDto>(mapper, realLimit)
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

        samV2Logic.findAmpsByVmpGroupCode(vmpgCode, paginationOffset).paginatedList<Amp, AmpDto>(mapper, realLimit)
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

        samV2Logic.findAmpsByVmpGroupId(vmpgId, paginationOffset).paginatedList<Amp, AmpDto>(mapper, realLimit)
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

        samV2Logic.findAmpsByVmpCode(vmpCode, paginationOffset).paginatedList<Amp, AmpDto>(mapper, realLimit)
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

        samV2Logic.findAmpsByVmpId(vmpId, paginationOffset).paginatedList<Amp, AmpDto>(mapper, realLimit)
    }

    @Operation(summary = "Finding AMPs by dmpp code", description = "Returns a list of amps matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byDmppCode/{dmppCode}")
    fun findAmpsByDmppCode(
            @Parameter(description = "dmppCode", required = true) @PathVariable dmppCode: String
    ) = samV2Logic.findAmpsByDmppCode(dmppCode).map { mapper.map(it, AmpDto::class.java) }.injectReactorContext()


    @Operation(summary = "Finding codes by code, type and version with pagination.", description = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmpgroup")
    fun findPaginatedVmpGroupsByLabel(
            @Parameter(description = "language") @RequestParam(required = false) language: String?,
            @Parameter(description = "label") @RequestParam(required = false) label: String?,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A vmpgroup document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, Array<String>::class.java).toList()
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)

        samV2Logic.findVmpGroupsByLabel(language, label, paginationOffset).paginatedList<VmpGroup, VmpGroupDto>(mapper, realLimit)
    }
}
