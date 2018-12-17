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

package org.taktik.icure.services.external.rest.v1.wsfacade;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.ws.rs.Path;

import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.logic.kmehr.smf.SoftwareMedicalFileLogic;
import org.taktik.icure.be.ehealth.logic.kmehr.sumehr.SumehrLogic;
import org.taktik.icure.be.ehealth.logic.kmehr.medicationscheme.MedicationSchemeLogic;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.services.external.http.websocket.KmehrFileOperation;
import org.taktik.icure.services.external.http.websocket.WebSocketOperation;
import org.taktik.icure.services.external.http.websocket.WebSocketParam;
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.SoftwareMedicalFileExportDto;
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.SumehrExportInfoDto;
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.MedicationSchemeExportInfoDto;


@Component
@Path("/ws/be_kmehr")
public class KmehrWsFacade {
	private MapperFacade mapper;
	private SessionLogic sessionLogic;
	private SumehrLogic sumehrLogic;
	private SoftwareMedicalFileLogic softwareMedicalFileLogic;
	private MedicationSchemeLogic medicationSchemeLogic;
	private HealthcarePartyLogic healthcarePartyLogic;
	private PatientLogic patientLogic;

	@Path("/generateSumehr")
	@WebSocketOperation(adapterClass = KmehrFileOperation.class)
	public void generateSumehr(@WebSocketParam("patientId") String patientId, @WebSocketParam("language") String language, @WebSocketParam("info") SumehrExportInfoDto info, KmehrFileOperation operation) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10000);
		try {
			sumehrLogic.createSumehr(bos, patientLogic.getPatient(patientId), info.getSecretForeignKeys(),
					healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()),
					mapper.map(info.getRecipient(), HealthcareParty.class), language, info.getComment(), operation);
			operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()));
			bos.close();
		} catch (Exception e) {
			operation.errorResponse(e);
		}
	}

	@Path("/generateSumehrpp")
	@WebSocketOperation(adapterClass = KmehrFileOperation.class)
	public void generateSumehrPlusPlus(@WebSocketParam("patientId") String patientId, @WebSocketParam("language") String language, @WebSocketParam("info") SumehrExportInfoDto info, KmehrFileOperation operation) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10000);
		try {
			sumehrLogic.createSumehrPlusPlus(bos, patientLogic.getPatient(patientId), info.getSecretForeignKeys(), healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()), mapper.map(info.getRecipient(), HealthcareParty.class), language, info.getComment(), operation);
			operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()));
			bos.close();
		} catch (Exception e) {
			operation.errorResponse(e);
		}
	}

	@Path("/validateSumehr")
	@WebSocketOperation(adapterClass = KmehrFileOperation.class)
	public void validateSumehr(@WebSocketParam("patientId") String patientId, @WebSocketParam("language") String language, @WebSocketParam("info") SumehrExportInfoDto info, KmehrFileOperation operation) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10000);
		try {
			sumehrLogic.validateSumehr(bos, patientLogic.getPatient(patientId), info.getSecretForeignKeys(), healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()), mapper.map(info.getRecipient(), HealthcareParty.class), language, info.getComment(), operation);
			operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()));
			bos.close();
		} catch (Exception e) {
			operation.errorResponse(e);
		}
	}

	@Path("/generateSmf")
	@WebSocketOperation(adapterClass = KmehrFileOperation.class)
	public void generateSmfExport(@WebSocketParam("patientId") String patientId , @WebSocketParam("language") String language, @WebSocketParam("info") SoftwareMedicalFileExportDto info, KmehrFileOperation operation) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10000);
		try {
			softwareMedicalFileLogic.createSmfExport(bos, patientLogic.getPatient(patientId), info.getSecretForeignKeys(), healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()), language, operation, operation);
			operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()));
			bos.close();
		} catch (Exception e) {
			operation.errorResponse(e);
		}
	}

	@Path("/generateMedicationScheme")
	@WebSocketOperation(adapterClass = KmehrFileOperation.class)
	public void generateMedicationSchemeExport(@WebSocketParam("patientId") String patientId , @WebSocketParam("language") String language, @WebSocketParam("info") MedicationSchemeExportInfoDto info,  @WebSocketParam("version") Integer version, KmehrFileOperation operation) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10000);
		try {
			//fun createMedicationSchemeExport(os: OutputStream, patient: Patient, sfks: List<String>, sender: HealthcareParty, language: String, version: Int, decryptor: AsyncDecrypt?, progressor: AsyncProgress?)
			medicationSchemeLogic.createMedicationSchemeExport(bos, patientLogic.getPatient(patientId), info.getSecretForeignKeys(),
					healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()),
					language, version, operation, null);
			operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()));
			bos.close();
		} catch (Exception e) {
			operation.errorResponse(e);
		}
	}

	@Autowired
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

	@Autowired
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Autowired
	public void setSumehrLogic(SumehrLogic sumehrLogic) {
		this.sumehrLogic = sumehrLogic;
	}

	@Autowired
	public void setMedicationSchemeLogic(MedicationSchemeLogic medicationSchemeLogic) { this.medicationSchemeLogic = medicationSchemeLogic; }

	@Autowired
	public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
		this.healthcarePartyLogic = healthcarePartyLogic;
	}

	@Autowired
	public void setPatientLogic(PatientLogic patientLogic) {
		this.patientLogic = patientLogic;
	}

	@Autowired
	public void setSoftwareMedicalFileLogic(SoftwareMedicalFileLogic softwareMedicalFileLogic) {
		this.softwareMedicalFileLogic = softwareMedicalFileLogic;
	}
}
