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
import java.time.Instant;

import javax.ws.rs.Path;

import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.dto.kmehr.v20170901.Utils;
import org.taktik.icure.be.ehealth.logic.kmehr.Config;
import org.taktik.icure.be.ehealth.logic.kmehr.smf.SoftwareMedicalFileLogic;
import org.taktik.icure.be.ehealth.logic.kmehr.sumehr.SumehrLogic;
import org.taktik.icure.be.ehealth.logic.kmehr.diarynote.DiaryNoteLogic;
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
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.DiaryNoteExportInfoDto;
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.MedicationSchemeExportInfoDto;


@Component
@Path("/ws/be_kmehr")
public class KmehrWsFacade {

    @Value("${icure.version}")
    private String ICUREVERSION;

	private MapperFacade mapper;
	private SessionLogic sessionLogic;
	private SumehrLogic sumehrLogicV1;
	private SumehrLogic sumehrLogicV2;
	private DiaryNoteLogic diaryNoteLogic;
	private SoftwareMedicalFileLogic softwareMedicalFileLogic;
	private MedicationSchemeLogic medicationSchemeLogic;
	private HealthcarePartyLogic healthcarePartyLogic;
	private PatientLogic patientLogic;

	@Path("/generateDiaryNote")
    @WebSocketOperation(adapterClass = KmehrFileOperation.class)
    public void generateDiaryNote(@WebSocketParam("patientId") String patientId, @WebSocketParam("language") String language, @WebSocketParam("info") DiaryNoteExportInfoDto info, KmehrFileOperation operation) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(10000);
        try {
            diaryNoteLogic.createDiaryNote(bos, patientLogic.getPatient(patientId), info.getSecretForeignKeys(),
                healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()),
                mapper.map(info.getRecipient(), HealthcareParty.class), language, info.getNote(), info.getTags(), info.getContexts(), info.getPsy(), info.getDocumentId(), info.getAttachmentId(), operation,
                    new Config(
                            ""+System.currentTimeMillis(),
                            Utils.INSTANCE.makeXGC(Instant.now().toEpochMilli(), true),
                            Utils.INSTANCE.makeXGC(Instant.now().toEpochMilli(), true),
                            new Config.Software(info.getSoftwareName() != null ? info.getSoftwareName() : "iCure", info.getSoftwareVersion() != null ? info.getSoftwareVersion() : ICUREVERSION),
                            "",
                            "en",
                            Config.Format.KMEHR
                        )
                    );
            operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()));
            bos.close();
        } catch (Exception e) {
            operation.errorResponse(e);
        }
    }

	@Path("/generateSumehr")
	@WebSocketOperation(adapterClass = KmehrFileOperation.class)
	public void generateSumehr(@WebSocketParam("patientId") String patientId, @WebSocketParam("language") String language, @WebSocketParam("info") SumehrExportInfoDto info, KmehrFileOperation operation) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10000);
		try {
			sumehrLogicV1.createSumehr(bos, patientLogic.getPatient(patientId), info.getSecretForeignKeys(),
					healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()),
					mapper.map(info.getRecipient(), HealthcareParty.class), language, info.getComment(), info.getExcludedIds(), info.getIncludeIrrelevantInformation() == null ? false : info.getIncludeIrrelevantInformation(), operation, null, null,
                    new Config(
                            ""+System.currentTimeMillis(),
                            org.taktik.icure.be.ehealth.dto.kmehr.v20110701.Utils.INSTANCE.makeXGC(Instant.now().toEpochMilli(), true),
                            org.taktik.icure.be.ehealth.dto.kmehr.v20110701.Utils.INSTANCE.makeXGC(Instant.now().toEpochMilli(), true),
                            new Config.Software(info.getSoftwareName() != null ? info.getSoftwareName() : "iCure", info.getSoftwareVersion() != null ? info.getSoftwareVersion() : ICUREVERSION),
                            "",
                            "en",
                            Config.Format.SUMEHR
                    )
            );
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
			sumehrLogicV1.validateSumehr(bos, patientLogic.getPatient(patientId), info.getSecretForeignKeys(), healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()), mapper.map(info.getRecipient(), HealthcareParty.class), language, info.getComment(), info.getExcludedIds(), info.getIncludeIrrelevantInformation() == null ? false : info.getIncludeIrrelevantInformation(), operation, null, null,
                    new Config(
                            ""+System.currentTimeMillis(),
                            org.taktik.icure.be.ehealth.dto.kmehr.v20110701.Utils.INSTANCE.makeXGC(Instant.now().toEpochMilli(), true),
                            org.taktik.icure.be.ehealth.dto.kmehr.v20110701.Utils.INSTANCE.makeXGC(Instant.now().toEpochMilli(), true),
                            new Config.Software(info.getSoftwareName() != null ? info.getSoftwareName() : "iCure", info.getSoftwareVersion() != null ? info.getSoftwareVersion() : ICUREVERSION),
                            "",
                            "en",
                            Config.Format.SUMEHR
                    ));
			operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()));
			bos.close();
		} catch (Exception e) {
			operation.errorResponse(e);
		}
	}

	@Path("/generateSumehrV2")
	@WebSocketOperation(adapterClass = KmehrFileOperation.class)
	public void generateSumehrV2(@WebSocketParam("patientId") String patientId, @WebSocketParam("language") String language, @WebSocketParam("info") SumehrExportInfoDto info, KmehrFileOperation operation) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10000);
		try {
			sumehrLogicV2.createSumehr(bos, patientLogic.getPatient(patientId), info.getSecretForeignKeys(),
					healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()),
					mapper.map(info.getRecipient(), HealthcareParty.class), language, info.getComment(), info.getExcludedIds(), info.getIncludeIrrelevantInformation() == null ? false : info.getIncludeIrrelevantInformation(), operation, null, null,
                    new Config(
                            ""+System.currentTimeMillis(),
                            org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils.INSTANCE.makeXGC(Instant.now().toEpochMilli(), true),
                            org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils.INSTANCE.makeXGC(Instant.now().toEpochMilli(), true),
                            new Config.Software(info.getSoftwareName() != null ? info.getSoftwareName() : "iCure", info.getSoftwareVersion() != null ? info.getSoftwareVersion() : ICUREVERSION),
                            "",
                            "en",
                            Config.Format.SUMEHR
                    ));
			operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()));
			bos.close();
		} catch (Exception e) {
			operation.errorResponse(e);
		}
	}

	@Path("/generateSumehrV2JSON")
	@WebSocketOperation(adapterClass = KmehrFileOperation.class)
	public void generateSumehrV2JSON(@WebSocketParam("patientId") String patientId, @WebSocketParam("language") String language, @WebSocketParam("info") SumehrExportInfoDto info, @WebSocketParam("asJson") Boolean asJson, KmehrFileOperation operation) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10000);
		try {
			sumehrLogicV2.createSumehr(bos, patientLogic.getPatient(patientId), info.getSecretForeignKeys(),
					healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()),
					mapper.map(info.getRecipient(), HealthcareParty.class), language, info.getComment(), info.getExcludedIds(), info.getIncludeIrrelevantInformation() == null ? false : info.getIncludeIrrelevantInformation(), operation, null, null,
                    new Config(
                            ""+System.currentTimeMillis(),
                            org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils.INSTANCE.makeXGC(Instant.now().toEpochMilli(), true),
                            org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils.INSTANCE.makeXGC(Instant.now().toEpochMilli(), true),
                            new Config.Software(info.getSoftwareName() != null ? info.getSoftwareName() : "iCure", info.getSoftwareVersion() != null ? info.getSoftwareVersion() : ICUREVERSION),
                            "",
                            "en",
                            Config.Format.SUMEHR
                    ));
			operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()));
			bos.close();
		} catch (Exception e) {
			operation.errorResponse(e);
		}
	}

	@Path("/validateSumehrV2")
	@WebSocketOperation(adapterClass = KmehrFileOperation.class)
	public void validateSumehrV2(@WebSocketParam("patientId") String patientId, @WebSocketParam("language") String language, @WebSocketParam("info") SumehrExportInfoDto info, KmehrFileOperation operation) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10000);
		try {
			sumehrLogicV2.validateSumehr(bos, patientLogic.getPatient(patientId), info.getSecretForeignKeys(), healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()), mapper.map(info.getRecipient(), HealthcareParty.class), language, info.getComment(), info.getExcludedIds(), info.getIncludeIrrelevantInformation() == null ? false : info.getIncludeIrrelevantInformation(), operation,  null, null,
                    new Config(
                            ""+System.currentTimeMillis(),
                            org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils.INSTANCE.makeXGC(Instant.now().toEpochMilli(), true),
                            org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils.INSTANCE.makeXGC(Instant.now().toEpochMilli(), true),
                            new Config.Software(info.getSoftwareName() != null ? info.getSoftwareName() : "iCure", info.getSoftwareVersion() != null ? info.getSoftwareVersion() : ICUREVERSION),
                            "",
                            "en",
                            Config.Format.SUMEHR
                    ));
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
			softwareMedicalFileLogic.createSmfExport(bos, patientLogic.getPatient(patientId), info.getSecretForeignKeys(), healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()), language, operation, operation, info.getExportAsPMF());
			operation.binaryResponse(ByteBuffer.wrap(bos.toByteArray()));
			bos.close();
		} catch (Exception e) {
			operation.errorResponse(e);
		}
	}

	@Path("/generateMedicationScheme")
	@WebSocketOperation(adapterClass = KmehrFileOperation.class)
	public void generateMedicationSchemeExport(@WebSocketParam("patientId") String patientId , @WebSocketParam("language") String language, @WebSocketParam("info") MedicationSchemeExportInfoDto info,  @WebSocketParam("version") String recipientSafe,  @WebSocketParam("version") Integer version, KmehrFileOperation operation) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10000);
		try {
			medicationSchemeLogic.createMedicationSchemeExport(bos, patientLogic.getPatient(patientId), info.getSecretForeignKeys(),
					healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()),
					language, recipientSafe, version, operation, null);
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
	public void setSumehrLogicV1(SumehrLogic sumehrLogicV1) {
		this.sumehrLogicV1 = sumehrLogicV1;
	}

	@Autowired
	public void setSumehrLogicV2(SumehrLogic sumehrLogicV2) {
		this.sumehrLogicV2 = sumehrLogicV2;
	}

	@Autowired
    public void setDiaryNoteLogic(DiaryNoteLogic diaryNoteLogic) {this.diaryNoteLogic = diaryNoteLogic; }

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
