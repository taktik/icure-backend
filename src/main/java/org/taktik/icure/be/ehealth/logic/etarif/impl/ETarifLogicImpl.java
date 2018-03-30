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

package org.taktik.icure.be.ehealth.logic.etarif.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.ehealth.business.mycarenetcommons.builders.CommonBuilder;
import be.ehealth.business.mycarenetcommons.builders.RequestBuilderFactory;
import be.ehealth.business.mycarenetcommons.domain.Blob;
import be.ehealth.business.mycarenetcommons.domain.CareReceiverId;
import be.ehealth.business.mycarenetcommons.domain.CommonInput;
import be.ehealth.business.mycarenetcommons.domain.Routing;
import be.ehealth.business.mycarenetcommons.mapper.SendRequestMapper;
import be.ehealth.businessconnector.tarification.builder.RequestBuilder;
import be.ehealth.businessconnector.tarification.builder.TarificationRequestBuilderFactory;
import be.ehealth.businessconnector.tarification.session.TarificationSessionService;
import be.ehealth.businessconnector.tarification.session.TarificationSessionServiceFactory;
import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.config.Configuration;
import be.ehealth.technicalconnector.config.util.ConfigUtil;
import be.ehealth.technicalconnector.config.util.domain.PackageInfo;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.idgenerator.IdGeneratorFactory;
import be.ehealth.technicalconnector.utils.MarshallerHelper;
import be.fgov.ehealth.messageservices.core.v1.PatientType;
import be.fgov.ehealth.messageservices.core.v1.RequestType;
import be.fgov.ehealth.messageservices.core.v1.RetrieveTransactionRequest;
import be.fgov.ehealth.messageservices.core.v1.RetrieveTransactionResponse;
import be.fgov.ehealth.messageservices.core.v1.SelectRetrieveTransactionType;
import be.fgov.ehealth.messageservices.core.v1.TransactionType;
import be.fgov.ehealth.mycarenet.commons.core.v2.BlobType;
import be.fgov.ehealth.mycarenet.commons.protocol.v2.TarificationConsultationRequest;
import be.fgov.ehealth.mycarenet.commons.protocol.v2.TarificationConsultationResponse;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDERRORMYCARENET;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEM;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ErrorMyCarenetType;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.dto.etarif.TarificationConsultationResult;
import org.taktik.icure.be.ehealth.logic.etarif.ETarifLogic;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.be.ehealth.dto.Error;


@org.springframework.stereotype.Service
public class ETarifLogicImpl implements ETarifLogic {

	private SessionLogic sessionLogic;
	private HealthcarePartyLogic healthcarePartyLogic;

	private static List<String> expectedProps = new ArrayList<>();
	private static Configuration config = ConfigFactory.getConfigValidator(expectedProps);

	@Override
	public TarificationConsultationResult consultTarif(String token, String ssin, List<String> codes, String justification) throws TokenNotAvailableException {
		return consultTarif(token, ssin, codes, justification, new Date());
	}

	@Override
	public TarificationConsultationResult consultTarif(String token, String ssin, List<String> codes, String justification, Date encounterDateTime) throws TokenNotAvailableException {
		return consultTarif(token, ssin, codes, justification, encounterDateTime, null);
	}

	@Override
	public TarificationConsultationResult consultTarif(String token, String patientSsin, List<String> codes, String justification, Date encounterDate, String nihiiDmg) throws TokenNotAvailableException {
		try {
			assert patientSsin != null || codes != null && codes.size() > 0;
			if (token == null) {
				throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
			}

			RequestBuilder requestBuilder = TarificationRequestBuilderFactory.getRequestObjectBuilder();

			DateTime encounterDateTime= new DateTime(encounterDate.getTime(), DateTimeZone.getDefault());
			DateTime dateTime = new DateTime().withMillisOfSecond(0).withZone(null);
			String kmehrUUID = dateTime.toString("YYYYddhhmmssSS");

			RetrieveTransactionRequest rtr = new RetrieveTransactionRequest();

			RequestType request = new RequestType();
			SelectRetrieveTransactionType select = new SelectRetrieveTransactionType();

			IDKMEHR id = new IDKMEHR();
			id.setS(IDKMEHRschemes.ID_KMEHR);
			id.setSV("1.0");

			HealthcareParty dr = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId());

			String nihii = dr.getNihii();
			String ssin = dr.getSsin();

			id.setValue(""+ nihii +"."+ kmehrUUID);

			request.setId(id);

			AuthorType author = new AuthorType();
			HcpartyType hcparty = new HcpartyType();

			IDHCPARTY nihiiIdhcparty = new IDHCPARTY();
			nihiiIdhcparty.setS(IDHCPARTYschemes.ID_HCPARTY);
			nihiiIdhcparty.setSV("1.0");
			nihiiIdhcparty.setValue(nihii);

			IDHCPARTY ssinIdhcparty = new IDHCPARTY();
			ssinIdhcparty.setS(IDHCPARTYschemes.INSS);
			ssinIdhcparty.setSV("1.0");
			ssinIdhcparty.setValue(ssin);

			hcparty.getIds().add(nihiiIdhcparty);
			hcparty.getIds().add(ssinIdhcparty);

			CDHCPARTY cdhcparty = new CDHCPARTY();
			cdhcparty.setS(CDHCPARTYschemes.CD_HCPARTY);
			cdhcparty.setSV("1.3");
			cdhcparty.setValue("persphysician");

			hcparty.getCds().add(cdhcparty);

			hcparty.setFirstname(dr.getFirstName());
			hcparty.setFamilyname(dr.getLastName());

			author.getHcparties().add(hcparty);

			request.setAuthor(author);
			request.setDate(dateTime);
			request.setTime(dateTime);

			PatientType patientType = new PatientType();

			IDPATIENT idpatient = new IDPATIENT();
			idpatient.setS(IDPATIENTschemes.ID_PATIENT);
			idpatient.setSV("1.0");
			idpatient.setValue(patientSsin);

			patientType.getIds().add(idpatient);

			select.setPatient(patientType);

			TransactionType transaction = new TransactionType();

			CDTRANSACTION cdtransaction = new CDTRANSACTION();
			cdtransaction.setS(CDTRANSACTIONschemes.CD_TRANSACTION_MYCARENET);
			cdtransaction.setSV("1.1");
			cdtransaction.setValue("tariff");

			transaction.getCds().add(cdtransaction);
			transaction.setAuthor(author);

			ItemType encounterDateTimeItemType = new ItemType();
			IDKMEHR id1 = new IDKMEHR();
			id1.setS(IDKMEHRschemes.ID_KMEHR);
			id1.setSV("1.0");
			id1.setValue("1");

			encounterDateTimeItemType.getIds().add(id1);

			CDITEM cd1 = new CDITEM();
			cd1.setS(CDITEMschemes.CD_ITEM);
			cd1.setSV("1.0");
			cd1.setValue("encounterdatetime");

			encounterDateTimeItemType.getCds().add(cd1);

			ContentType encounterDateTimeContent = new ContentType();
			encounterDateTimeContent.setDate(new DateTime(encounterDate.getTime()));
			encounterDateTimeItemType.getContents().add(encounterDateTimeContent);
			transaction.getHeadingsAndItemsAndTexts().add(encounterDateTimeItemType);

			for (String code : codes) {
				ItemType claim = new ItemType();

				IDKMEHR id2 = new IDKMEHR();
				id2.setS(IDKMEHRschemes.ID_KMEHR);
				id2.setSV("1.0");
				id2.setValue("2");

				claim.getIds().add(id2);

				CDITEM cd2 = new CDITEM();
				cd2.setS(CDITEMschemes.CD_ITEM);
				cd2.setSV("1.0");
				cd2.setValue("claim");

				claim.getCds().add(cd2);

				ContentType claimContent = new ContentType();

				CDCONTENT cdcontent = new CDCONTENT();
				cdcontent.setS(CDCONTENTschemes.CD_NIHDI);
				cdcontent.setSV("1.0");
				cdcontent.setValue(code);
				claimContent.getCds().add(cdcontent);
				claim.getContents().add(claimContent);
				transaction.getHeadingsAndItemsAndTexts().add(claim);
			}

			if (justification != null) {
				ItemType justif = new ItemType();

				IDKMEHR id3 = new IDKMEHR();
				id3.setS(IDKMEHRschemes.ID_KMEHR);
				id3.setSV("1.0");
				id3.setValue("3");

				justif.getIds().add(id3);

				CDITEM cd3 = new CDITEM();
				cd3.setS(CDITEMschemes.CD_ITEM);
				cd3.setSV("1.0");
				cd3.setValue("justification");

				justif.getCds().add(cd3);

				ContentType justifContent = new ContentType();

				CDCONTENT cdcontent = new CDCONTENT();
				cdcontent.setS(CDCONTENTschemes.CD_MYCARENET_JUSTIFICATION);
				cdcontent.setSV("1.0");
				cdcontent.setValue(justification);
				justifContent.getCds().add(cdcontent);

				justif.getContents().add(justifContent);
				transaction.getHeadingsAndItemsAndTexts().add(justif);
			}

			// DMG

			if (nihiiDmg != null) {
				ItemType dmg = new ItemType();

				IDKMEHR id4 = new IDKMEHR();
				id4.setS(IDKMEHRschemes.ID_KMEHR);
				id4.setSV("1.0");
				id4.setValue("4");
				dmg.getIds().add(id4);

				CDITEM cd4 = new CDITEM();
				cd4.setS(CDITEMschemes.CD_ITEM);
				cd4.setSV("1.0");
				cd4.setValue("gmdmanager");
				dmg.getCds().add(cd4);

				HcpartyType dmgHcparty = new HcpartyType();

				IDHCPARTY hcpartyId = new IDHCPARTY();
				hcpartyId.setS(IDHCPARTYschemes.ID_HCPARTY);
				hcpartyId.setSV("1.0");
				hcpartyId.setValue(nihiiDmg);

				dmgHcparty.getIds().add(hcpartyId);
				dmgHcparty.getCds().add(cdhcparty);


				ContentType content = new ContentType();
				content.setHcparty(dmgHcparty);

				dmg.getContents().add(content);
				transaction.getHeadingsAndItemsAndTexts().add(dmg);
			}

			//

			select.setTransaction(transaction);

			rtr.setRequest(request);
			rtr.setSelect(select);

			String inputReference = IdGeneratorFactory.getIdGenerator("xsid").generateId();

			MarshallerHelper<RetrieveTransactionRequest,RetrieveTransactionRequest>  rtrHelper = new MarshallerHelper<>(RetrieveTransactionRequest.class, RetrieveTransactionRequest.class);

			// there are 2 ways to create the request
			// FIRST : with minimal input , only a minimal input is needed.
			// create routing element : this will determine where to send the message , ( see documentation mycarenet and javadoc )
			// Retrieve patient info from eid card, or create the Patient object in your own code

			String rtrText = rtrHelper.toString(rtr);
			byte[] rtrBytes = rtrText.replaceAll("(<ns.:time>)([0-9]{2}:[0-9]{2}:[0-9]{2})\\+[0-9][0-9]?:[0-9][0-9]?(</ns.:time>)", "$1$2$3").getBytes("UTF8");

			Boolean isTest = config.getProperty("endpoint.mcn.tarification").contains("-acpt");

			CommonBuilder commonBuilder = RequestBuilderFactory.getCommonBuilder("mcn.tarification");
			PackageInfo packageInfo = ConfigUtil.retrievePackageInfo("mcn.tarification");

			CommonInput commonInput = commonBuilder.createCommonInput(packageInfo, isTest, inputReference);
			TarificationConsultationRequest req = requestBuilder.buildConsultationRequest(commonInput, new Routing(new CareReceiverId(patientSsin), dateTime), RequestBuilderFactory.getBlobBuilder("mcn.tarification").build(rtrBytes));

			req.getCommonInput().setInputReference(kmehrUUID);
			req.getRouting().setReferenceDate(encounterDateTime);

			TarificationSessionService session = TarificationSessionServiceFactory.getTarificationSession();
			TarificationConsultationResponse consultTarificationResponse = session.consultTarification(req);

			// there are utility methods , preconfigured for tarification to help extract the business response
			BlobType detail = consultTarificationResponse.getReturn().getDetail();
			// the blobtype can be mapped to a common domain class for the connector
			Blob blob = SendRequestMapper.mapBlobTypeToBlob(detail);
			// you can check the validity of the blob and retrieve the content ( deflated and decoded if needed )
			byte[] content = RequestBuilderFactory.getBlobBuilder("mcn.tarification").checkAndRetrieveContent(blob);

			MarshallerHelper<RetrieveTransactionResponse, RetrieveTransactionResponse> helper = new MarshallerHelper<>(RetrieveTransactionResponse.class, RetrieveTransactionResponse.class);
			RetrieveTransactionResponse commonInputResponse = helper.toObject(content);

			TarificationConsultationResult result = new TarificationConsultationResult();

			List<ErrorMyCarenetType> errorsMyCarenetType = commonInputResponse.getAcknowledge().getErrors();
			for (ErrorMyCarenetType errorMyCarenetType : errorsMyCarenetType) {
				Error error = new Error();
				StringBuilder errorCodes = null;
				for (CDERRORMYCARENET errorCode : errorMyCarenetType.getCds()) {
					if (errorCodes != null) {
						errorCodes.append(" ").append(errorCode.getValue());
					} else {
						errorCodes = new StringBuilder(errorCode.getValue());
					}
				}
				if (errorCodes != null) {
					error.setCode(errorCodes.toString());
					error.setDescr(errorMyCarenetType.getDescription().getValue());
					result.getErrors().add(error);
				}
			}

			Kmehrmessage kmehrmessage = commonInputResponse.getKmehrmessage();
			if (kmehrmessage != null && kmehrmessage.getFolders() != null && kmehrmessage.getFolders().size() > 0) {
				result.setNiss(patientSsin);

				FolderType folder = kmehrmessage.getFolders().get(0);
				if (folder.getPatient() != null) {
					result.fill(folder.getPatient());
				}
				if (folder.getTransactions() != null) {
					result.fill(folder.getTransactions());
				}
			}

			return result;

		} catch (ConnectorException | UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	@Autowired
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Autowired
	public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
		this.healthcarePartyLogic = healthcarePartyLogic;
	}
}
