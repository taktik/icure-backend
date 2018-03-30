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

package org.taktik.icure.services.external.rest.v1.facade.be;

import java.util.Arrays;
import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.logic.etarif.ETarifLogic;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.services.external.rest.v1.dto.be.etarif.TarificationConsultationResultDto;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiDefinitionTags;

@Component
@Path("/be_etarif")
@Api(tags = { "be_etarif" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class ETarifFacade extends OpenApiDefinitionTags {
	private MapperFacade mapper;
	private ETarifLogic eTarifLogic;
	private SessionLogic sessionLogic;
	private HealthcarePartyLogic healthcarePartyLogic;

	@ApiOperation(
			value = "Consult Etarif",
			response = TarificationConsultationResultDto.class,
			httpMethod = "GET",
			notes = ""
	)
	@Path("/{token}/{patientNiss}/{codes}")
	@GET
	public	TarificationConsultationResultDto consultTarif(@PathParam("token") String token, @PathParam("patientNiss") String patientNiss, @PathParam("codes") String codes,  @QueryParam("justification") String justification, @QueryParam("date") Long encounterDateTime, @QueryParam("nihiiDmg") String nihiiDmg) throws TokenNotAvailableException {
		return mapper.map(eTarifLogic.consultTarif(token, patientNiss, Arrays.asList(codes.split(",")), justification, encounterDateTime == null ? null:new Date(encounterDateTime), nihiiDmg), TarificationConsultationResultDto.class);
	}

	@Context
	public void seteTarifLogic(ETarifLogic eTarifLogic) {
		this.eTarifLogic = eTarifLogic;
	}

	@Context
	public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
		this.healthcarePartyLogic = healthcarePartyLogic;
	}

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

	@Context
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}
}
