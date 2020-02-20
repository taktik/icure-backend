package org.taktik.icure.services.external.rest.v1.controllers.be

import com.google.gson.Gson
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import kotlinx.coroutines.flow.map
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
@Api(tags = ["be_samv2"])
class SamV2Controller(val mapper: MapperFacade,
                      val samV2Logic: SamV2Logic) {
    private val DEFAULT_LIMIT = 1000

    @ApiOperation(nickname = "findPaginatedAmpsByLabel", value = "Finding AMPs by label with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp")
    suspend fun findPaginatedAmpsByLabel(
            @ApiParam(value = "language") @RequestParam(required = false) language: String?,
            @ApiParam(value = "label") @RequestParam(required = false) label: String?,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "An amp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto> {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements: List<String>? = if (startKey == null) null else Gson().fromJson<List<String>>(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)

        return samV2Logic.findAmpsByLabel(language, label, paginationOffset).paginatedList<Amp, AmpDto>(mapper, realLimit)

    }

    @ApiOperation(nickname = "findPaginatedVmpsByLabel", value = "Finding VMPs by label with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmp")
    suspend fun findPaginatedVmpsByLabel(
            @ApiParam(value = "language") @RequestParam(required = false) language: String?,
            @ApiParam(value = "label") @RequestParam(required = false) label: String?,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto> {

        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = if (startKey == null) null else Gson().fromJson<List<String>>(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)

        return samV2Logic.findVmpsByLabel(language, label, paginationOffset).paginatedList<Vmp, VmpDto>(mapper, realLimit)
    }

    @ApiOperation(nickname = "findPaginatedVmpsByGroupCode", value = "Finding VMPs by group with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmp/byGroupCode/{vmpgCode}")
    suspend fun findPaginatedVmpsByGroupCode(
            @ApiParam(value = "vmpgCode", required = true) @PathVariable vmpgCode: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto> {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        return samV2Logic.findVmpsByGroupCode(vmpgCode, paginationOffset).paginatedList<Vmp, VmpDto>(mapper, realLimit)

    }

    @ApiOperation(nickname = "findPaginatedVmpsByGroupId", value = "Finding VMPs by group with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmp/byGroupId/{vmpgId}")
    suspend fun findPaginatedVmpsByGroupId(
            @ApiParam(value = "vmpgId", required = true) @PathVariable vmpgId: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto> {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        return samV2Logic.findVmpsByGroupId(vmpgId, paginationOffset).paginatedList<Vmp, VmpDto>(mapper, realLimit)
    }

    @ApiOperation(nickname = "findPaginatedAmpsByGroupCode", value = "Finding AMPs by group with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byGroupCode/{vmpgCode}")
    suspend fun findPaginatedAmpsByGroupCode(
            @ApiParam(value = "vmpgCode", required = true) @PathVariable vmpgCode: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto> {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        return samV2Logic.findAmpsByVmpGroupCode(vmpgCode, paginationOffset).paginatedList<Amp, AmpDto>(mapper, realLimit)

    }

    @ApiOperation(nickname = "findPaginatedAmpsByGroupId", value = "Finding AMPs by group with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byGroupId/{vmpgId}")
    suspend fun findPaginatedAmpsByGroupId(
            @ApiParam(value = "vmpgCode", required = true) @PathVariable vmpgId: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto> {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        return samV2Logic.findAmpsByVmpGroupId(vmpgId, paginationOffset).paginatedList<Amp, AmpDto>(mapper, realLimit)
    }

    @ApiOperation(nickname = "findPaginatedAmpsByVmpCode", value = "Finding AMPs by vmp code with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byVmpCode/{vmpCode}")
    suspend fun findPaginatedAmpsByVmpCode(
            @ApiParam(value = "vmpCode", required = true) @PathVariable vmpCode: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A amp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto> {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        return samV2Logic.findAmpsByVmpCode(vmpCode, paginationOffset).paginatedList<Amp, AmpDto>(mapper, realLimit)

    }

    @ApiOperation(nickname = "findPaginatedAmpsByVmpId", value = "Finding AMPs by vmp id with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byVmpId/{vmpId}")
    suspend fun findPaginatedAmpsByVmpId(
            @ApiParam(value = "vmpgCode", required = true) @PathVariable vmpId: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A amp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto> {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        return samV2Logic.findAmpsByVmpId(vmpId, paginationOffset).paginatedList<Amp, AmpDto>(mapper, realLimit)

    }

    @ApiOperation(value = "Finding AMPs by dmpp code", responseContainer = "Array", response = AmpDto::class, httpMethod = "GET", notes = "Returns a list of amps matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byDmppCode/{dmppCode}")
    fun findAmpsByDmppCode(
            @ApiParam(value = "dmppCode", required = true) @PathVariable dmppCode: String
    ) = samV2Logic.findAmpsByDmppCode(dmppCode).map { mapper.map(it, AmpDto::class.java) }.injectReactorContext()


    @ApiOperation(nickname = "findPaginatedVmpGroupsByLabel", value = "Finding codes by code, type and version with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmpgroup")
    suspend fun findPaginatedVmpGroupsByLabel(
            @ApiParam(value = "language") @RequestParam(required = false) language: String?,
            @ApiParam(value = "label") @RequestParam(required = false) label: String?,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A vmpgroup document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpGroupDto> {

        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, Array<String>::class.java).toList()
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)

        return samV2Logic.findVmpGroupsByLabel(language, label, paginationOffset).paginatedList<VmpGroup, VmpGroupDto>(mapper, realLimit)

    }
}
