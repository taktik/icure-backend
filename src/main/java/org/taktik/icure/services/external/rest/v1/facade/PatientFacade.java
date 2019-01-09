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

package org.taktik.icure.services.external.rest.v1.facade;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.ektorp.ComplexKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.db.Sorting;
import org.taktik.icure.dto.filter.predicate.Predicate;
import org.taktik.icure.entities.AccessLog;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.User;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.exceptions.BulkUpdateConflictException;
import org.taktik.icure.exceptions.DocumentNotFoundException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.exceptions.UpdateConflictException;
import org.taktik.icure.logic.AccessLogLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.logic.impl.filter.Filters;
import org.taktik.icure.services.external.rest.v1.dto.IdWithRevDto;
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto;
import org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair;
import org.taktik.icure.services.external.rest.v1.dto.PatientDto;
import org.taktik.icure.services.external.rest.v1.dto.PatientPaginatedList;
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.PatientHealthCarePartyDto;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain;
import org.taktik.icure.utils.ResponseUtils;

import javax.security.auth.login.LoginException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
@Path("/patient")
@Api(tags = { "patient" })
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class PatientFacade implements OpenApiFacade{
	private static final Logger log = LoggerFactory.getLogger(PatientFacade.class);

	private ICureSessionLogic sessionLogic;
	private AccessLogLogic accessLogLogic;
	private MapperFacade mapper;
	private org.taktik.icure.logic.impl.filter.Filters filters;
	private PatientLogic patientLogic;

    @ApiOperation(
            value = "Find patients for the current user (HcParty) ",
            response = org.taktik.icure.services.external.rest.v1.dto.PatientPaginatedList.class,
            httpMethod = "GET",
            notes = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " +
                    "Null it means that this is the last page."
    )
    @GET
    @Path("/byNameBirthSsinAuto")
    public Response findByNameBirthSsinAuto(
    		@ApiParam(value = "HealthcareParty Id, if unset will user user's hcpId") @QueryParam("healthcarePartyId") String healthcarePartyId,
            @ApiParam(value = "Optional value for filtering results") @QueryParam("filterValue") String filterValue,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " +
                    "components to form the Complex Key's startKey") @QueryParam("startKey") String startKey,
            @ApiParam(value = "A patient document ID") @QueryParam("startDocumentId") String startDocumentId,
            @ApiParam(value = "Number of rows") @QueryParam("limit") Integer limit,
            @ApiParam(value = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.",
	            defaultValue = "asc") @QueryParam("sortDirection") String sortDirection) {

        Response response;

        if (healthcarePartyId==null) {
	        healthcarePartyId = sessionLogic.getCurrentHealthcarePartyId();
        }

        String[] startKeyElements = new Gson().fromJson(startKey, String[].class);
        @SuppressWarnings("unchecked") PaginationOffset paginationOffset = new PaginationOffset(startKeyElements, startDocumentId, null,
                limit == null ? null : limit);

	    PaginatedList<Patient> patients = patientLogic.findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(healthcarePartyId, paginationOffset, filterValue, new Sorting(null, sortDirection));

	    if (patients != null) {
		    response = buildPaginatedListResponse(patients);
	    } else {
	        response = ResponseUtils.internalServerError("Listing patients failed.");
	    }

	    return response;
    }

	private Response buildPaginatedListResponse(PaginatedList<Patient> patients) {
		Response response;
		if (patients.getRows() == null) {
			patients.setRows(new ArrayList<>());
		}

		org.taktik.icure.services.external.rest.v1.dto.PaginatedList<PatientDto> paginatedPatientDtoList =
				new org.taktik.icure.services.external.rest.v1.dto.PaginatedList<>();
		mapper.map(patients, paginatedPatientDtoList, new TypeBuilder<PaginatedList<Patient>>() {
				}.build(),
				new TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<PatientDto>>() {
				}.build());
		response = ResponseUtils.ok(paginatedPatientDtoList);
		return response;
	}


	@ApiOperation(
			value = "List patients of a specific HcParty or of the current HcParty ",
			response = org.taktik.icure.services.external.rest.v1.dto.PatientPaginatedList.class,
			httpMethod = "GET",
			notes = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " +
					"Null it means that this is the last page."
	)
	@GET
	@Path("/ofHcParty/{hcPartyId}")
	public Response listPatientsOfHcParty(@PathParam("hcPartyId") String hcPartyId,
								 @ApiParam(value = "Optional value for sorting results by a given field ('name', 'ssin', 'dateOfBirth'). " +
										 "Specifying this deactivates filtering") @QueryParam("sortField") String sortField,
								 @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " +
										 "components to form the Complex Key's startKey") @QueryParam("startKey") String startKey,
								 @ApiParam(value = "A patient document ID") @QueryParam("startDocumentId") String startDocumentId,
								 @ApiParam(value = "Number of rows") @QueryParam("limit") Integer limit,
								 @ApiParam(value = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.",
									 defaultValue = "asc") @QueryParam("sortDirection") String sortDirection) {

		Response response;

		String[] startKeyElements = new Gson().fromJson(startKey, String[].class);
		@SuppressWarnings("unchecked") PaginationOffset paginationOffset = new PaginationOffset(startKeyElements, startDocumentId, null, limit);

		PaginatedList<Patient> patients = patientLogic.findOfHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(hcPartyId, paginationOffset, null, new Sorting(sortField, sortDirection));

		if (patients != null) {
			response = buildPaginatedListResponse(patients);

		} else {
			response = ResponseUtils.internalServerError("Listing patients failed.");
		}

		return response;
	}

	@ApiOperation(
		value = "List patients that have been merged towards another patient ",
		response = org.taktik.icure.services.external.rest.v1.dto.PatientDto.class,
		responseContainer = "Array",
		httpMethod = "GET",
		notes = "Returns a list of patients that have been merged after the provided date"
	)
	@GET
	@Path("/merges/{date}")
	public List<PatientDto> listOfMergesAfter(@PathParam("date") Long date) {
		return patientLogic.listOfMergesAfter(date).stream().map(p->mapper.map(p, PatientDto.class)).collect(Collectors.toList());
	}

	@ApiOperation(
		value = "List patients that have been modified after the provided date",
		response = org.taktik.icure.services.external.rest.v1.dto.PatientPaginatedList.class,
		httpMethod = "GET",
		notes = "Returns a list of patients that have been modified after the provided date"
	)
	@GET
	@Path("/modifiedAfter/{date}")
	public PatientPaginatedList listOfPatientsModifiedAfter(@PathParam("date") Long date,
	                                                        @ApiParam(value = "The start key for pagination the date of the first element of the new page") @QueryParam("startKey") Long startKey,
	                                                        @ApiParam(value = "A patient document ID") @QueryParam("startDocumentId") String startDocumentId,
	                                                        @ApiParam(value = "Number of rows") @QueryParam("limit") Integer limit) {
		PaginatedList<Patient> patientPaginatedList = patientLogic.listOfPatientsModifiedAfter(date, startKey, startDocumentId, limit);
		return mapper.map(patientPaginatedList, PatientPaginatedList.class);
	}

	@SuppressWarnings("Duplicates")
	@ApiOperation(
			value = "List patients for a specific HcParty or for the current HcParty ",
			response = org.taktik.icure.services.external.rest.v1.dto.PatientPaginatedList.class,
			httpMethod = "GET",
			notes = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " +
					"Null it means that this is the last page."
	)
	@GET
	@Path("/hcParty/{hcPartyId}")
	public Response listPatientsByHcParty(@PathParam("hcPartyId") String hcPartyId,
										  @ApiParam(value = "Optional value for sorting results by a given field ('name', 'ssin', 'dateOfBirth'). " +
												  "Specifying this deactivates filtering") @QueryParam("sortField") String sortField,
										  @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " +
												  "components to form the Complex Key's startKey") @QueryParam("startKey") String startKey,
										  @ApiParam(value = "A patient document ID") @QueryParam("startDocumentId") String startDocumentId,
										  @ApiParam(value = "Number of rows") @QueryParam("limit") Integer limit,
										  @ApiParam(value = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.",
												  defaultValue = "asc") @QueryParam("sortDirection") String sortDirection) {

		return listPatients(hcPartyId, sortField, startKey, startDocumentId, limit, sortDirection);
	}

	@Path("/hcParty/{hcPartyId}/count")
	@ApiOperation(
			value = "Get count of patients for a specific HcParty or for the current HcParty ",
			response = ContentDto.class,
			httpMethod = "GET",
			notes = "Returns the count of patients"
	)
	@GET
	public Response countOfPatients(@ApiParam(value = "Healthcare party id") @PathParam("hcPartyId") String hcPartyId) {
		return ResponseUtils.ok(ContentDto.fromNumberValue(patientLogic.countByHcParty(hcPartyId)));
	}

	@ApiOperation(
			value = "List patients for a specific HcParty",
			response = org.taktik.icure.services.external.rest.v1.dto.PatientPaginatedList.class,
			httpMethod = "GET",
			notes = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " +
					"Null it means that this is the last page."
	)
	@GET
	public Response listPatients(@ApiParam(value = "Healthcare party id") @QueryParam("hcPartyId") String hcPartyId,
			@ApiParam(value = "Optional value for sorting results by a given field ('name', 'ssin', 'dateOfBirth'). " +
                    "Specifying this deactivates filtering") @QueryParam("sortField") String sortField,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " +
                    "components to form the Complex Key's startKey") @QueryParam("startKey") String startKey,
            @ApiParam(value = "A patient document ID") @QueryParam("startDocumentId") String startDocumentId,
            @ApiParam(value = "Number of rows") @QueryParam("limit") Integer limit,
            @ApiParam(value = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.",
	            defaultValue = "asc") @QueryParam("sortDirection") String sortDirection) {

		Response response;

	    String[] startKeyElements = new Gson().fromJson(startKey, String[].class);
		@SuppressWarnings("unchecked") PaginationOffset paginationOffset = new PaginationOffset(startKeyElements, startDocumentId, null,
			limit);

		PaginatedList<Patient> patients = patientLogic.findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(hcPartyId, paginationOffset, null, new Sorting(sortField, sortDirection));

		if (patients != null) {
			response = buildPaginatedListResponse(patients);

		} else {
			response = ResponseUtils.internalServerError("Listing patients failed.");
		}

		return response;
	}

	@ApiOperation(
			value = "List patients by pages for a specific HcParty",
			response = org.taktik.icure.services.external.rest.v1.dto.PatientPaginatedList.class,
			httpMethod = "GET",
			notes = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " +
					"Null it means that this is the last page."
	)
	@GET
	@Path("/idsPages")
	public Response listPatientsIds(@ApiParam(value = "Healthcare party id") @QueryParam("hcPartyId") String hcPartyId,
	                             @ApiParam(value = "The page first id") @QueryParam("startKey") String startKey,
	                             @ApiParam(value = "A patient document ID") @QueryParam("startDocumentId") String startDocumentId,
	                             @ApiParam(value = "Page size") @QueryParam("limit") Integer limit) {

		Response response;

		ComplexKey startKeyElements = startKey == null ? null : ComplexKey.of((Object[]) new Gson().fromJson(startKey, String[].class));
		@SuppressWarnings("unchecked") PaginationOffset paginationOffset = new PaginationOffset(startKeyElements, startDocumentId, null,
				limit);

		PaginatedList<String> patientIds = patientLogic.findByHcPartyIdsOnly(hcPartyId, paginationOffset);

		if (patientIds != null) {
			response = ResponseUtils.ok(patientIds);
		} else {
			response = ResponseUtils.internalServerError("Listing patients failed.");
		}

		return response;
	}

	@ApiOperation(
			httpMethod = "GET",
			response = org.taktik.icure.services.external.rest.v1.dto.PatientDto.class,
			value = "Get Paginated List of Patients sorted by Access logs descending")
	@GET
	@Path("/byExternalId/{externalId}")
	public org.taktik.icure.services.external.rest.v1.dto.PatientDto findByAccessLogUserAfterDate(@PathParam("externalId") @ApiParam(value = "A external ID", required = true) String externalId) {
		return mapper.map(patientLogic.getByExternalId(externalId), PatientDto.class);
	}

	@ApiOperation(response = org.taktik.icure.services.external.rest.v1.dto.PatientPaginatedList.class,
            httpMethod = "GET",
            value = "Get Paginated List of Patients sorted by Access logs descending")
    @GET
    @Path("/byAccess/{userId}")
    public Response findByAccessLogUserAfterDate(@PathParam("userId") @ApiParam(value = "A User ID", required = true) String userId,
                                                 @ApiParam(value = "The type of access (COMPUTER or USER)") @QueryParam("accessType") String accessType,
                                                 @ApiParam(value = "The start search epoch") @QueryParam("startDate") Long startDate,
												 @ApiParam(value = "The start key for pagination") @QueryParam("startKey") String startKey,
                                                 @ApiParam(value = "A patient document ID") @QueryParam("startDocumentId") String startDocumentId,
                                                 @ApiParam(value = "Number of rows") @QueryParam("limit") Integer limit) {

        @SuppressWarnings("unchecked") PaginationOffset paginationOffset = new PaginationOffset(startKey, startDocumentId, null, limit);
        org.taktik.icure.db.PaginatedList<AccessLog> accessLogs = accessLogLogic.findByUserAfterDate(userId, accessType, Instant.ofEpochMilli(startDate), paginationOffset, true);

        if (accessLogs != null) {
            PatientPaginatedList patientsDtos = new PatientPaginatedList();

            patientsDtos.setNextKeyPair(mapper.map(accessLogs.getNextKeyPair(), PaginatedDocumentKeyIdPair.class));
            patientsDtos.setPageSize(accessLogs.getPageSize());
            patientsDtos.setTotalSize(accessLogs.getTotalSize());

			List<String> patientIds = removeDuplicates(accessLogs.getRows().stream().filter(Objects::nonNull).sorted((a, b)->b.getDate().compareTo(a.getDate())).map(AccessLog::getPatientId).collect(Collectors.toList()));

			patientsDtos.setRows(patientLogic.getPatients(patientIds).stream().filter(p->p!=null&&p.getDeletionDate()==null).map(p -> {
				//Use an optimized stripped down and custom version of the mapper
				PatientDto pdto = new PatientDto();
				pdto.setId(p.getId());

				pdto.setLastName(p.getLastName());
				pdto.setFirstName(p.getFirstName());
				pdto.setPartnerName(p.getPartnerName());
				pdto.setMaidenName(p.getMaidenName());
				pdto.setDateOfBirth(p.getDateOfBirth());
				pdto.setSsin(p.getSsin());
				pdto.setExternalId(p.getExternalId());
				pdto.setPatientHealthCareParties(p.getPatientHealthCareParties().stream().map(phcp->mapper.map(phcp, PatientHealthCarePartyDto.class)).collect(Collectors.toList()));
				pdto.setAddresses(new TreeSet<>(p.getAddresses().stream().map(a->mapper.map(a, AddressDto.class)).collect(Collectors.toSet())));

				return pdto;
			}).collect(Collectors.toList()));

            return ResponseUtils.ok(patientsDtos);
        } else {
            return ResponseUtils.internalServerError("AccessLog based patient listing failed");
        }
    }

	private List<String> removeDuplicates(List<String> patientIds) {
		Set<String> patientIdsSet = new LinkedHashSet<>();
		patientIdsSet.addAll(patientIds);
		patientIds = new ArrayList<>(patientIdsSet);
		return patientIds;
	}

	@ApiOperation(
			value = "Filter patients for the current user (HcParty) ",
			response = org.taktik.icure.services.external.rest.v1.dto.PatientPaginatedList.class,
			httpMethod = "POST",
			notes = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page."
	)
	@POST
	@Path("/filter")
	public Response filterBy(
			@ApiParam(value = "The start key for pagination, depends on the filters used") @QueryParam("startKey") String startKey,
			@ApiParam(value = "A patient document ID") @QueryParam("startDocumentId") String startDocumentId,
			@ApiParam(value = "Number of rows") @QueryParam("limit") Integer limit,
			@ApiParam(value = "Skip rows") @QueryParam("skip") Integer skip,
			@ApiParam(value = "Sort key") @QueryParam("sort") String sort,
			@ApiParam(value = "Descending") @QueryParam("desc") Boolean desc,
			FilterChain filterChain) {

		Response response;

		ArrayList startKeyList = null;
		if (startKey != null && startKey.length()>0) {
			startKeyList = new ArrayList<>(Splitter.on(",").omitEmptyStrings().trimResults().splitToList(startKey));
		}

		@SuppressWarnings("unchecked") PaginationOffset paginationOffset = new PaginationOffset(startKeyList, startDocumentId, skip, limit);


		try {
			PaginatedList<Patient> patients;
			long timing = System.currentTimeMillis();
			if (filterChain != null) {
				patients = patientLogic.listPatients(paginationOffset, new org.taktik.icure.dto.filter.chain.FilterChain(filterChain.getFilter(), mapper.map(filterChain.getPredicate(), Predicate.class)), sort, desc);
				//patients = iCureFacade.findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(paginationOffset, mapper.map(filterChain, org.taktik.icure.dto.filter.chain.FilterChain.class));
			} else {
				patients = patientLogic.findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(null, paginationOffset, null, new Sorting(null, "asc"));
			}
			log.info("Filter patients in "+(System.currentTimeMillis() - timing) + " ms.");
			if (patients != null) {
				response = buildPaginatedListResponse(patients);
			} else {
				response = ResponseUtils.internalServerError("Listing patients failed.");
			}
		} catch (LoginException e) {
			log.warn(e.getMessage(), e);
			response = ResponseUtils.badRequest(e.getMessage());
		}

		return response;
	}

	@ApiOperation(
			value = "Get ids of patients matching the provided filter for the current user (HcParty) ",
			response = String.class,
			responseContainer = "Array",
			httpMethod = "POST"
	)
	@POST
	@Path("/match")
	public List<String> matchBy(Filter filter) throws LoginException {
		return new ArrayList<>(filters.resolve(filter));
	}

	@ApiOperation(
			value = "Filter patients for the current user (HcParty) ",
			response = org.taktik.icure.services.external.rest.v1.dto.PatientDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Returns a list of patients"
	)
	@GET
	@Path("/fuzzy")
	public Response fuzzySearch(
			@ApiParam(value = "The first name") @QueryParam("firstName") String firstName,
			@ApiParam(value = "The last name") @QueryParam("lastName") String lastName,
			@ApiParam(value = "The date of birth") @QueryParam("dateOfBirth") Integer dateOfBirth
			) {
		Response response;

		try {
			List<Patient> patients = patientLogic.fuzzySearchPatients(sessionLogic.getCurrentHealthcarePartyId(),firstName, lastName, dateOfBirth);
			response = ResponseUtils.ok(patients.stream().map(p->mapper.map(p, PatientDto.class)).collect(Collectors.toList()));
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			response = ResponseUtils.badRequest(e.getMessage());
		}

		return response;
	}

    @ApiOperation(
			value = "Create a patient",
			response = PatientDto.class,
			httpMethod = "POST",
			notes = "Name, last name, date of birth, and gender are required. After creation of the patient and obtaining the ID, you need to create an initial delegation."
	)
    @POST
    public Response createPatient(PatientDto p) {
		if (p == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Patient patient;
		try {
			patient = patientLogic.createPatient(mapper.map(p, Patient.class));
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}

	    boolean succeed = (patient != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(patient, PatientDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Patient creation failed.").build();
		}
    }

	@ApiOperation(
			value = "Delete patients.",
			response = String.class,
			responseContainer = "Array",
			httpMethod = "DELETE",
			notes = "Response is an array containing the ID of deleted patient.."
	)
	@DELETE
	@Path("/{patientIds}")
	public Response deletePatient(@PathParam("patientIds") String patientIds) {
		List<String> ids = Arrays.asList(patientIds.split(","));
		if (ids.size() == 0) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> deletedIds;
		try {
			deletedIds = patientLogic.deletePatients(new HashSet<>(ids));
		} catch (DocumentNotFoundException e) {
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}

		boolean succeed = (deletedIds != null);
		if (succeed) {
			return Response.ok().entity(ids).build();
		} else {
			return Response.status(500).type("text/plain").entity("Patients deletion failed.").build();
		}
	}

	@ApiOperation(
			value = "Find deleted patients",
			response = org.taktik.icure.services.external.rest.v1.dto.PatientPaginatedList.class,
			httpMethod = "GET",
			notes = "Returns a list of deleted patients, within the specified time period, if any."
	)
	@GET
	@Path("/deleted/by_date")
	public Response listDeletedPatients(
			@ApiParam(value = "Filter deletions after this date (unix epoch), included") @QueryParam("startDate") Long startDate,
			@ApiParam(value = "Filter deletions before this date (unix epoch), included") @QueryParam("endDate") Long endDate,
			@ApiParam(value = "Descending") @QueryParam("desc") Boolean desc,
			@ApiParam(value = "A patient document ID") @QueryParam("startDocumentId") String startDocumentId,
			@ApiParam(value = "Number of rows") @QueryParam("limit") Integer limit) {

		PaginationOffset paginationOffset = new PaginationOffset<>(startDate, startDocumentId, null, limit); // TODO works with descending=true?

		PaginatedList<Patient> patients = patientLogic.findDeletedPatientsByDeleteDate(startDate, endDate, desc!=null?desc:false, paginationOffset);

		if (patients != null) {
			return buildPaginatedListResponse(patients);
		} else {
			return ResponseUtils.internalServerError("Listing deleted patients failed.");
		}
	}

	@ApiOperation(
			value = "Find deleted patients",
			response = org.taktik.icure.services.external.rest.v1.dto.PatientPaginatedList.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Returns a list of deleted patients, by name and/or firstname prefix, if any."
	)
	@GET
	@Path("/deleted/by_name")
	public Response listDeletedPatients(
			@ApiParam(value = "First name prefix") @QueryParam("firstName") String firstName,
			@ApiParam(value = "Last name prefix") @QueryParam("lastName") String lastName) {
		Response response;
		try {
			List<Patient> patients = patientLogic.findDeletedPatientsByNames(firstName, lastName);
			response = ResponseUtils.ok(patients.stream().map(p->mapper.map(p, PatientDto.class)).collect(Collectors.toList()));
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			response = ResponseUtils.badRequest(e.getMessage());
		}
		return response;
	}

	@ApiOperation(
			value = "undelete previously deleted patients",
			response = String.class,
			responseContainer = "Array",
			httpMethod = "PUT",
			notes = "Response is an array containing the ID of undeleted patient.."
	)
	@PUT
	@Path("/undelete/{patientIds}")
	public Response undeletePatient(@PathParam("patientIds") String patientIds) {
		List<String> ids = Arrays.asList(patientIds.split(","));
		if (ids.size() == 0) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> deletedIds;
		try {
			deletedIds = patientLogic.undeletePatients(new HashSet<>(ids));
		} catch (DocumentNotFoundException e) {
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}

		if (deletedIds != null) {
			return Response.ok().entity(ids).build();
		} else {
			return Response.status(500).type("text/plain").entity("Patients undeleted failed.").build();
		}
	}


	@ApiOperation(
			value = "Delegates a patients to a healthcare party",
			response = PatientDto.class,
			httpMethod = "POST",
			notes = "It delegates a patient to a healthcare party (By current healthcare party). A modified patient with new delegation gets returned."
	)
	@POST
	@Path("/{patientId}/delegate")
	public Response newDelegations(@PathParam("patientId") String patientId, List<DelegationDto> ds) {
		if (patientId == null || ds == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		try {
			patientLogic.addDelegations(patientId, ds.stream().map(d->mapper.map(d, Delegation.class)).collect(Collectors.toList()));
			Patient patientWithDelegations = patientLogic.getPatient(patientId);

			boolean succeed = (patientWithDelegations != null && patientWithDelegations.getDelegations() != null && patientWithDelegations.getDelegations().size() > 0);
			if (succeed) {
				return Response.ok().entity(mapper.map(patientWithDelegations, PatientDto.class)).build();
			} else {
				return Response.status(500).type("text/plain").entity("An error occurred in creation of the delegation.").build();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.status(500).type("text/plain").entity(e.getMessage()).build();
		}
	}

	@ApiOperation(
			value = "Get patients by id",
			responseContainer = "Array",
			response = PatientDto.class,
			httpMethod = "POST",
			notes = "It gets patient administrative data."
	)
	@POST
	@Path("/byIds")
	public Response getPatients(ListOfIdsDto patientIds) {
		if (patientIds == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		List<Patient> patients = patientLogic.getPatients(patientIds.getIds());

		boolean succeed = (patients != null);
		if (succeed) {
			return Response.ok().entity(patients.stream().map(p -> mapper.map(p, PatientDto.class)).collect(Collectors.toList())).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting patients failed. Possible reasons: no such patient exists, or server error. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
			value = "Get patient",
			response = PatientDto.class,
			httpMethod = "GET",
			notes = "It gets patient administrative data."
	)
	@GET
	@Path("/{patientId}")
	public Response getPatient(@PathParam("patientId") String patientId) {
		if (patientId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Patient patient = patientLogic.getPatient(patientId);

		boolean succeed = (patient != null);
		if (succeed) {

			return Response.ok().entity(mapper.map(patient, PatientDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting patient failed. Possible reasons: no such patient exists, or server error. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
			value = "Modify a patient",
			response = IdWithRevDto.class,
			responseContainer = "Array",
			httpMethod = "POST",
			notes = "Returns the id and _rev of created patients"
	)
	@POST
	@Path("/bulk")
	public Response bulkUpdatePatients(List<PatientDto> patientDtos) {
		if (patientDtos == null) {
			return Response.status(400).type("text/plain").entity("Required body was not specified for this request.").build();
		}

		try {
			Collection<Patient> patients = patientLogic.updateEntities(patientDtos.stream().map(p -> mapper.map(p, Patient.class)).collect(Collectors.toList()));
			return Response.ok().entity(patients.stream().map(p->mapper.map(p, IdWithRevDto.class)).collect(Collectors.toList())).build();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}
	}

	@ApiOperation(
		value = "Modify a patient",
		response = PatientDto.class,
		httpMethod = "PUT",
		notes = "No particular return value. It's just a message."
	)
	@PUT
	public Response modifyPatient(PatientDto patientDto) {
		if (patientDto == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		try {
			Patient modifiedPatient = patientLogic.modifyPatient(mapper.map(patientDto, Patient.class));

			boolean succeed = (modifiedPatient != null);
			if (succeed) {
				return Response.ok().entity(mapper.map(modifiedPatient, PatientDto.class)).build();
			} else {
				log.error("Getting patient failed. Possible reasons: no such patient exists, or server error. Please try again or read the server log.");
				return Response.status(500).type("text/plain").entity("Modification patient failed. Possible reasons: no such patient exists, or server error. Please try again or read the server log.").build();
			}
		} catch (MissingRequirementsException e) {
			log.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}
	}

	@ApiOperation(
			value = "Set a patient referral doctor",
			response = PatientDto.class,
			httpMethod = "PUT"
	)
	@PUT
	@Path("/{patientId}/referral/{referralId}")
	public Response modifyPatientReferral(@PathParam("patientId") String patientId, @ApiParam(value = "The referal id. Accepts 'none' for referral removal.") @PathParam("referralId") String referralId,
										  @ApiParam(value = "Optional value for start of referral") @QueryParam("start") Long start,
										  @ApiParam(value = "Optional value for end of referral") @QueryParam("end") Long end) throws Exception {
		Response response;

		if (patientId == null || referralId == null) {
			String nullProperty = patientId == null ? "patient id is null" : "Referral is null";
			response = ResponseUtils.badRequest("Cannot modify patient summary: supplied " + nullProperty);
		} else {
			Patient patient = patientLogic.getPatient(patientId);
			if (patient != null) {
				Patient modifiedPatient = patientLogic.modifyPatientReferral(patient, referralId.equals("none") ? null : referralId, start == null ? null : Instant.ofEpochMilli(start), end == null ? null : Instant.ofEpochMilli(end));
				response = ResponseUtils.ok(mapper.map(modifiedPatient, PatientDto.class));

			} else {
				response = ResponseUtils.notFound("Could not find patient with ID " + patientId + " in the database");
			}
		}

		return response;
	}

	@ApiOperation(
			value = "Merge a series of patients into another patient",
			response = PatientDto.class,
			httpMethod = "PUT"
	)
	@PUT
	@Path("/mergeInto/{toId}/from/{fromIds}")
	public Response mergeInto(@PathParam("toId") String patientId, @PathParam("fromIds") String fromIds) throws Exception {
		Response response;

		Patient patient = patientLogic.getPatient(patientId);
		List<Patient> fromPatients = Arrays.stream(fromIds.split(",")).map(id->patientLogic.getPatient(id)).collect(Collectors.toList());
		if (patient != null) {
			Patient modifiedPatient = patientLogic.mergePatient(patient, fromPatients);
			response = ResponseUtils.ok(mapper.map(modifiedPatient, PatientDto.class));
		} else {
			response = ResponseUtils.notFound("Could not find patient with ID " + patientId + " in the database");
		}

		return response;
	}

    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

    @Context
    public void setPatientLogic(PatientLogic patientLogic) {
        this.patientLogic = patientLogic;
    }

    @Context
    public void setAccessLogLogic(AccessLogLogic accessLogLogic) {
        this.accessLogLogic = accessLogLogic;
    }

	@Context
	public void setFilters(Filters filters) {
		this.filters = filters;
	}

	@Context
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@ExceptionHandler(BulkUpdateConflictException.class)
	Response bulkUpdateConflictExceptionHandler(BulkUpdateConflictException e) {
		log.error(e.getMessage());
		return Response.status(Response.Status.CONFLICT).type(MediaType.APPLICATION_JSON_TYPE).entity(e.getConflicts()).build();
	}

	@ExceptionHandler(UpdateConflictException.class)
	Response updateConflictExceptionHandler(UpdateConflictException e) {
		log.error(e.getMessage());
		return Response.status(Response.Status.CONFLICT).type(MediaType.APPLICATION_JSON_TYPE).entity(e.getDoc()).build();
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		log.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}

	@Deprecated
	@SuppressWarnings("UnusedDeclaration")
	public static class PatientSummaryDto {
		private Map<String, Object> values = new HashMap<>();
		public PatientSummaryDto() {
		}
		public PatientSummaryDto(Map<String, Object> values) {
			this.values = values;
		}
		public Map<String, Object> getValues() {
			return values;
		}
		public void setValues(Map<String, Object> values) {
			this.values = values;
		}
	}
}
