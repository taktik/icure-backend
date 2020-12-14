package org.taktik.icure.services.external.rest.v1.facade.be

import com.google.gson.Gson
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.metadata.TypeBuilder
import org.springframework.stereotype.Component
import org.taktik.icure.be.samv2.logic.SamV2Logic
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Nmp
import org.taktik.icure.entities.samv2.Vmp
import org.taktik.icure.entities.samv2.VmpGroup
import org.taktik.icure.samv2.SamVersion
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.be.samv2.*
import org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed.PharmaceuticalFormDto
import org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed.SubstanceDto
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade
import org.taktik.icure.utils.ResponseUtils
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.Response

@Component
@Path("/be_samv2")
@Api(tags = ["be_samv2"])
@Consumes("application/json")
@Produces("application/json")
class SamV2Facade(val mapper: MapperFacade, val samV2Logic: SamV2Logic) : OpenApiFacade {

    @ApiOperation(value = "Get Samv2 version.", response = SamVersion::class, httpMethod = "GET")
    @GET
    @Path("/v")
    fun getVersion(): Response {
        return ResponseUtils.ok(samV2Logic.getVersion())
    }

    @ApiOperation(value = "Finding AMPs by label with pagination.", response = AmpPaginatedList::class, httpMethod = "GET")
    @GET
    @Path("/amp")
    fun findPaginatedAmpsByLabel(
            @ApiParam(value = "language", required = false) @QueryParam("language") language: String,
            @ApiParam(value = "label", required = false) @QueryParam("label") label: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @QueryParam("startKey") startKey: String?,
            @ApiParam(value = "An amp document ID", required = false) @QueryParam("startDocumentId") startDocumentId: String?,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") limit: Int?): Response {

        val response: Response

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
        addProductIdsToAmps(ampDtosPaginatedList.rows)
        response = ResponseUtils.ok(ampDtosPaginatedList)

        return response
    }

    @ApiOperation(value = "Finding NMPs by label with pagination.", response = AmpPaginatedList::class, httpMethod = "GET")
    @GET
    @Path("/nmp")
    fun findPaginatedNmpsByLabel(
            @ApiParam(value = "language", required = false) @QueryParam("language") language: String,
            @ApiParam(value = "label", required = false) @QueryParam("label") label: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @QueryParam("startKey") startKey: String?,
            @ApiParam(value = "A nmp document ID", required = false) @QueryParam("startDocumentId") startDocumentId: String?,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") limit: Int?): Response {

        val response: Response

        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)

        val nmpsList = samV2Logic.findNmpsByLabel(language, label, paginationOffset)

        if (nmpsList.rows == null) {
            nmpsList.rows = ArrayList()
        }

        val nmpDtosPaginatedList = org.taktik.icure.services.external.rest.v1.dto.PaginatedList<NmpDto>()
        mapper.map<PaginatedList<Nmp>, org.taktik.icure.services.external.rest.v1.dto.PaginatedList<NmpDto>>(
                nmpsList,
                nmpDtosPaginatedList,
                object : TypeBuilder<PaginatedList<Nmp>>() {}.build(),
                object : TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<NmpDto>>() {}.build()
        )
        addProductIdsToNmps(nmpDtosPaginatedList.rows)
        response = ResponseUtils.ok(nmpDtosPaginatedList)

        return response
    }

    @ApiOperation(value = "Finding VMPs by label with pagination.", response = VmpPaginatedList::class, httpMethod = "GET", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GET
    @Path("/vmp")
    fun findPaginatedVmpsByLabel(
            @ApiParam(value = "language", required = false) @QueryParam("language") language: String,
            @ApiParam(value = "label", required = false) @QueryParam("label") label: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @QueryParam("startKey") startKey: String?,
            @ApiParam(value = "A vmp document ID", required = false) @QueryParam("startDocumentId") startDocumentId: String?,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") limit: Int?): Response {

        val response: Response

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
        response = ResponseUtils.ok(vmpDtosPaginatedList)

        return response
    }

    @ApiOperation(value = "Finding VMPs by group with pagination.", response = VmpPaginatedList::class, httpMethod = "GET", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GET
    @Path("/vmp/byGroupCode/{vmpgCode}")
    fun findPaginatedVmpsByGroupCode(
            @ApiParam(value = "vmpgCode", required = true) @PathParam("vmpgCode") vmpgCode: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @QueryParam("startKey") startKey: String?,
            @ApiParam(value = "A vmp document ID", required = false) @QueryParam("startDocumentId") startDocumentId: String?,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") limit: Int?): Response {

        val response: Response

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
        response = ResponseUtils.ok(vmpDtosPaginatedList)

        return response
    }

    @ApiOperation(value = "Finding VMPs by group with pagination.", response = VmpPaginatedList::class, httpMethod = "GET", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GET
    @Path("/vmp/byVmpCode/{vmpCode}")
    fun findPaginatedVmpsByVmpCode(
            @ApiParam(value = "vmpCode", required = true) @PathParam("vmpCode") vmpCode: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @QueryParam("startKey") startKey: String?,
            @ApiParam(value = "A vmp document ID", required = false) @QueryParam("startDocumentId") startDocumentId: String?,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") limit: Int?): Response {

        val response: Response

        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)

        val vmpsList = samV2Logic.findVmpsByVmpCode(vmpCode, paginationOffset)

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
        response = ResponseUtils.ok(vmpDtosPaginatedList)

        return response
    }

    @ApiOperation(value = "Finding VMPs by group with pagination.", response = VmpPaginatedList::class, httpMethod = "GET", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GET
    @Path("/vmp/byGroupId/{vmpgId}")
    fun findPaginatedVmpsByGroupId(
            @ApiParam(value = "vmpgId", required = true) @PathParam("vmpgId") vmpgId: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @QueryParam("startKey") startKey: String?,
            @ApiParam(value = "A vmp document ID", required = false) @QueryParam("startDocumentId") startDocumentId: String?,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") limit: Int?): Response {

        val response: Response

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
        response = ResponseUtils.ok(vmpDtosPaginatedList)

        return response
    }

    @ApiOperation(value = "Finding AMPs by group with pagination.", response = AmpPaginatedList::class, httpMethod = "GET", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GET
    @Path("/amp/byGroupCode/{vmpgCode}")
    fun findPaginatedAmpsByGroupCode(
            @ApiParam(value = "vmpgCode", required = true) @PathParam("vmpgCode") vmpgCode: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @QueryParam("startKey") startKey: String?,
            @ApiParam(value = "A vmp document ID", required = false) @QueryParam("startDocumentId") startDocumentId: String?,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") limit: Int?): Response {

        val response: Response

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
        addProductIdsToAmps(ampDtosPaginatedList.rows)
        response = ResponseUtils.ok(ampDtosPaginatedList)

        return response
    }

    @ApiOperation(value = "Finding AMPs by dmpp code", responseContainer = "Array", response = AmpDto::class, httpMethod = "GET", notes = "Returns a list of amps matched with given input. If several types are provided, paginantion is not supported")
    @GET
    @Path("/amp/byDmppCode/{dmppCode}")
    fun findAmpsByDmppCode(
            @ApiParam(value = "dmppCode", required = true) @PathParam("dmppCode") dmppCode: String
    ): Response = ResponseUtils.ok(samV2Logic.findAmpsByDmppCode(dmppCode).map { mapper.map(it, AmpDto::class.java) }.also { addProductIdsToAmps(it) })

    @ApiOperation(value = "Finding AMPs by group with pagination.", response = AmpPaginatedList::class, httpMethod = "GET", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GET
    @Path("/amp/byGroupId/{vmpgId}")
    fun findPaginatedAmpsByGroupId(
            @ApiParam(value = "vmpgCode", required = true) @PathParam("vmpgId") vmpgId: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @QueryParam("startKey") startKey: String?,
            @ApiParam(value = "A vmp document ID", required = false) @QueryParam("startDocumentId") startDocumentId: String?,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") limit: Int?): Response {

        val response: Response

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
        addProductIdsToAmps(ampDtosPaginatedList.rows)
        response = ResponseUtils.ok(ampDtosPaginatedList)

        return response
    }

    @ApiOperation(value = "Finding AMPs by vmp code with pagination.", response = AmpPaginatedList::class, httpMethod = "GET", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GET
    @Path("/amp/byVmpCode/{vmpCode}")
    fun findPaginatedAmpsByVmpCode(
            @ApiParam(value = "vmpCode", required = true) @PathParam("vmpCode") vmpCode: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @QueryParam("startKey") startKey: String?,
            @ApiParam(value = "A amp document ID", required = false) @QueryParam("startDocumentId") startDocumentId: String?,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") limit: Int?): Response {

        val response: Response

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
        addProductIdsToAmps(ampDtosPaginatedList.rows)
        response = ResponseUtils.ok(ampDtosPaginatedList)

        return response
    }

    @ApiOperation(value = "Finding AMPs by atc code with pagination.", response = AmpPaginatedList::class, httpMethod = "GET", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GET
    @Path("/amp/byAtc/{atcCode}")
    fun findPaginatedAmpsByAtc(
            @ApiParam(value = "atcCode", required = true) @PathParam("atcCode") atcCode: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @QueryParam("startKey") startKey: String?,
            @ApiParam(value = "A amp document ID", required = false) @QueryParam("startDocumentId") startDocumentId: String?,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") limit: Int?): Response {

        val response: Response

        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)

        val ampsList = samV2Logic.findAmpsByAtcCode(atcCode, paginationOffset)

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
        addProductIdsToAmps(ampDtosPaginatedList.rows)
        response = ResponseUtils.ok(ampDtosPaginatedList)

        return response
    }

    @ApiOperation(value = "Finding AMPs by vmp id with pagination.", response = AmpPaginatedList::class, httpMethod = "GET", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GET
    @Path("/amp/byVmpId/{vmpId}")
    fun findPaginatedAmpsByVmpId(
            @ApiParam(value = "vmpgCode", required = true) @PathParam("vmpId") vmpId: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @QueryParam("startKey") startKey: String?,
            @ApiParam(value = "A amp document ID", required = false) @QueryParam("startDocumentId") startDocumentId: String?,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") limit: Int?): Response {

        val response: Response

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
        addProductIdsToAmps(ampDtosPaginatedList.rows)
        response = ResponseUtils.ok(ampDtosPaginatedList)

        return response
    }

    @ApiOperation(value = "Finding AMPs by group with pagination.", response = VmpGroupPaginatedList::class, httpMethod = "GET", notes = "Returns a list of group codes matched with given input. If several types are provided, paginantion is not supported")
    @GET
    @Path("/vmpgroup/byGroupCode/{vmpgCode}")
    fun findPaginatedVmpGroupsByVmpGroupCode(
            @ApiParam(value = "vmpgCode", required = true) @PathParam("vmpgCode") vmpgCode: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @QueryParam("startKey") startKey: String?,
            @ApiParam(value = "A vmpgroup document ID", required = false) @QueryParam("startDocumentId") startDocumentId: String?,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") limit: Int?): Response {

        val response: Response

        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)

        val vmpGroupsList = samV2Logic.findVmpGroupsByVmpGroupCode(vmpgCode, paginationOffset)

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
        addProductIdsToVmpGroups(vmpGroupDtosPaginatedList.rows)
        response = ResponseUtils.ok(vmpGroupDtosPaginatedList)

        return response
    }

    @ApiOperation(value = "Finding VMPs by group.", response = VmpPaginatedList::class, httpMethod = "POST", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @POST
    @Path("/vmp/byVmpCodes")
    fun listVmpsByVmpCodes(
        vmpCodes: ListOfIdsDto
    ) = ResponseUtils.ok(samV2Logic.listVmpsByVmpCodes(vmpCodes.ids).map { mapper.map(it, VmpDto::class.java) })

    @ApiOperation(value = "Finding VMPs by group.", response = VmpPaginatedList::class, httpMethod = "POST", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @POST
    @Path("/vmp/byGroupIds")
    fun listVmpsByGroupIds(
            vmpgIds: ListOfIdsDto
    ) = ResponseUtils.ok(samV2Logic.listVmpsByGroupIds(vmpgIds.ids).map { mapper.map(it, VmpDto::class.java) })

    @ApiOperation(value = "Finding AMPs by group.", response = AmpPaginatedList::class, httpMethod = "POST", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @POST
    @Path("/amp/byGroupCodes")
    fun listAmpsByGroupCodes(
            vmpgCodes: ListOfIdsDto
    ) = ResponseUtils.ok(samV2Logic.listAmpsByGroupCodes(vmpgCodes.ids).map { mapper.map(it, AmpDto::class.java) }.also { addProductIdsToAmps(it) })

    @ApiOperation(value = "Finding AMPs by dmpp code", responseContainer = "Array", response = AmpDto::class, httpMethod = "POST", notes = "Returns a list of amps matched with given input. If several types are provided, paginantion is not supported")
    @POST
    @Path("/amp/byDmppCodes")
    fun listAmpsByDmppCodes(
            dmppCodes: ListOfIdsDto
    ): Response = ResponseUtils.ok(samV2Logic.listAmpsByDmppCodes(dmppCodes.ids).map { mapper.map(it, AmpDto::class.java) }.also { addProductIdsToAmps(it) })

    @ApiOperation(value = "Finding AMPs by group.", response = AmpPaginatedList::class, httpMethod = "POST", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @POST
    @Path("/amp/byGroupIds")
    fun listAmpsByGroupIds(
        groupIds: ListOfIdsDto
    ) = ResponseUtils.ok(samV2Logic.listAmpsByGroupIds(groupIds.ids).map { mapper.map(it, AmpDto::class.java) }.also { addProductIdsToAmps(it) })

    @ApiOperation(value = "Finding AMPs by vmp code.", response = AmpPaginatedList::class, httpMethod = "POST", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @POST
    @Path("/amp/byVmpCodes")
    fun listAmpsByVmpCodes(
            vmpgCodes: ListOfIdsDto
    ) = ResponseUtils.ok(samV2Logic.listAmpsByVmpCodes(vmpgCodes.ids).map { mapper.map(it, AmpDto::class.java) }.also { addProductIdsToAmps(it) })

    @ApiOperation(value = "Finding AMPs by vmp id.", response = AmpPaginatedList::class, httpMethod = "POST", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @POST
    @Path("/amp/byVmpIds")
    fun listAmpsByVmpIds(
            vmpIds: ListOfIdsDto
    ) = ResponseUtils.ok(samV2Logic.listAmpsByVmpIds(vmpIds.ids).map { mapper.map(it, AmpDto::class.java) }.also { addProductIdsToAmps(it) })

    @ApiOperation(value = "Finding AMPs by group.", response = VmpGroupPaginatedList::class, httpMethod = "POST", notes = "Returns a list of group codes matched with given input. If several types are provided, paginantion is not supported")
    @POST
    @Path("/vmpgroup/byGroupCodes")
    fun listVmpGroupsByVmpGroupCodes(
            vmpgCodes: ListOfIdsDto
    ) = ResponseUtils.ok(samV2Logic.listVmpGroupsByVmpGroupCodes(vmpgCodes.ids).map { mapper.map(it, VmpGroupDto::class.java) }.also { addProductIdsToVmpGroups(it) })

    @ApiOperation(value = "Finding NMPs by cnk id.", response = AmpPaginatedList::class, httpMethod = "POST", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @POST
    @Path("/nmp/byCnks")
    fun listNmpsByCnks(
            cnks: ListOfIdsDto
    ) = ResponseUtils.ok(samV2Logic.listNmpsByCnks(cnks.ids).map { mapper.map(it, NmpDto::class.java) }.also { addProductIdsToNmps(it) })


    @ApiOperation(value = "Finding codes by code, type and version with pagination.", response = VmpGroupPaginatedList::class, httpMethod = "GET", notes = "Returns a list of group codes matched with given input. If several types are provided, paginantion is not supported")
    @GET
    @Path("/vmpgroup")
    fun findPaginatedVmpGroupsByLabel(
            @ApiParam(value = "language", required = false) @QueryParam("language") language: String,
            @ApiParam(value = "label", required = false) @QueryParam("label") label: String,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary components to form the Complex Key's startKey")
            @QueryParam("startKey") startKey: String?,
            @ApiParam(value = "A vmpgroup document ID", required = false) @QueryParam("startDocumentId") startDocumentId: String?,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") limit: Int?): Response {

        val response: Response

        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)

        val vmpGroupsList = samV2Logic.findVmpGroupsByLabel(language, label, paginationOffset)

        if (vmpGroupsList.rows == null) {
            vmpGroupsList.rows = ArrayList()
        }

        val vmpGroupDtosPaginatedList = org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpGroupDto>()
        mapper.map(
                vmpGroupsList,
                vmpGroupDtosPaginatedList,
                object : TypeBuilder<PaginatedList<VmpGroup>>() {}.build(),
                object : TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpGroupDto>>() {}.build()
        )
        addProductIdsToVmpGroups(vmpGroupDtosPaginatedList.rows)
        response = ResponseUtils.ok(vmpGroupDtosPaginatedList)

        return response
    }

    @ApiOperation(value = "Finding codes by code, type and version with pagination.", response = VmpGroupPaginatedList::class, httpMethod = "GET", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GET
    @Path("/vmpgroup/all")
    fun findPaginatedVmpGroups(
            @ApiParam(value = "A vmpgroup document ID", required = false) @QueryParam("startDocumentId") startDocumentId: String?,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") limit: Int?): Response {
        val response: Response

        val paginationOffset = PaginationOffset(null, startDocumentId, null, limit)

        val vmpGroupsList = samV2Logic.findVmpGroups(paginationOffset)

        if (vmpGroupsList.rows == null) {
            vmpGroupsList.rows = ArrayList()
        }

        val vmpGroupDtosPaginatedList = org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpGroupDto>()
        mapper.map(
                vmpGroupsList,
                vmpGroupDtosPaginatedList,
                object : TypeBuilder<PaginatedList<VmpGroup>>() {}.build(),
                object : TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<VmpGroupDto>>() {}.build()
        )
        addProductIdsToVmpGroups(vmpGroupDtosPaginatedList.rows)
        response = ResponseUtils.ok(vmpGroupDtosPaginatedList)

        return response
    }

    @ApiOperation(value = "List all substances.", response = VmpGroupPaginatedList::class, httpMethod = "GET", notes = "Returns a list of existing substances")
    @GET
    @Path("/substance")
    fun listSubstances(): Response {
        return ResponseUtils.ok(samV2Logic.listSubstances().map { mapper.map(it, SubstanceDto::class.java) })
    }

    @ApiOperation(value = "List all pharmaceutical forms.", response = VmpGroupPaginatedList::class, httpMethod = "GET", notes = "Returns a list of existing pharmaceutical forms")
    @GET
    @Path("/pharmaform")
    fun listPharmaceuticalForms(): Response {
        return ResponseUtils.ok(samV2Logic.listPharmaceuticalForms().map { mapper.map(it, PharmaceuticalFormDto::class.java) })
    }

    private fun addProductIdsToVmpGroups(vmpGroups: List<VmpGroupDto>) : List<VmpGroupDto> {
        val productIds = samV2Logic.listProductIds(vmpGroups.map { "SAMID:${it.id}" })
        vmpGroups.forEachIndexed { index, g ->
            g.productId = if (index < productIds.size && productIds[index].id == "SAMID:${g.id}") productIds[index].productId else productIds.find { it.id == "SAMID:${g.id}"}?.productId
        }
        return vmpGroups
    }

    private fun addProductIdsToAmps(amps: List<AmpDto>) : List<AmpDto> {
        val dmpps = amps.flatMap { it.ampps.flatMap { it.dmpps ?: listOf() } }.filterNotNull()
        val productIds = samV2Logic.listProductIds(dmpps.map { "SAMID:${it.id}" })
        dmpps.forEachIndexed { index, dmpp ->
            dmpp.productId = if (index < productIds.size && productIds[index].id == "SAMID:${dmpp.id}") productIds[index].productId else productIds.find { it.id == "SAMID:${dmpp.id}"}?.productId
        }
        return amps
    }

    private fun addProductIdsToNmps(nmps: List<NmpDto>) : List<NmpDto> {
        val productIds = samV2Logic.listProductIds(nmps.map { "SAMID:${it.id}" })
        nmps.forEachIndexed { index, nmp ->
            nmp.productId = if (index < productIds.size && productIds[index].id == "SAMID:${nmp.id}") productIds[index].productId else productIds.find { it.id == "SAMID:${nmp.id}"}?.productId
        }
        return nmps
    }



}
