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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.HeaderType;
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.taktik.icure.be.ehealth.logic.kmehr.medex.MedexLogic;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.db.Sorting;
import org.taktik.icure.dto.filter.predicate.Predicate;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.logic.CodeLogic;
import org.taktik.icure.services.external.rest.v1.dto.CodeDto;
import org.taktik.icure.services.external.rest.v1.dto.CodePaginatedList;
import org.taktik.icure.services.external.rest.v1.dto.MedexInfoDto;
import org.taktik.icure.services.external.rest.v1.dto.PatientDto;
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain;
import org.taktik.icure.utils.ResponseUtils;

import javax.security.auth.login.LoginException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/medex")
@Api(tags = { "medex" })
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class MedexFacade implements OpenApiFacade {

    private static final Logger logger = LoggerFactory.getLogger(MedexFacade.class);

    private MedexLogic medexLogic;
    private MapperFacade mapperFacade;

    @ApiOperation(
            value = "Generate a Medex XML String",
            response = String.class,
            httpMethod = "POST"
    )
    @POST
    @Path("/generate")
    public Response generateMedex(MedexInfoDto infos) {
        Response response = ResponseUtils.ok(medexLogic.createMedex(
                mapperFacade.map(infos.getAuthor(), HealthcareParty.class),
                mapperFacade.map(infos.getPatient(), Patient.class),
                infos.getPatientLanguage(),
                infos.getIncapacityType(),
                infos.getIncapacityReason(),
                infos.getOutOfHomeAllowed(),
                infos.getCertificateDate(),
                infos.getContentDate(),
                infos.getBeginDate(),
                infos.getEndDate(),
                infos.getDiagnosisICD(),
                infos.getDiagnosisICPC(),
                infos.getDiagnosisDescr()
        ));
        return response;
    }

    @Context
    public void setMedexLogic(MedexLogic medexLogic) {
        this.medexLogic = medexLogic;
    }

    @Context
    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }
}
