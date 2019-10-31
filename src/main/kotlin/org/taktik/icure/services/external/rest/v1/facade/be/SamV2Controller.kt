package org.taktik.icure.services.external.rest.v1.facade.be

import com.google.gson.Gson
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.metadata.TypeBuilder
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import org.taktik.icure.be.samv2.logic.SamV2Logic
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Vmp
import org.taktik.icure.entities.samv2.VmpGroup
import org.taktik.icure.services.external.rest.v1.dto.be.samv2.*
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade
import org.taktik.icure.utils.ResponseUtils
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.Response

@RestController
@RequestMapping("/be_samv2")
@Api(tags = ["be_samv2"])
class SamV2Controller(val mapper: MapperFacade,
                      val samV2Logic: SamV2Logic) : OpenApiFacade {

    @ApiOperation(nickname = "findPaginatedAmpsByLabel", value = "Finding AMPs by label with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp")
    fun findPaginatedAmpsByLabel(
            @ApiParam(value = "language") @RequestParam(required = false) language: String?,
            @ApiParam(value = "label") @RequestParam(required = false) label: String?,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "An amp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto> {

        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)

        val ampsList = samV2Logic.findAmpsByLabel(language, label, paginationOffset)

        if (ampsList.rows == null) {
            ampsList.rows = ArrayList()
        }

        val ampDtosPaginatedList = org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>()
        mapper.map<PaginatedList<Amp>, org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>>(
                ampsList,
                ampDtosPaginatedList,
                object : TypeBuilder<PaginatedList<Amp>>() {}.build(),
                object : TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>>() {}.build()
        )
        return ampDtosPaginatedList
    }

    @ApiOperation(nickname = "findPaginatedVmpsByLabel", value = "Finding VMPs by label with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmp")
    fun findPaginatedVmpsByLabel(
            @ApiParam(value = "language") @RequestParam(required = false) language: String?,
            @ApiParam(value = "label") @RequestParam(required = false) label: String?,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto> {

        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)

        val vmpsList = samV2Logic.findVmpsByLabel(language, label, paginationOffset)

        if (vmpsList.rows == null) {
            vmpsList.rows = ArrayList()
        }

        val vmpDtosPaginatedList = org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto>()
        mapper.map<PaginatedList<Vmp>, org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto>>(
                vmpsList,
                vmpDtosPaginatedList,
                object : TypeBuilder<PaginatedList<Vmp>>() {}.build(),
                object : TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto>>() {}.build()
        )
        return vmpDtosPaginatedList
    }

    @ApiOperation(nickname = "findPaginatedVmpsByGroupCode", value = "Finding VMPs by group with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmp/byGroupCode/{vmpgCode}")
    fun findPaginatedVmpsByGroupCode(
            @ApiParam(value = "vmpgCode", required = true) @PathVariable vmpgCode: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto> {

        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)

        val vmpsList = samV2Logic.findVmpsByGroupCode(vmpgCode, paginationOffset)

        if (vmpsList.rows == null) {
            vmpsList.rows = ArrayList()
        }

        val vmpDtosPaginatedList = org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto>()
        mapper.map<PaginatedList<Vmp>, org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto>>(
                vmpsList,
                vmpDtosPaginatedList,
                object : TypeBuilder<PaginatedList<Vmp>>() {}.build(),
                object : TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto>>() {}.build()
        )
        return vmpDtosPaginatedList
    }

    @ApiOperation(nickname = "findPaginatedVmpsByGroupId", value = "Finding VMPs by group with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmp/byGroupId/{vmpgId}")
    fun findPaginatedVmpsByGroupId(
            @ApiParam(value = "vmpgId", required = true) @PathVariable vmpgId: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto> {

        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)

        val vmpsList = samV2Logic.findVmpsByGroupId(vmpgId, paginationOffset)

        if (vmpsList.rows == null) {
            vmpsList.rows = ArrayList()
        }

        val vmpDtosPaginatedList = org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto>()
        mapper.map<PaginatedList<Vmp>, org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto>>(
                vmpsList,
                vmpDtosPaginatedList,
                object : TypeBuilder<PaginatedList<Vmp>>() {}.build(),
                object : TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpDto>>() {}.build()
        )
        return vmpDtosPaginatedList
    }

    @ApiOperation(nickname = "findPaginatedAmpsByGroupCode", value = "Finding AMPs by group with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byGroupCode/{vmpgCode}")
    fun findPaginatedAmpsByGroupCode(
            @ApiParam(value = "vmpgCode", required = true) @PathVariable vmpgCode: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto> {

        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)

        val ampsList = samV2Logic.findAmpsByVmpGroupCode(vmpgCode, paginationOffset)

        if (ampsList.rows == null) {
            ampsList.rows = ArrayList()
        }

        val ampDtosPaginatedList = org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>()
        mapper.map<PaginatedList<Amp>, org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>>(
                ampsList,
                ampDtosPaginatedList,
                object : TypeBuilder<PaginatedList<Amp>>() {}.build(),
                object : TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>>() {}.build()
        )
       return ampDtosPaginatedList
    }

    @ApiOperation(nickname = "findPaginatedAmpsByGroupId", value = "Finding AMPs by group with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byGroupId/{vmpgId}")
    fun findPaginatedAmpsByGroupId(
            @ApiParam(value = "vmpgCode", required = true) @PathVariable vmpgId: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A vmp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto> {

        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)

        val ampsList = samV2Logic.findAmpsByVmpGroupId(vmpgId, paginationOffset)

        if (ampsList.rows == null) {
            ampsList.rows = ArrayList()
        }

        val ampDtosPaginatedList = org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>()
        mapper.map<PaginatedList<Amp>, org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>>(
                ampsList,
                ampDtosPaginatedList,
                object : TypeBuilder<PaginatedList<Amp>>() {}.build(),
                object : TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>>() {}.build()
        )
        return ampDtosPaginatedList
    }

    @ApiOperation(nickname = "findPaginatedAmpsByVmpCode", value = "Finding AMPs by vmp code with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byVmpCode/{vmpCode}")
    fun findPaginatedAmpsByVmpCode(
            @ApiParam(value = "vmpCode", required = true) @PathVariable vmpCode: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A amp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto> {

        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)

        val ampsList = samV2Logic.findAmpsByVmpCode(vmpCode, paginationOffset)

        if (ampsList.rows == null) {
            ampsList.rows = ArrayList()
        }

        val ampDtosPaginatedList = org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>()
        mapper.map<PaginatedList<Amp>, org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>>(
                ampsList,
                ampDtosPaginatedList,
                object : TypeBuilder<PaginatedList<Amp>>() {}.build(),
                object : TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>>() {}.build()
        )
        return ampDtosPaginatedList
    }

    @ApiOperation(nickname = "findPaginatedAmpsByVmpId", value = "Finding AMPs by vmp id with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/amp/byVmpId/{vmpId}")
    fun findPaginatedAmpsByVmpId(
            @ApiParam(value = "vmpgCode", required = true) @PathVariable vmpId: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A amp document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto> {

        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)

        val ampsList = samV2Logic.findAmpsByVmpId(vmpId, paginationOffset)

        if (ampsList.rows == null) {
            ampsList.rows = ArrayList()
        }

        val ampDtosPaginatedList = org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>()
        mapper.map<PaginatedList<Amp>, org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>>(
                ampsList,
                ampDtosPaginatedList,
                object : TypeBuilder<PaginatedList<Amp>>() {}.build(),
                object : TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<AmpDto>>() {}.build()
        )
        return ampDtosPaginatedList
    }

    @ApiOperation(nickname = "findPaginatedVmpGroupsByLabel", value = "Finding codes by code, type and version with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/vmpgroup")
    fun findPaginatedVmpGroupsByLabel(
            @ApiParam(value = "language") @RequestParam(required = false) language: String?,
            @ApiParam(value = "label") @RequestParam(required = false) label: String?,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A vmpgroup document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpGroupDto> {

        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)

        val vmpGroupsList = samV2Logic.findVmpGroupsByLabel(language, label, paginationOffset)

        if (vmpGroupsList.rows == null) {
            vmpGroupsList.rows = ArrayList()
        }

        val vmpGroupDtosPaginatedList = org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpGroupDto>()
        mapper.map<PaginatedList<VmpGroup>, org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpGroupDto>>(
                vmpGroupsList,
                vmpGroupDtosPaginatedList,
                object : TypeBuilder<PaginatedList<VmpGroup>>() {}.build(),
                object : TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpGroupDto>>() {}.build()
        )
        return vmpGroupDtosPaginatedList
    }
}
