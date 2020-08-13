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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.taktik.icure.constants.PropertyTypes;
import org.taktik.icure.entities.embed.DatabaseSynchronization;
import org.taktik.icure.logic.ContactLogic;
import org.taktik.icure.logic.DocumentLogic;
import org.taktik.icure.logic.FormLogic;
import org.taktik.icure.logic.HealthElementLogic;
import org.taktik.icure.logic.InvoiceLogic;
import org.taktik.icure.logic.MessageLogic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.logic.ReplicationLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.logic.UserLogic;
import org.taktik.icure.logic.impl.ICureLogicImpl;
import org.taktik.icure.services.external.rest.v1.dto.IndexingInfoDto;
import org.taktik.icure.services.external.rest.v1.dto.ReplicationInfoDto;
import org.taktik.icure.services.external.rest.v1.dto.UserStubDto;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Path("/icure")
@Api(tags = { "icure" })
@Consumes({"application/json"})
@Produces({"application/json"})
public class ICureFacade implements OpenApiFacade{


	private static Logger logger = LoggerFactory.getLogger(ICureFacade.class);

	private ReplicationLogic replicationLogic;
	private ICureLogicImpl iCureLogic;
	private PatientLogic patientLogic;
	private UserLogic userLogic;
	private ContactLogic contactLogic;
	private MessageLogic messageLogic;
	private InvoiceLogic invoiceLogic;
	private DocumentLogic documentLogic;
	private HealthElementLogic healthElementLogic;
	private FormLogic formLogic;
	private SessionLogic sessionLogic;
	private MapperFacade mapper;
	private ApplicationContext context;

	@ApiOperation(
			value = "Get version",
			response = String.class
	)
	@GET
	@Path("/v")
	@Produces({"text/plain"})
	public Response getVersion() {
		return Response.ok(iCureLogic.getVersion()).build();
	}

	@ApiOperation(
			value = "Check if a user exists",
			response = String.class
	)
	@GET
	@Path("/ok")
	@Produces({"text/plain"})
	public Response isReady() {
		return Response.ok(
				userLogic.hasEntities()?"true":"false"
		).build();
	}

	@ApiOperation(
			value = "Check if a patient exists",
			response = String.class
	)
	@GET
	@Path("/pok")
	@Produces({"text/plain"})
	public Response isPatientReady() {
		return Response.ok(
				patientLogic.hasEntities()?"true":"false"
		).build();
	}


	@ApiOperation(
			value = "Get users stubs",
			responseContainer = "Array",
			response = UserStubDto.class
	)
	@GET
	@Path("/u")
	public Response getUsers() {
		return Response.ok(userLogic.getAllEntities().stream().map(u->mapper.map(u, UserStubDto.class)).collect(Collectors.toList())).build();
	}

	@ApiOperation(
			value = "Get process info",
			response = String.class
	)
	@GET
	@Path("/p")
	@Produces({"text/plain"})
	public Response getProcessInfo() {
		return Response.ok(java.lang.management.ManagementFactory.getRuntimeMXBean().getName()).build();
	}

	@ApiOperation(
			value = "Get version",
			responseContainer = "Array",
			response = String.class
	)
	@GET
	@Path("/propertytypes/{type}")
	public Response getPropertyTypes(@PathParam("type") String type) {
		return Response.ok(type.equals("system")?PropertyTypes.System.identifiers():PropertyTypes.User.identifiers()).build();
	}

	@ApiOperation(
			value = "Get replication info",
			response = ReplicationInfoDto.class
	)
	@GET
	@Path("/r")
	public Response getReplicationInfo() {
		ReplicationInfoDto ri = new ReplicationInfoDto();

		ri.setPendingFrom(0);
		ri.setPendingTo(0);

		ri.setActive(replicationLogic.hasEntities());
		if (ri.getActive()) {
			Map<DatabaseSynchronization, Number> pendingChanges = replicationLogic.getPendingChanges();
			ri.setRunning(pendingChanges.size() > 0);
			for (Map.Entry<DatabaseSynchronization, Number> e : pendingChanges.entrySet()) {
				String src = e.getKey().getSource();
				if (src.contains("127.0.0.1") || src.contains("localhost")) {
					if (e.getValue() != null) {
						ri.setPendingFrom(ri.getPendingFrom() != null ? ri.getPendingFrom() + e.getValue().intValue() : e.getValue().intValue());
					} else {
						ri.setPendingFrom(null);
					}
				} else {
					if (e.getValue() != null) {
						ri.setPendingTo(ri.getPendingTo() != null ? ri.getPendingTo() + e.getValue().intValue() : e.getValue().intValue());
					} else {
						ri.setPendingTo(null);
					}
				}
			}
		}
		return Response.ok(ri).build();
	}

	@ApiOperation(
			value = "Force update design doc",
			response = Boolean.class
	)
	@POST
	@Path("/dd/{entityName}")
	public Response updateDesignDoc(@PathParam("entityName") String entityName, @ApiParam(value = "Trigger indexation warm up") @QueryParam("warmup") Boolean warmup) {
		iCureLogic.updateDesignDoc(entityName, warmup);
		return Response.ok(true).build();
	}

	@ApiOperation(
			value = "Get index info",
			response = IndexingInfoDto.class
	)
	@GET
	@Path("/i")
	public Response getIndexingInfo() {
		return Response.ok(new IndexingInfoDto(iCureLogic.getIndexingStatus())).build();
	}

	@POST @Path("/conflicts/patient")
	public Response resolvePatientsConflicts() throws Exception { patientLogic.solveConflicts(); return Response.ok().build(); }

	@POST @Path("/conflicts/contact")
	public Response resolveContactsConflicts() throws Exception { contactLogic.solveConflicts(); return Response.ok().build(); }

	@POST @Path("/conflicts/form")
	public Response resolveFormsConflicts() throws Exception { formLogic.solveConflicts(); return Response.ok().build(); }

	@POST @Path("/conflicts/healthelement")
	public Response resolveHealthElementsConflicts() throws Exception { healthElementLogic.solveConflicts(); return Response.ok().build(); }

	@POST @Path("/conflicts/invoice")
	public Response resolveInvoicesConflicts() throws Exception { invoiceLogic.solveConflicts(); return Response.ok().build(); }

	@POST @Path("/conflicts/message")
	public Response resolveMessagesConflicts() throws Exception { messageLogic.solveConflicts(); return Response.ok().build(); }

	@POST @Path("/conflicts/document")
	public Response resolveDocumentsConflicts(@QueryParam("ids") String ids) throws Exception { documentLogic.solveConflicts(ids != null ? Arrays.asList(ids.split(",")) : null); return Response.ok().build(); }

	@Context
	public void setReplicationLogic(ReplicationLogic replicationLogic) {
		this.replicationLogic = replicationLogic;
	}

	@Context
	public void setiCureLogic(ICureLogicImpl iCureLogic) {
		this.iCureLogic = iCureLogic;
	}

	@Context
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Context
	public void setUserLogic(UserLogic userLogic) {
		this.userLogic = userLogic;
	}

	@Context
	public void setPatientLogic(PatientLogic patientLogic) {
		this.patientLogic = patientLogic;
	}

	@Context
	public void setContactLogic(ContactLogic contactLogic) {
		this.contactLogic = contactLogic;
	}

	@Context
	public void setMessageLogic(MessageLogic messageLogic) {
		this.messageLogic = messageLogic;
	}

	@Context
	public void setInvoiceLogic(InvoiceLogic invoiceLogic) {
		this.invoiceLogic = invoiceLogic;
	}

	@Context
	public void setHealthElementLogic(HealthElementLogic healthElementLogic) {
		this.healthElementLogic = healthElementLogic;
	}

	@Context
	public void setFormLogic(FormLogic formLogic) {
		this.formLogic = formLogic;
	}

	@Context
	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

	@Context
	public void setDocumentLogic(DocumentLogic documentLogic) { this.documentLogic = documentLogic; }
}
