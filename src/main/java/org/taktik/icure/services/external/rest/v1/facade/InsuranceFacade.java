/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.entities.Insurance;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.InsuranceLogic;
import org.taktik.icure.services.external.rest.v1.dto.AccessLogDto;
import org.taktik.icure.services.external.rest.v1.dto.InsuranceDto;
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/insurance")
@Api(tags = { "insurance" })
@Consumes({"application/json"})
@Produces({"application/json"})
public class InsuranceFacade implements OpenApiFacade{

	private static final Logger logger = LoggerFactory.getLogger(InsuranceFacade.class);

	private InsuranceLogic insuranceLogic;
	private MapperFacade mapper;

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}
	@Context
	public void setInsuranceLogic(InsuranceLogic insuranceLogic) {
		this.insuranceLogic = insuranceLogic;
	}


	@ApiOperation(response = AccessLogDto.class, value = "Creates an insurance")
	@POST
	public Response createInsurance(InsuranceDto insuranceDto) {
		Response response;

		if (insuranceDto == null) {
			response = ResponseUtils.badRequest("Cannot create insurance: supplied insuranceDto is null");

		} else {
			Insurance insurance = insuranceLogic.createInsurance(mapper.map(insuranceDto, Insurance.class));
			if (insurance != null) {
				response = ResponseUtils.ok(mapper.map(insurance, InsuranceDto.class));

			} else {
				response = ResponseUtils.internalServerError("Insurance creation failed");
			}
		}

		return response;
	}

	@ApiOperation(value = "Deletes an insurance")
	@DELETE
	@Path("/{insuranceId}")
	public Response deleteInsurance(@PathParam("insuranceId") String insuranceId) throws DeletionException {
		Response response;

		if (insuranceId == null) {
			response = ResponseUtils.badRequest("Cannot delete insurance: supplied insuranceId is null");

		} else {
			String deletedInsuranceId = insuranceLogic.deleteInsurance(insuranceId);
			if (deletedInsuranceId != null) {
				response = ResponseUtils.ok();

			} else {
				response = ResponseUtils.internalServerError("Insurance deletion failed");
			}
		}

		return response;
	}

	@ApiOperation(response = InsuranceDto.class, value = "Gets an insurance")
	@GET
	@Path("/{insuranceId}")
	public Response getInsurance(@PathParam("insuranceId") String insuranceId) {
		Response response;

		if (insuranceId == null) {
			response = ResponseUtils.badRequest("Cannot get insurance: supplied insuranceId is null");

		} else {
			Insurance insurance = insuranceLogic.getInsurance(insuranceId);
			if (insurance != null) {
				response = ResponseUtils.ok(mapper.map(insurance, InsuranceDto.class));

			} else {
				response = ResponseUtils.internalServerError("Insurance fetching failed");
			}
		}

		return response;
	}

	@ApiOperation(response = InsuranceDto.class, responseContainer = "Array", value = "Gets insurances by id")
	@POST
	@Path("/byIds")
	public Response getInsurances(ListOfIdsDto insuranceIds) {
		Response response;

		if (insuranceIds == null) {
			response = ResponseUtils.badRequest("Cannot get insurance: supplied insuranceIds is null");
		} else {
			List<Insurance> insurances = insuranceLogic.getInsurances(new HashSet<>(insuranceIds.getIds()));
			if (insurances != null) {
				response = ResponseUtils.ok(insurances.stream().map(i->mapper.map(i, InsuranceDto.class)).collect(Collectors.toList()));
			} else {
				response = ResponseUtils.internalServerError("Insurance fetching failed");
			}
		}

		return response;
	}

	@ApiOperation(response = InsuranceDto.class,
		responseContainer = "Array",
		value = "Gets an insurance")
	@GET
	@Path("/byCode/{insuranceCode}")
	public Response listInsurancesByCode(@PathParam("insuranceCode") String insuranceCode) {
		Response response;

		List<Insurance> insurances = insuranceLogic.listInsurancesByCode(insuranceCode);
		if (insurances != null) {
			response = ResponseUtils.ok(insurances.stream().map((i) -> mapper.map(i, InsuranceDto.class)).collect(Collectors.toList()));
		} else {
			response = ResponseUtils.internalServerError("Listing insurances failed");
		}

		return response;
	}

	@ApiOperation(response = InsuranceDto.class,
			responseContainer = "Array",
			value = "Gets an insurance")
	@GET
	@Path("/byName/{insuranceName}")
	public Response listInsurancesByName(@PathParam("insuranceName") String insuranceName) {
		Response response;

		List<Insurance> insurances = insuranceLogic.listInsurancesByName(insuranceName);
		System.out.println(insurances);
		if (insurances != null) {
			response = ResponseUtils.ok(insurances.stream().map((i) -> mapper.map(i, InsuranceDto.class)).collect(Collectors.toList()));
		} else {
			response = ResponseUtils.internalServerError("Listing insurances failed");
		}
		System.out.println(response);
		return response;
	}

	@ApiOperation(response = InsuranceDto.class, value = "Modifies an insurance")
	@PUT
	public Response modifyInsurance(InsuranceDto insuranceDto) {
		Response response;

		if (insuranceDto == null) {
			response = ResponseUtils.badRequest("Cannot modify insurance: supplied insuranceDto is null");

		} else {
			Insurance insurance = insuranceLogic.modifyInsurance(mapper.map(insuranceDto, Insurance.class));
			if (insurance != null) {
				response = ResponseUtils.ok(mapper.map(insurance, InsuranceDto.class));

			} else {
				response = ResponseUtils.internalServerError("Insurance modification failed");
			}
		}

		return response;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
