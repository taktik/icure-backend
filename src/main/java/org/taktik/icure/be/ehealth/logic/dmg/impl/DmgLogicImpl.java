/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth.logic.dmg.impl;

import be.cin.mycarenet.esb.common.v2.CareProviderType;
import be.cin.mycarenet.esb.common.v2.CommonInput;
import be.cin.mycarenet.esb.common.v2.IdType;
import be.cin.mycarenet.esb.common.v2.LicenseType;
import be.cin.mycarenet.esb.common.v2.NihiiType;
import be.cin.mycarenet.esb.common.v2.OrigineType;
import be.cin.mycarenet.esb.common.v2.PackageType;
import be.cin.mycarenet.esb.common.v2.ValueRefString;
import be.cin.nip.async.generic.Get;
import be.cin.nip.async.generic.GetResponse;
import be.cin.nip.async.generic.MsgQuery;
import be.cin.nip.async.generic.MsgResponse;
import be.cin.nip.async.generic.Post;
import be.cin.nip.async.generic.PostResponse;
import be.cin.nip.async.generic.Query;
import be.cin.nip.async.generic.TAck;
import be.cin.nip.async.generic.TAckResponse;
import be.cin.nip.sync.reg.v1.RegistrationStatus;
import be.cin.nip.sync.reg.v1.RegistrationsAnswer;
import be.cin.types.v1.DetailType;
import be.ehealth.business.common.domain.Patient;
import be.ehealth.business.kmehrcommons.HcPartyUtil;
import be.ehealth.business.kmehrcommons.builders.HcPartyBuilder;
import be.ehealth.business.mycarenetcommons.builders.BlobBuilder;
import be.ehealth.business.mycarenetcommons.builders.BlobBuilderFactory;
import be.ehealth.business.mycarenetcommons.builders.CommonBuilder;
import be.ehealth.business.mycarenetcommons.builders.RequestBuilderFactory;
import be.ehealth.business.mycarenetcommons.domain.Blob;
import be.ehealth.business.mycarenetcommons.domain.CareReceiverId;
import be.ehealth.business.mycarenetcommons.domain.Routing;
import be.ehealth.business.mycarenetcommons.mapper.SendRequestMapper;
import be.ehealth.business.mycarenetcommons.util.WsAddressingUtil;
import be.ehealth.businessconnector.dmg.builders.RequestObjectBuilder;
import be.ehealth.businessconnector.dmg.builders.RequestObjectBuilderFactory;
import be.ehealth.businessconnector.dmg.builders.ResponseObjectBuilder;
import be.ehealth.businessconnector.dmg.builders.ResponseObjectBuilderFactory;
import be.ehealth.businessconnector.dmg.domain.DMGReferences;
import be.ehealth.businessconnector.dmg.domain.DmgBuilderResponse;
import be.ehealth.businessconnector.dmg.exception.DmgBusinessConnectorException;
import be.ehealth.businessconnector.dmg.exception.DmgBusinessConnectorExceptionValues;
import be.ehealth.businessconnector.dmg.mappers.CommonInputMapper;
import be.ehealth.businessconnector.dmg.mappers.RequestObjectMapper;
import be.ehealth.businessconnector.dmg.mappers.RoutingMapper;
import be.ehealth.businessconnector.dmg.session.DmgService;
import be.ehealth.businessconnector.dmg.session.DmgSessionServiceFactory;
import be.ehealth.businessconnector.dmg.util.DmgConstants;
import be.ehealth.businessconnector.dmg.validators.impl.DmgXmlValidatorImpl;
import be.ehealth.businessconnector.genericasync.builders.BuilderFactory;
import be.ehealth.businessconnector.registration.builder.RegistrationRequestBuilderFactory;
import be.ehealth.businessconnector.registration.builder.RequestBuilder;
import be.ehealth.businessconnector.registration.helper.ResponseHelper;
import be.ehealth.businessconnector.registration.session.RegistrationSession;
import be.ehealth.businessconnector.registration.session.RegistrationSessionFactory;
import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.config.Configuration;
import be.ehealth.technicalconnector.config.util.ConfigUtil;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.handler.domain.WsAddressingHeader;
import be.ehealth.technicalconnector.idgenerator.IdGeneratorFactory;
import be.ehealth.technicalconnector.session.Session;
import be.ehealth.technicalconnector.utils.ConnectorXmlUtils;
import be.ehealth.technicalconnector.utils.MarshallerHelper;
import be.fgov.ehealth.globalmedicalfile.core.v1.BlobType;
import be.fgov.ehealth.globalmedicalfile.protocol.v1.ConsultGlobalMedicalFileRequest;
import be.fgov.ehealth.globalmedicalfile.protocol.v1.NotifyGlobalMedicalFileRequest;
import be.fgov.ehealth.globalmedicalfile.protocol.v1.SendRequestType;
import be.fgov.ehealth.globalmedicalfile.protocol.v1.SendResponseType;
import be.fgov.ehealth.messageservices.core.v1.AcknowledgeType;
import be.fgov.ehealth.messageservices.core.v1.PatientType;
import be.fgov.ehealth.messageservices.core.v1.RequestType;
import be.fgov.ehealth.messageservices.core.v1.RetrieveTransactionRequest;
import be.fgov.ehealth.messageservices.core.v1.RetrieveTransactionResponse;
import be.fgov.ehealth.messageservices.core.v1.SelectRetrieveTransaction;
import be.fgov.ehealth.messageservices.core.v1.SelectRetrieveTransactionType;
import be.fgov.ehealth.messageservices.core.v1.SendTransactionRequest;
import be.fgov.ehealth.messageservices.core.v1.SendTransactionResponse;
import be.fgov.ehealth.messageservices.core.v1.TransactionType;
import be.fgov.ehealth.mycarenet.registration.protocol.v1.RegisterToMycarenetServiceRequest;
import be.fgov.ehealth.mycarenet.registration.protocol.v1.RegisterToMycarenetServiceResponse;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDERRORMYCARENET;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEM;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDSEX;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDSEXvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDSTANDARD;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDINSURANCE;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ErrorMyCarenetType;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HeaderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.MemberinsuranceType;
import be.fgov.ehealth.standards.kmehr.schema.v1.PersonType;
import be.fgov.ehealth.standards.kmehr.schema.v1.RecipientType;
import be.fgov.ehealth.standards.kmehr.schema.v1.SenderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.SexType;
import be.fgov.ehealth.standards.kmehr.schema.v1.StandardType;
import be.fgov.ehealth.technicalconnector.signature.AdvancedElectronicSignatureEnumeration;
import be.fgov.ehealth.technicalconnector.signature.SignatureBuilderFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.dto.Error;
import org.taktik.icure.be.ehealth.dto.dmg.DmgAcknowledge;
import org.taktik.icure.be.ehealth.dto.dmg.DmgClosure;
import org.taktik.icure.be.ehealth.dto.dmg.DmgConsultation;
import org.taktik.icure.be.ehealth.dto.dmg.DmgExtension;
import org.taktik.icure.be.ehealth.dto.dmg.DmgInscription;
import org.taktik.icure.be.ehealth.dto.dmg.DmgMessage;
import org.taktik.icure.be.ehealth.dto.dmg.DmgMessageWithPatient;
import org.taktik.icure.be.ehealth.dto.dmg.DmgNotification;
import org.taktik.icure.be.ehealth.dto.dmg.DmgRegistration;
import org.taktik.icure.be.ehealth.dto.dmg.DmgsList;
import org.taktik.icure.be.ehealth.logic.EhealthLogic;
import org.taktik.icure.be.ehealth.logic.dmg.DmgLogic;
import org.taktik.icure.be.ehealth.logic.messages.AbstractMessage;
import org.taktik.icure.be.ehealth.logic.messages.ErrorWarningMessages;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.entities.Document;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Message;
import org.taktik.icure.entities.User;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.DocumentLogic;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.MessageLogic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.services.external.rest.handlers.GsonMessageBodyHandler;
import org.w3._2005._05.xmlmime.Base64Binary;

import javax.security.auth.login.LoginException;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateExpiredException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

@org.springframework.stereotype.Service
public class DmgLogicImpl implements DmgLogic {
	private final Log log = LogFactory.getLog(this.getClass());
	private IDGenerator idg = new UUIDGenerator();
	private Gson gson = new GsonMessageBodyHandler().getGson();
	private static List<String> expectedProps = new ArrayList<>();
	private static Configuration config = ConfigFactory.getConfigValidator(expectedProps);

	private MapperFacade mapper;
	private SessionLogic sessionLogic;
	private HealthcarePartyLogic healthcarePartyLogic;
	private DocumentLogic documentLogic;
	private MessageLogic messageLogic;
	private PatientLogic patientLogic;

	private Map<String, AbstractMessage> errorMessages = new HashMap<>();

	@Autowired
	public void setPatientLogic(PatientLogic patientLogic) {
		this.patientLogic = patientLogic;
	}

	@Autowired
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Autowired
	public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
		this.healthcarePartyLogic = healthcarePartyLogic;
	}

	@Autowired
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

	@Autowired
	public void setDocumentLogic(DocumentLogic documentLogic) {
		this.documentLogic = documentLogic;
	}

	@Autowired
	public void setMessageLogic(MessageLogic messageLogic) {
		this.messageLogic = messageLogic;
	}

	public DmgLogicImpl() {
		InputStream errors = EhealthLogic.class.getResourceAsStream("ErrorCodes.xml");
		for (AbstractMessage am : ErrorWarningMessages.parse(errors, "CINNIC", abstractMessage -> abstractMessage.getSubContext() == null || abstractMessage.getSubContext().length() == 0)) {
			errorMessages.put(am.getSubContext() + ":" + am.getCode(), am);
		}
		errors = EhealthLogic.class.getResourceAsStream("ErrorCodes.xml");
		for (AbstractMessage am : ErrorWarningMessages.parse(errors, "SOA")) {
			errorMessages.put(am.getSubContext() + ":" + am.getCode(), am);
		}
	}

	private AuthorType getAuthor(String nihii, String ssin, String firstname, String lastname) throws TechnicalConnectorException {
		AuthorType author = new AuthorType();

		author.getHcparties().add(new HcPartyBuilder().idHcPartyId(nihii, "1.0").inssId(ssin, "1.0").cdHcPartyCd("persphysician", "1.0").firstname(firstname).lastname(lastname).build());
		return author;
	}

	private NotifyGlobalMedicalFileRequest buildSendNotifyRequest(boolean isTest, HealthcareParty hcp, DMGReferences references, Patient patientInfo, DateTime referenceDate, Kmehrmessage msg) throws TechnicalConnectorException, DmgBusinessConnectorException, InstantiationException {
		SendTransactionRequest request = new SendTransactionRequest();
		request.setRequest(this.generatedReq(references, hcp));
		request.setKmehrmessage(msg);
		MarshallerHelper<SendTransactionRequest, SendTransactionRequest> kmehrRequestMarshaller = new MarshallerHelper<>(SendTransactionRequest.class, SendTransactionRequest.class);
		byte[] xmlByteArray = kmehrRequestMarshaller.toXMLByteArray(request);
		log.debug("RequestObjectBuilder : created blob content: " + new String(xmlByteArray));

		Blob blob = BlobBuilderFactory.getBlobBuilder("dmg").build(xmlByteArray, "none", "_" + references.getBlobId(), "text/xml");
		blob.setMessageName("GMD-CONSULT-HCP");

		RequestObjectBuilder reqBuilder = RequestObjectBuilderFactory.getRequestObjectBuilder();
		return reqBuilder.buildSendNotifyRequest(isTest, references.getInputReference(), patientInfo, referenceDate, blob, ArrayUtils.EMPTY_BYTE_ARRAY);
	}


	private RequestType generatedReq(DMGReferences references, HealthcareParty hcp) throws TechnicalConnectorException {
		RequestType req = new RequestType();
		IDKMEHR dmgId = new IDKMEHR();
		dmgId.setS(IDKMEHRschemes.ID_KMEHR);
		dmgId.setSV("1.0");
		dmgId.setValue(hcp.getNihii() + "." + references.getKmehrIdSuffix());

		req.setId(dmgId);
		req.setAuthor(getAuthor(hcp.getNihii(), hcp.getSsin(), hcp.getFirstName(), hcp.getLastName()));
		req.setDate(new DateTime());
		req.setTime(new DateTime());
		return req;
	}

	private <T extends SendRequestType> T fillSendRequest(T sendRequestT, boolean isTest, String referenceId, Patient patientInfo, DateTime referenceDate, Blob blob, byte[] xades, boolean generatedXades) throws TechnicalConnectorException, DmgBusinessConnectorException {
		CommonBuilder cb = RequestBuilderFactory.getCommonBuilder("dmg");
		this.checkInputParameters(referenceId, patientInfo, referenceDate, blob);
		sendRequestT.setCommonInput(CommonInputMapper.mapCommonInputType(cb.createCommonInput(ConfigUtil.retrievePackageInfo("dmg"), isTest, referenceId)));
		sendRequestT.setRouting(RoutingMapper.mapRoutingType(cb.createRouting(patientInfo, referenceDate)));
		sendRequestT.setDetail(RequestObjectMapper.mapBlobTypefromBlob(blob));
		this.setXades(sendRequestT, xades, generatedXades);
		return sendRequestT;
	}

	public ConsultGlobalMedicalFileRequest buildSendConsultRequest(boolean isTest, DMGReferences references, HealthcareParty hcp, Patient patientInfo, DateTime referenceDate, SelectRetrieveTransaction request) throws TechnicalConnectorException, DmgBusinessConnectorException, InstantiationException {
		ConsultGlobalMedicalFileRequest result = new ConsultGlobalMedicalFileRequest();
		RetrieveTransactionRequest req = new RetrieveTransactionRequest();
		req.setRequest(this.generatedReq(references, hcp));
		req.setSelect(request);
		MarshallerHelper<RetrieveTransactionRequest, RetrieveTransactionRequest> kmehrRequestMarshaller = new MarshallerHelper<>(RetrieveTransactionRequest.class, RetrieveTransactionRequest.class);
		byte[] xmlByteArray = kmehrRequestMarshaller.toXMLByteArray(req);
		if (xmlByteArray != null && config.getBooleanProperty("be.ehealth.businessconnector.dmg.builders.impl.dumpMessages", false)) {
			log.debug("RequestObjectBuilder : created blob content: " + new String(xmlByteArray));
		}

		Blob blob = BlobBuilderFactory.getBlobBuilder("dmg").build(xmlByteArray, "none", "_" + references.getBlobId(), "text/xml");
		blob.setMessageName("GMD-CONSULT-HCP");
		result = this.fillSendRequest(result, isTest, references.getInputReference(), patientInfo, referenceDate, blob, ArrayUtils.EMPTY_BYTE_ARRAY, false);
		DmgXmlValidatorImpl validator = new DmgXmlValidatorImpl();
		validator.validate(result);
		return result;
	}

	private <T extends SendRequestType> void setXades(T sendRequestT, byte[] xades, boolean generatedXades) throws TechnicalConnectorException {
		byte[] xadesValue;
		if (ArrayUtils.isEmpty(xades) && generatedXades) {
			HashMap<String, Object> value = new HashMap<>();
			value.put("baseURI", sendRequestT.getDetail().getId());
			ArrayList<String> transformList = new ArrayList<>();
			transformList.add("http://www.w3.org/2000/09/xmldsig#base64");
			value.put("tranformerList", transformList);
			xadesValue = SignatureBuilderFactory.getSignatureBuilder(AdvancedElectronicSignatureEnumeration.XAdES).sign(Session.getInstance().getSession().getEncryptionCredential(), ConnectorXmlUtils.toByteArray(sendRequestT), value);
		} else {
			xadesValue = ArrayUtils.clone(xades);
		}

		if (!ArrayUtils.isEmpty(xadesValue)) {
			Base64Binary value1 = new Base64Binary();
			value1.setValue(xadesValue);
			value1.setContentType("text/xml");
			sendRequestT.setXadesT(value1);
		}
	}

	private void checkInputParameters(String referenceId, Patient patientInfo, DateTime referenceDate, Blob blob) throws DmgBusinessConnectorException {
		this.checkParameterNotNull(referenceId, "DmgReferences");
		if (blob != null && blob.getContent() != null && blob.getContent().length != 0) {
			this.checkStringParameterNotNullOrEmpty(blob.getContentType(), "Blob contentType");
			this.checkStringParameterNotNullOrEmpty(blob.getId(), "Blob id");
			this.checkParameterNotNull(referenceDate, "Reference date");
			this.checkParameterNotNull(patientInfo, "Patient info");
			if (patientInfo.getInss() == null || patientInfo.getInss().isEmpty()) {
				if (patientInfo.getMutuality() == null || patientInfo.getMutuality().isEmpty()) {
					throw new DmgBusinessConnectorException(DmgBusinessConnectorExceptionValues.PARAMETER_NULL, "Ssin and mutuality (No valid patient information)");
				}

				if (patientInfo.getRegNrWithMut() == null || patientInfo.getRegNrWithMut().isEmpty()) {
					throw new DmgBusinessConnectorException(DmgBusinessConnectorExceptionValues.PARAMETER_NULL, "Ssin and registration number (No valid patient information)");
				}
			}

		} else {
			throw new DmgBusinessConnectorException(DmgBusinessConnectorExceptionValues.PARAMETER_NULL, "Blob Content");
		}
	}

	private void checkStringParameterNotNullOrEmpty(String contentType, String parameterName) throws DmgBusinessConnectorException {
		if (contentType == null || contentType.isEmpty()) {
			throw new DmgBusinessConnectorException(DmgBusinessConnectorExceptionValues.PARAMETER_NULL, parameterName);
		}
	}

	private void checkParameterNotNull(Object references, String parameterName) throws DmgBusinessConnectorException {
		if (references == null) {
			throw new DmgBusinessConnectorException(DmgBusinessConnectorExceptionValues.PARAMETER_NULL, parameterName);
		}
	}


	@Override
	public DmgNotification notifyDmg(String token, String patientNiss, String mutuality, String regNrWithMut, String firstName, String lastName, String gender, String nomenclature, Date requestDate) throws ConnectorException, InstantiationException, DataFormatException, NoSuchAlgorithmException, TokenNotAvailableException {
		DmgService service;
		DateTime now = new DateTime();
		now = now.withMillisOfSecond(0);

		assert patientNiss != null || mutuality != null && regNrWithMut != null;
		if (token == null) {
			throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
		}

		String healthcarePartyId = sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId();
		HealthcareParty hcp = healthcarePartyLogic.getHealthcareParty(healthcarePartyId);

		// DMGReferences ref = DmgTestUtils.createDmgReferenceForTest();
		DMGReferences ref = new DMGReferences(true);

		Patient pI = new Patient();
		pI.setInss(patientNiss);
		pI.setMutuality(mutuality);
		pI.setRegNrWithMut(regNrWithMut);

		DateTime dateReference = new DateTime();

		Kmehrmessage request = new Kmehrmessage();

		PersonType patient = new PersonType();
		addNissToPatientIds(patientNiss, patient.getIds());

		patient.setFamilyname(lastName);
		patient.getFirstnames().add(firstName);

		if (pI.getRegNrWithMut() != null) {
			MemberinsuranceType member = new MemberinsuranceType();
			member.setMembership(pI.getRegNrWithMut());
			setMemberId(pI, member);
			patient.setInsurancymembership(member);
		}
		if (gender != null) {
			SexType sexType = new SexType();

			CDSEX cdsex = new CDSEX();
			cdsex.setSV("1.0");
			cdsex.setS("CD-SEX");
			gender = gender.toLowerCase();
			cdsex.setValue(
					gender.equals("f") ? CDSEXvalues.FEMALE :
							gender.equals("m") ? CDSEXvalues.MALE :
									gender.equals("c") ? CDSEXvalues.CHANGED : CDSEXvalues.UNKNOWN
			);
			sexType.setCd(cdsex);

			patient.setSex(sexType);
		}

		request.getFolders().add(new FolderType());
		IDKMEHR idf = new IDKMEHR();
		idf.setS(IDKMEHRschemes.ID_KMEHR);
		idf.setValue("1");
		idf.setSV("1.0");

		request.getFolders().get(0).getIds().add(idf);
		request.getFolders().get(0).setPatient(patient);

		be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType transaction = new be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType();
		request.getFolders().get(0).getTransactions().add(transaction);

		AuthorType author = getAuthor(hcp.getNihii(), hcp.getSsin(), hcp.getFirstName(), hcp.getLastName());
		request.getFolders().get(0).getTransactions().get(0).setAuthor(author);

		HeaderType header = new HeaderType();
		header.setSender(new SenderType());
		header.getSender().getHcparties().addAll(author.getHcparties());

		StandardType standard = new StandardType();
		CDSTANDARD cdstandard = new CDSTANDARD();
		cdstandard.setS("CD-STANDARD");
		cdstandard.setSV("1.8");
		cdstandard.setValue("20131001");
		standard.setCd(cdstandard);
		header.setStandard(standard);

		IDKMEHR idkmehr = new IDKMEHR();
		idkmehr.setS(IDKMEHRschemes.ID_KMEHR);
		idkmehr.setValue("1");
		idkmehr.setSV("1.0");

		header.setDate(now);
		header.setTime(now);
		header.getIds().add(idkmehr);

		RecipientType recipient = new RecipientType();

		HcpartyType application = new HcpartyType();

		application.setName("mycarenet");
		CDHCPARTY cdhcparty = new CDHCPARTY();
		cdhcparty.setS(CDHCPARTYschemes.CD_HCPARTY);
		cdhcparty.setSV("1.0");
		cdhcparty.setValue("application");
		application.getCds().add(cdhcparty);

		recipient.getHcparties().add(application);

		header.getRecipients().add(recipient);

		request.setHeader(header);

		CDTRANSACTION cdtransaction = new CDTRANSACTION();
		cdtransaction.setSV("1.0");
		cdtransaction.setValue("gmd");
		cdtransaction.setS(CDTRANSACTIONschemes.CD_TRANSACTION_MYCARENET);
		IDKMEHR idt = new IDKMEHR();
		idt.setS(IDKMEHRschemes.ID_KMEHR);
		idt.setValue("1");
		idt.setSV("1.0");
		transaction.getIds().add(idt);
		transaction.getCds().add(cdtransaction);

		transaction.setDate(now);
		transaction.setTime(now);
		transaction.setAuthor(author);
		transaction.setIscomplete(true);
		transaction.setIsvalidated(true);

		ItemType gmdManagerItem = new ItemType();

		CDITEM cditem = new CDITEM();
		cditem.setS(CDITEMschemes.CD_ITEM);
		cditem.setSV("1.0");
		cditem.setValue("gmdmanager");

		gmdManagerItem.getCds().add(cditem);

		IDKMEHR idkmehr1 = new IDKMEHR();
		idkmehr1.setS(IDKMEHRschemes.ID_KMEHR);
		idkmehr1.setValue("1");
		idkmehr1.setSV("1.0");
		gmdManagerItem.getIds().add(idkmehr1);

		ContentType gmdManagerContent = new ContentType();
		gmdManagerContent.setHcparty(author.getHcparties().get(0));
		gmdManagerItem.getContents().add(gmdManagerContent);

		transaction.getItem().add(gmdManagerItem);

		ItemType encounterItem = new ItemType();
		DateTime requestDateTime = new DateTime(requestDate.getTime());

		cditem = new CDITEM();
		cditem.setS(CDITEMschemes.CD_ITEM);
		cditem.setSV("1.0");
		cditem.setValue("encounterdatetime");
		encounterItem.getCds().add(cditem);

		IDKMEHR idkmehr2 = new IDKMEHR();
		idkmehr2.setS(IDKMEHRschemes.ID_KMEHR);
		idkmehr2.setValue("2");
		idkmehr2.setSV("1.0");
		encounterItem.getIds().add(idkmehr2);


		ContentType encounterContent = new ContentType();
		encounterContent.setDate(requestDateTime);
		encounterItem.getContents().add(encounterContent);

		transaction.getItem().add(encounterItem);

		ItemType claimItem = new ItemType();

		cditem = new CDITEM();
		cditem.setS(CDITEMschemes.CD_ITEM);
		cditem.setSV("1.0");
		cditem.setValue("claim");
		claimItem.getCds().add(cditem);

		IDKMEHR idkmehr3 = new IDKMEHR();
		idkmehr3.setS(IDKMEHRschemes.ID_KMEHR);
		idkmehr3.setValue("3");
		idkmehr3.setSV("1.0");
		claimItem.getIds().add(idkmehr3);

		ContentType claimContent = new ContentType();
		CDCONTENT claimCd = new CDCONTENT();

		claimCd.setS(CDCONTENTschemes.CD_NIHDI);
		claimCd.setSV("1.0");
		claimCd.setValue(nomenclature);

		claimContent.getCds().add(claimCd);
		claimItem.getContents().add(claimContent);

		transaction.getItem().add(claimItem);

		Boolean istest = config.getProperty("endpoint.dmg.notification.v1").contains("-acpt");

		NotifyGlobalMedicalFileRequest result = this.buildSendNotifyRequest(istest, hcp, ref, pI, dateReference, request);

		//Patch time... This is pretty ugly stuff
		/*BlobType detail = result.getDetail();
		try {
			String req = new String(detail.getValue(), "UTF8");
			String fixedRequest = req.replaceAll("(<ns[0-9]+:time>)([0-9]+:[0-9]+:[0-9]+)(?:\\.[0-9]+)?\\+[0-9]+:[0-9]+(</ns[0-9]+:time>)", "$1$2$3");
				Blob blob = RequestBuilderFactory.getBlobBuilder("dmg").build(fixedRequest.getBytes("UTF8"), "none", detail.getId(), "text/xml");
				blob.setMessageName("GMD-CONSULT-HCP");
                result.setDetail(RequestObjectMapper.mapBlobTypefromBlob(blob));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}*/

		service = DmgSessionServiceFactory.getDmgService();
		SendResponseType resp = service.notifyGlobalMedicalFile(result);
		ResponseObjectBuilder respBuilder = ResponseObjectBuilderFactory.getResponseObjectBuilder();
		DmgBuilderResponse response = respBuilder.handleSendResponseType(resp);

		if (!response.getEhealthStatus().equals("200")) {
			throw new RuntimeException("Wrong status code" + response.getEhealthStatus());
		}

		SendTransactionResponse sendTransactionResponse = response.getSendTransactionResponse();
		AcknowledgeType acknowledge = sendTransactionResponse.getAcknowledge();
		List<ErrorMyCarenetType> errors = acknowledge.getErrors();

		DmgNotification dmgNotification = new DmgNotification(acknowledge.isIscomplete());

		if (errors != null && !errors.isEmpty()) {
			addErrorsToMessage(errors,dmgNotification);
			return dmgNotification;
		}

		Kmehrmessage kmehrmessage = sendTransactionResponse.getKmehrmessage();

		if (kmehrmessage != null) {
			OUTER:
			for (FolderType f : kmehrmessage.getFolders()) {
				for (be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType t : f.getTransactions()) {
					for (CDTRANSACTION cd : t.getCds()) {
						if (cd.getValue() != null && cd.getValue().toLowerCase().equals("gmd")) {
							for (ItemType i : t.getItem()) {
								for (CDITEM cdi : i.getCds()) {
									if (cdi.getValue() != null && cdi.getValue().equals("gmdmanager")) {
										dmgNotification.setFrom(i.getBeginmoment() != null && i.getBeginmoment().getDate() != null ? i.getBeginmoment().getDate().toDate() : null);

										for (ContentType c : i.getContents()) {
											if (c.getHcparty() != null) {
												fillHcParty(c.getHcparty(), healthcarePartyId);
												dmgNotification.setHcParty(c.getHcparty());
												break; //Content
											}
										}
										break; //CD-ITEM
									}
									if (cdi.getValue() != null && cdi.getValue().equals("payment")) {
										for (ContentType c : i.getContents()) {
											if (c.isBoolean() != null) {
												dmgNotification.setPayment(c.isBoolean());
												break; //Content
											}
										}
										break; //CD-ITEM
									}
								}
							}
							break OUTER;
						}
					}
				}
			}
		}
		return dmgNotification;
	}

	private void addErrorsToMessage(List<ErrorMyCarenetType> errors, DmgMessage dmgMessage) {
		errors.stream().filter(Objects::nonNull).forEach(errorType -> {
			for (CDERRORMYCARENET errorCd : errorType.getCds()) {
				String code = errorCd.getValue();
				Error error = new Error(code, errorType.getUrl(), errorType.getDescription() == null ? null : errorType.getDescription().getValue(), errorMessages.get(":" + code) != null ? errorMessages.get(":" + code).getMessage() : null);
				dmgMessage.getErrors().add(error);
				log.error(error.toString());
			}
		});
	}

	@Override
	public boolean confirmDmgMessages(String token, List<DmgMessage> dmgMessages, List<DmgAcknowledge> dmgTacks) throws TokenNotAvailableException, EidSessionCreationFailedException, ConnectorException, URISyntaxException, InstantiationException, DataFormatException {
		assert dmgMessages != null && dmgTacks!= null;
		if (token == null) {
			throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
		}

		if (dmgMessages.size() == 0 && dmgTacks.size() == 0) { return true; }

		Base64.Decoder b64 = Base64.getDecoder();

		DmgService service = DmgSessionServiceFactory.getDmgService();
		OrigineType origin = buildOriginType(config);
		WsAddressingHeader confirmheader = WsAddressingUtil.createHeader("", "urn:be:cin:nip:async:generic:confirm:hash");
		be.cin.nip.async.generic.Confirm confirm = BuilderFactory.getRequestObjectBuilder("dmg").buildConfirmRequestWithHashes(origin, dmgMessages.stream().map(dmgMessage -> b64.decode(dmgMessage.getValueHash())).collect(Collectors.toList()), dmgTacks.stream().map(dmgMessage -> b64.decode(dmgMessage.getValueHash())).collect(Collectors.toList()));

		// The output is empty, which indicates that the confirm was processed correctly.
		// In case there is an error, a fault is returned instead of the empty response
		service.confirmRequest(confirm, confirmheader);

		return true;
	}

	@Override
	public boolean confirmDmgMessagesWithNames(String token, List<String> messageNames) throws TokenNotAvailableException, EidSessionCreationFailedException, ConnectorException, URISyntaxException, InstantiationException, DataFormatException {
		assert messageNames != null;
		if (token == null) {
			throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
		}

		DmgService service = DmgSessionServiceFactory.getDmgService();
		MsgQuery msgQuery = new MsgQuery();
		msgQuery.setMax(100);

		// if no messages are added , all messages are returned
		msgQuery.getMessageNames().clear();
		messageNames.stream().filter(messageName -> !messageName.equals("ACKS")).forEach(messageName -> msgQuery.getMessageNames().add(messageName));
		msgQuery.setInclude(msgQuery.getMessageNames().size() > 0);

		Query tackQuery = new Query();
		tackQuery.setInclude(messageNames.contains("ACKS"));
		tackQuery.setMax(100);

		WsAddressingHeader responseGetHeader = new WsAddressingHeader(new URI("urn:be:cin:nip:async:generic:get:query"));
		responseGetHeader.setMessageID(new URI(IdGeneratorFactory.getIdGenerator("uuid").generateId()));

		Configuration config = ConfigFactory.getConfigValidator().getConfig();

		Get get = new Get();
		get.setMsgQuery(msgQuery);
		get.setTAckQuery(tackQuery);
		OrigineType origin = buildOriginType(config);
		get.setOrigin(origin);

		GetResponse response = service.getRequest(get, responseGetHeader);

		if (response.getReturn().getTAckResponses().size() > 0 || response.getReturn().getMsgResponses().size() > 0) {
			WsAddressingHeader confirmheader = WsAddressingUtil.createHeader("", "urn:be:cin:nip:async:generic:confirm:hash");
			be.cin.nip.async.generic.Confirm confirm = BuilderFactory.getRequestObjectBuilder("dmg").buildConfirmRequest(origin, response.getReturn().getMsgResponses(), response.getReturn().getTAckResponses());

			// The output is empty, which indicates that the confirm was processed correctly.
			// In case there is an error, a fault is returned instead of the empty response
			service.confirmRequest(confirm, confirmheader);
		}
		return true;
	}

	@Override
	public DmgRegistration registerDoctor(String token, String oa, String bic, String iban) throws TechnicalConnectorException, TokenNotAvailableException, EidSessionCreationFailedException {
		try {
			if (token == null) {
				throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
			}
			String healthcarePartyId = sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId();
			HealthcareParty hcp = healthcarePartyLogic.getHealthcareParty(healthcarePartyId);

			String nihii = hcp.getNihii();

			Boolean istest = config.getProperty("endpoint.dmg.notification.v1").contains("-acpt");

			RequestBuilder mapper = RegistrationRequestBuilderFactory.getRequestObjectBuilder();
			CommonBuilder commonBuilder = RequestBuilderFactory.getCommonBuilder("mcn.registration");
			be.ehealth.business.mycarenetcommons.domain.CommonInput commonInput = commonBuilder.createCommonInput(ConfigUtil.retrievePackageInfo("mcn.registration"), istest, "");

			String request = ("<reg:registrations xmlns:p=\"urn:be:cin:mycarenet:esb:common:v2\"\n" +
					"xmlns:reg=\"urn:be:cin:nip:sync:reg:v1\"\n" +
					"xmlns:other=\"urn:other\"\n" +
					"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
					"xsi:schemaLocation=\"urn:be:cin:nip:sync:reg:v1Registrations-v1.xsd\"\n" +
					"StartDateTime=\"replaceWithDateYYYY-MM-DD\" >\n" +
					"<reg:registrant>\n" +
					"<reg:CareProvider>\n" +
					"<p:Nihii>\n" +
					"<p:Quality>doctor</p:Quality>\n" +
					"<p:Value>replaceWithNihiiNumber</p:Value>\n" +
					"</p:Nihii>\n" +
					"</reg:CareProvider>\n" +
					"</reg:registrant>\n" +
					"<reg:registration serviceName=\"GMD\" >\n" +
					"<reg:bankAccount bic=\"replaceWithBic\" iban=\"replaceWithIban\"/>\n" +
					"</reg:registration>\n" +
					"</reg:registrations>")
					.replaceAll("replaceWithDateYYYY-MM-DD", new DateTime().toString("YYYY-MM-dd"))
					.replaceAll("replaceWithNihiiNumber", nihii)
					.replaceAll("replaceWithBic", bic)
					.replaceAll("replaceWithIban", iban.toUpperCase());

			String sysProp = java.lang.System.getProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema");

			java.lang.System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema", "com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory");
			Blob blob = RequestBuilderFactory.getBlobBuilder("mcn.registration").build(request.getBytes("UTF8"));

			CareReceiverId careReceiver = new CareReceiverId(null);
			careReceiver.setMutuality(oa);

			RegisterToMycarenetServiceRequest mcRequest = mapper.buildRegisterToMycarenetRequest(commonInput, new Routing(careReceiver, new DateTime()), blob, null);
			RegistrationSession session = RegistrationSessionFactory.getRegistrationSession();

			RegisterToMycarenetServiceResponse response = session.registerToMycarenetService(mcRequest);

			be.fgov.ehealth.mycarenet.commons.core.v2.BlobType detail = response.getReturn().getDetail();

			RegistrationsAnswer registrationsAnswer = ResponseHelper.toObject(detail.getValue());

			DmgRegistration rr = new DmgRegistration();
			rr.setSuccess(registrationsAnswer.getRegistrationAnswer().getStatus().equals(RegistrationStatus.SUCCESS));
			rr.setComplete(true);

			if (!rr.isSuccess()) {
				for (DetailType d : registrationsAnswer.getRegistrationAnswer().getAnswerDetails()) {
					rr.getErrors().add(new Error(d.getDetailCode(), d.getLocation(), d.getDetailSource(), errorMessages.get(":" + d.getDetailCode()) != null ? errorMessages.get(":" + d.getDetailCode()).getMessage() : null));
					if (d.getDetailCode().equals("168")) {
						rr.setSuccess(true);
					}
				}
			}

			if (sysProp != null) {
				java.lang.System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema", sysProp);
			} else {
				java.lang.System.clearProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema");
			}

			return rr;
		} catch (ConnectorException e) {
			throw new IllegalArgumentException("Invalid configuration", e);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Invalid parameters", e);
		} catch (SOAPFaultException e) {
			DmgRegistration rr = new DmgRegistration();
			rr.setComplete(false);
			rr.getErrors().add(new Error("SOA-02001", null, "SOAP Fault", errorMessages.get(":SOA-02001").getMessage()));
			return rr;
		}
	}


	@Override
	public List<DmgMessageResponse> fetchDmgMessages(String token, List<String> messageNames) throws TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException {
		User loggedUser = sessionLogic.getCurrentSessionContext().getUser();

		return this.getDmgMessages(token, messageNames).stream().map(m -> {
			Document document = new Document();
			document.setId(idg.newGUID().toString());

			try {
				JsonElement jsonTree = gson.toJsonTree(mapper.map(m, org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgMessage.class));
				((JsonObject)jsonTree).addProperty("$type",m.getClass().getSimpleName());
				String gsonMsg = gson.toJson(jsonTree);
				document.setAttachment(gsonMsg.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				log.error("Could not encode message attachment: "+e);
				return null;
			}

			document.setName("GMD message: " + m.getIo() + " (" + m.getClass().getSimpleName() + ")");
			document.setMainUti("public.json");

			org.taktik.icure.entities.Message msg = new Message();
			msg.setId(idg.newGUID().toString());

			String transportGuid = "GMD:IN:" + m.getReference();
			msg.setTransportGuid(transportGuid);
			msg.setFromAddress("GMD");
			msg.setRecipients(Collections.singleton(loggedUser.getHealthcarePartyId()));
			msg.setRecipientsType(HealthcareParty.class.getName());

			msg.setReceived(System.currentTimeMillis());
			msg.setStatus(Message.STATUS_UNREAD);
			msg.setSubject(document.getName());
			msg.setToAddresses(Collections.singleton("GMDBOX"));

			DmgMessageResponse message = new DmgMessageResponse();

			message.setDmgMessage(m);

			try {
				message.setDocument(documentLogic.createDocument(document, loggedUser.getHealthcarePartyId()));
			} catch (CreationException e) {
				log.error("Cannot create DMG document: "+e);
				return null;
			}
			try {
				message.setMessage(messageLogic.createMessage(msg));
			} catch (CreationException | LoginException e) {
				log.error("Cannot create DMG message: "+e);
				return null;
			}
			return message;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public List<DmgMessage> getDmgMessages(String token, List<String> messageNames) throws TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException {
		try {
			if (token == null) {
				throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
			}

			String healthcarePartyId = sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId();

			List<PatientWithDate> activeDmgs = new LinkedList<>();
			List<PatientWithDate> closedDmgs = new LinkedList<>();

			DmgService service = DmgSessionServiceFactory.getDmgService();
			MsgQuery msgQuery = new MsgQuery();
			msgQuery.setInclude(true);
			msgQuery.setMax(100);

			// if no messages are added , all messages are returned
			msgQuery.getMessageNames().clear();
			if (messageNames != null) {
				for (String messageName : messageNames) {
					msgQuery.getMessageNames().add(messageName);
				}
			}

			Query tackQuery = new Query();
			tackQuery.setInclude(true);
			tackQuery.setMax(100);

			WsAddressingHeader responseGetHeader = new WsAddressingHeader(new URI("urn:be:cin:nip:async:generic:get:query"));
			responseGetHeader.setMessageID(new URI(IdGeneratorFactory.getIdGenerator("uuid").generateId()));

			Configuration config = ConfigFactory.getConfigValidator().getConfig();

			Get get = new Get();
			get.setMsgQuery(msgQuery);
			get.setTAckQuery(tackQuery);
			get.setOrigin(buildOriginType(config));

			GetResponse response = service.getRequest(get, responseGetHeader);

			Base64.Encoder b64 = Base64.getEncoder();
			List<DmgMessage> messages = new ArrayList<>();
			for (TAckResponse r : response.getReturn().getTAckResponses()) {
				TAck tAck = r.getTAck();
				messages.add(new DmgAcknowledge(tAck.getResultMajor(), tAck.getResultMinor(), tAck.getResultMessage()).withIo(tAck.getIssuer().replaceAll("urn:nip:issuer:io:","")).withReference(tAck.getReference()).withHash(b64.encodeToString(tAck.getValue())));
			}
			for (MsgResponse r : response.getReturn().getMsgResponses()) {
				DmgBuilderResponse decResponse = ResponseObjectBuilderFactory.getResponseObjectBuilder().handleAsyncResponse(r);
				Kmehrmessage kmehrmessage = decResponse.getKmehrmessage();
				RetrieveTransactionResponse retrieveTransactionResponse = decResponse.getRetrieveTransactionResponse();
				if (retrieveTransactionResponse != null) {
					DmgsList dmgsList = new DmgsList();

					messages.add(dmgsList.withIo((kmehrmessage != null ? kmehrmessage.getHeader().getSender().getHcparties() : retrieveTransactionResponse.getResponse().getAuthor().getHcparties()).iterator().next().getIds().stream().filter(idhcparty -> idhcparty.getS()==IDHCPARTYschemes.ID_INSURANCE).findFirst().map(IDHCPARTY::getValue).orElse(null))
						.withReference(r.getCommonOutput().getNIPReference())
						.withHash(b64.encodeToString(r.getDetail().getHashValue())));

					AcknowledgeType acknowledge = retrieveTransactionResponse.getAcknowledge();
					List<ErrorMyCarenetType> errors = acknowledge.getErrors();

					if (retrieveTransactionResponse.getResponse() != null && retrieveTransactionResponse.getResponse().getAuthor() != null && retrieveTransactionResponse.getResponse().getAuthor().getHcparties().size() > 0) {
						for (HcpartyType p : retrieveTransactionResponse.getResponse().getAuthor().getHcparties()) {
							p.getCds().stream().filter(cd -> cd.getS().equals(CDHCPARTYschemes.CD_HCPARTY) && cd.getValue() != null && cd.getValue().equals("orginsurance")).filter(cd -> p.getIds() != null && p.getIds().size() > 0).forEach(cd -> dmgsList.setIo(p.getIds().get(0).getValue()));
						}
					}
					if (errors != null && !errors.isEmpty()) {
						addErrorsToMessage(errors, dmgsList);
					} else if (retrieveTransactionResponse.getAcknowledge() != null && retrieveTransactionResponse.getAcknowledge().isIscomplete()) {
						kmehrmessage = retrieveTransactionResponse.getKmehrmessage();
						if (kmehrmessage != null) {
							if (kmehrmessage.getHeader().getDate() != null) {
								dmgsList.setDate(kmehrmessage.getHeader().getDate().toDate());
							}
							for (FolderType f : kmehrmessage.getFolders()) {
								DmgInscription insc = new DmgInscription();
								dmgsList.getInscriptions().add(insc);
								PersonType patient = f.getPatient();
								fillDmgMessageWithPatient(insc, patient);
								for (be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType t : f.getTransactions()) {
									for (CDTRANSACTION cd : t.getCds()) {
										if (cd.getValue() != null && cd.getValue().toLowerCase().equals("gmd")) {
											int paymentIdx = 0;
											for (ItemType i : t.getItem()) {
												for (CDITEM cdi : i.getCds()) {
													if (cdi.getValue() != null && cdi.getValue().equals("gmdmanager")) {
														insc.setFrom(i.getBeginmoment() != null && i.getBeginmoment().getDate() != null ? i.getBeginmoment().getDate().toDate() : null);
														insc.setTo(i.getEndmoment() != null && i.getEndmoment().getDate() != null ? i.getEndmoment().getDate().toDate() : null);

														String hcpId = null;
														for (ContentType c : i.getContents()) {
															if (c.getHcparty() != null) {
																hcpId = fillHcParty(c.getHcparty(), healthcarePartyId);
																insc.setHcParty(c.getHcparty());
																break; //Content
															}
														}

														if (insc.getFrom() != null && (healthcarePartyId.equals(hcpId))) {
															activeDmgs.add(new PatientWithDate(patient, insc.getFrom()));
														}

														break; //CD-ITEM
													}
													if (cdi.getValue() != null && cdi.getValue().equals("payment")) {
														paymentIdx++;
														if (i.getCost() != null && i.getCost().getDecimal() != null) {
															insc.setPaymentAmount(paymentIdx, i.getCost().getDecimal().doubleValue());
														}
														if (i.getCost() != null && i.getCost().getUnit() != null) {
															insc.setPaymentCurrency(paymentIdx, i.getCost().getUnit().getCd().getValue());
														}
														if (i.getBeginmoment() != null) {
															insc.setPaymentDate(paymentIdx, i.getBeginmoment().getDate().toDate());
														}
														for (ContentType c : i.getContents()) {
															if (c.getTexts() != null && c.getTexts().size() > 0) {
																StringBuilder bf = new StringBuilder();
																for (TextType txt : c.getTexts()) {
																	bf.append(txt.getValue()).append(" ");
																}
																insc.setPaymentRef(paymentIdx, bf.toString());
															}
														}
														break; //CD-ITEM
													}
												}
											}
										}
									}
								}
							}
						}
					}
				} else if (kmehrmessage != null) {
					if (kmehrmessage.getFolders().size() > 0 && kmehrmessage.getFolders().get(0).getTransactions().size() > 0) {
						be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType t = kmehrmessage.getFolders().get(0).getTransactions().get(0);
						FolderType f = kmehrmessage.getFolders().get(0);

						for (CDTRANSACTION cd : t.getCds()) {
							if (cd.getValue() != null) {

								String type = cd.getValue();

								PersonType patient = f.getPatient();
								if (cd.getS().equals(CDTRANSACTIONschemes.CD_TRANSACTION_MYCARENET) && type.equals("gmdextension")) {
									DmgExtension extension = new DmgExtension();
									fillDmgMessageWithPatient(extension, patient);

									messages.add(extension.withIo(kmehrmessage.getHeader().getSender().getHcparties().iterator().next().getIds().stream().filter(idhcparty -> idhcparty.getS()==IDHCPARTYschemes.ID_INSURANCE).findFirst().map(IDHCPARTY::getValue).orElse(null)).withReference(r.getCommonOutput().getNIPReference()).withHash(b64.encodeToString(r.getDetail().getHashValue())));
									String hcpId = null;
									for (ItemType i : t.getItem()) {
										for (CDITEM cdi : i.getCds()) {
											if (cdi.getValue() != null && cdi.getValue().equals("gmdmanager")) {
												for (ContentType c : i.getContents()) {
													if (c.getHcparty() != null) {
														hcpId = fillHcParty(c.getHcparty(), healthcarePartyId);
														extension.setHcParty(c.getHcparty());
														break; //Content
													}
												}
												break; //CdItem
											}
										}
									}
									for (ItemType i : t.getItem()) {
										for (CDITEM cdi : i.getCds()) {
											if (cdi.getValue() != null && cdi.getValue().equals("encounterdatetime")) {
												for (ContentType c : i.getContents()) {
													if (c.getDate() != null) {
														extension.setEncounterDate(c.getDate().toDate());
														if (healthcarePartyId.equals(hcpId)) { activeDmgs.add(new PatientWithDate(patient, extension.getEncounterDate())); }
														break; //Content
													}
												}
												break; //CdItem
											} else if (cdi.getValue() != null && cdi.getValue().equals("claim")) {
												for (ContentType c : i.getContents()) {
													if (c.getCds() != null && c.getCds().size() > 0 && c.getCds().get(0).getS().equals(CDCONTENTschemes.CD_NIHDI)) {
														extension.setClaim(c.getCds().get(0).getValue());
														break; //Content
													}
												}
												break; //CdItem
											}
										}
									}
									break; //Transaction
								} else if (cd.getS().equals(CDTRANSACTIONschemes.CD_TRANSACTION_MYCARENET) && type.equals("gmdclosure")) {
									DmgClosure closure = new DmgClosure();
									fillDmgMessageWithPatient(closure, patient);

									messages.add(closure.withIo(kmehrmessage.getHeader().getSender().getHcparties().iterator().next().getIds().stream().filter(idhcparty -> idhcparty.getS()==IDHCPARTYschemes.ID_INSURANCE).findFirst().map(IDHCPARTY::getValue).orElse(null)).withReference(r.getCommonOutput().getNIPReference()).withHash(b64.encodeToString(r.getDetail().getHashValue())));
									for (ItemType i : t.getItem()) {
										for (CDITEM cdi : i.getCds()) {
											if (cdi.getValue() != null && cdi.getValue().equals("gmdmanager")) {
												Date begin = (i.getBeginmoment() != null) ? i.getBeginmoment().getDate().toDate() : null;
												Date end = (i.getEndmoment() != null) ? i.getEndmoment().getDate().toDate() : null;
												HcpartyType party = null;
												String hcpId = null;
												for (ContentType c : i.getContents()) {
													if (c.getHcparty() != null) {
														hcpId = fillHcParty(c.getHcparty(), healthcarePartyId);
														party = c.getHcparty();
														break; //Content
													}
												}
												if (end != null && party != null) {
													if (healthcarePartyId.equals(hcpId)) { closedDmgs.add(new PatientWithDate(patient, end)); }
													closure.setEndOfPreviousDmg(end);
													closure.setPreviousHcParty(party);
												} else if (begin != null && party != null) {
													closure.setBeginOfNewDmg(begin);
													closure.setNewHcParty(party);
													closure.setNewHcPartyId(hcpId);
												}
												break; //CdItem
											}
										}
									}
									break; //Transaction
								}
							}
						}
					}
				}
			}

			if (activeDmgs.size()>0 || closedDmgs.size()>0) {
				HashMap<String, Date> activeSsins = new HashMap<>();
				HashMap<String, Date> closedSsins = new HashMap<>();
				activeDmgs.forEach(pwd -> pwd.patient.getIds().stream().filter(i -> i.getS() == IDPATIENTschemes.ID_PATIENT).findFirst().map(IDPATIENT::getValue).ifPresent(ssin->{if (!activeSsins.containsKey(ssin) || activeSsins.get(ssin).before(pwd.date)) { activeSsins.put(ssin, pwd.date); } }));
				closedDmgs.forEach(pwd -> pwd.patient.getIds().stream().filter(i -> i.getS() == IDPATIENTschemes.ID_PATIENT).findFirst().map(IDPATIENT::getValue).ifPresent(ssin->{if (!closedSsins.containsKey(ssin) || closedSsins.get(ssin).before(pwd.date)) { closedSsins.put(ssin, pwd.date); } }));

				List<String> activePatientIds = patientLogic.listByHcPartyAndSsinsIdsOnly(activeSsins.keySet(), healthcarePartyId);
				for (org.taktik.icure.entities.Patient p : patientLogic.getPatients(activePatientIds)) {
					Date date = activeSsins.get(p.getSsin());
					if (closedSsins.containsKey(p.getSsin()) && closedSsins.get(p.getSsin()).after(date)) {
						continue; //skip
					}
					try {
						patientLogic.modifyPatientReferral(p, healthcarePartyId, Instant.ofEpochMilli(date.getTime()), null);
					} catch (MissingRequirementsException e) {
						log.info(e);
					}
				}

				List<String> closedPatientIds = patientLogic.listByHcPartyAndSsinsIdsOnly(closedSsins.keySet(), healthcarePartyId);
				for (org.taktik.icure.entities.Patient p : patientLogic.getPatients(closedPatientIds)) {
					Date date = closedSsins.get(p.getSsin());
					if (activeSsins.containsKey(p.getSsin()) && activeSsins.get(p.getSsin()).after(date)) {
						continue; //skip
					}
					try {
						patientLogic.modifyPatientReferral(p, null, null, Instant.ofEpochMilli(date.getTime()));
					} catch (MissingRequirementsException e) {
						log.info(e);
					}
				}
			}

			return messages;
		} catch (ConnectorException | URISyntaxException e) {
			throw new IllegalArgumentException("Invalid configuration", e);
		}
	}

	private void fillDmgMessageWithPatient(DmgMessageWithPatient insc, PersonType patient) {
		if (patient != null) {
			insc.setLastName(patient.getFamilyname());
			insc.setFirstName(StringUtils.join(patient.getFirstnames(), " "));
			if (patient.getSex() != null) {
				insc.setSex(patient.getSex().getCd().getValue().value());
			}
			if (patient.getBirthdate() != null) {
				insc.setBirthday(patient.getBirthdate().getDate().toDate());
			}
			patient.getIds().stream().filter(id -> id.getS().equals(IDPATIENTschemes.ID_PATIENT) || id.getS().equals(IDPATIENTschemes.INSS)).forEach(id -> insc.setInss(id.getValue()));
			if (patient.getInsurancymembership() != null) {
				if (patient.getInsurancymembership().getId() != null) {
					insc.setMutuality(patient.getInsurancymembership().getId().getValue());
				}
				if (patient.getInsurancymembership().getMembership() != null) {
					insc.setRegNrWithMut(patient.getInsurancymembership().getMembership().toString());
				}
			}
		}
	}

	private String fillHcParty(HcpartyType hcParty, String healthcarePartyId) {
		if (StringUtils.isEmpty(hcParty.getFirstname()) || StringUtils.isEmpty(hcParty.getFamilyname())) {
			for (IDHCPARTY id : hcParty.getIds()) {
				if (id.getS().equals(IDHCPARTYschemes.ID_HCPARTY)) {
					List<HealthcareParty> hcps = healthcarePartyLogic.listByNihii(id.getValue());
					if (hcps != null && hcps.size() > 0) {
						HealthcareParty hcp = hcps.stream().filter(h->h.getId().equals(healthcarePartyId)).findFirst().orElse(hcps.get(0));
						hcParty.setFirstname(hcp.getFirstName());
						hcParty.setFamilyname(hcp.getLastName());

						return hcp.getId();
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean postDmgsListRequest(String token, String insurance, Date requestDate) throws TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException {
		RetrieveTransactionRequest retrieveTransactionRequest = new RetrieveTransactionRequest();
		RequestType request = new RequestType();
		Boolean istest = config.getProperty("endpoint.dmg.notification.v1").contains("-acpt");

		AuthorType author;
		try {
			if (token == null) {
				throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
			}
			author = HcPartyUtil.createAuthor(DmgConstants.PROJECT_IDENTIFIER);
			String inputReference = IdGeneratorFactory.getIdGenerator().generateId();
			if (istest) {
				inputReference = "T" + inputReference.substring(1);
			}
			request.setId(HcPartyUtil.createKmehrId(DmgConstants.PROJECT_IDENTIFIER, inputReference));
			request.setAuthor(author);

			DateTime now = new DateTime();
			now = now.withMillisOfSecond(0);
			request.setDate(now);
			request.setTime(now);
			retrieveTransactionRequest.setRequest(request);
			TransactionType transactions = new TransactionType();
			transactions.setAuthor(author);
			DateTime beginDate = new DateTime(requestDate);
			transactions.setBegindate(beginDate);

			CDTRANSACTION myCarenetTransaction = new CDTRANSACTION();
			myCarenetTransaction.setS(CDTRANSACTIONschemes.CD_TRANSACTION_MYCARENET);
			myCarenetTransaction.setSV("1.0");
			myCarenetTransaction.setValue("gmd");
			transactions.getCds().add(myCarenetTransaction);

			SelectRetrieveTransactionType select = new SelectRetrieveTransactionType();

			retrieveTransactionRequest.setSelect(select);
			select.setTransaction(transactions);

			byte[] content = ConnectorXmlUtils.toByteArray(retrieveTransactionRequest);
			try {
				String contentString = new String(content, "UTF8");
				String fixedContentString = contentString.replaceAll("(<ns[0-9]+:time>)([0-9]+:[0-9]+:[0-9]+)\\.[0-9]+\\+[0-9]+:[0-9]+(</ns[0-9]+:time>)", "$1$2$3");
				log.debug("created RetrieveTransactionRequest: " + fixedContentString);
				content = fixedContentString.getBytes("UTF8");
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(e);
			}

			BlobBuilder bbuilder = RequestBuilderFactory.getBlobBuilder("genericasync");
			Blob blob = bbuilder.build(content, "deflate", "_" + UUID.randomUUID().toString(), "text/xml");
			String messageName = DmgConstants.GMD_CONSULT_HCP;
			blob.setMessageName(messageName);

			DmgService service = DmgSessionServiceFactory.getDmgService();

			PostParameter postParameter = new PostParameter(blob, istest, DmgConstants.PROJECT_IDENTIFIER, false, null, "urn:be:cin:nip:async:generic:post:msg", inputReference);
			CommonInput ci = buildCommonInput(istest, inputReference);

			be.cin.types.v1.Blob det = SendRequestMapper.mapBlobToCinBlob(postParameter.blob);

			// no xades needed for dmg async
			Post post = BuilderFactory.getRequestObjectBuilder(postParameter.serviceName).buildPostRequest(ci, det, null);

			WsAddressingHeader header = new WsAddressingHeader(new URI("urn:be:cin:nip:async:generic:post:msg"));
			header.setTo(new URI(insurance != null ? "urn:nip:destination:io:" + insurance : ""));
			header.setFaultTo("http://www.w3.org/2005/08/addressing/anonymous");
			header.setReplyTo("http://www.w3.org/2005/08/addressing/anonymous");
			header.setMessageID(new URI("uuid:" + UUID.randomUUID()));

			PostResponse postResponse = service.postRequest(post, header);
			TAck tack = postResponse.getReturn();

			return tack.getResultMajor() != null && tack.getResultMajor().equals("urn:nip:tack:result:major:success");
		} catch (ConnectorException | InstantiationException e) {
			throw new IllegalArgumentException("Invalid configuration", e);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Invalid configuration", e);
		}
	}

	private CommonInput buildCommonInput(Boolean istest, String inputReference) throws TechnicalConnectorException {
		Configuration config = ConfigFactory.getConfigValidator().getConfig();
		CommonInput ci = new CommonInput();
		be.cin.mycarenet.esb.common.v2.RequestType ciRequestType = new be.cin.mycarenet.esb.common.v2.RequestType();
		ciRequestType.setIsTest(istest);
		ci.setRequest(ciRequestType);

		OrigineType ciOriginType = buildOriginType(config);

		ci.setOrigin(ciOriginType);

		ci.setInputReference(inputReference);
		return ci;
	}

	private OrigineType buildOriginType(Configuration config) {
		OrigineType ciOriginType = new OrigineType();

		PackageType ciPackageType = new PackageType();
		ValueRefString ciPackageName = new ValueRefString();
		ciPackageName.setValue(config.getProperty("genericasync.dmg.package.name"));
		ciPackageType.setName(ciPackageName);

		LicenseType ciLicenseType = new LicenseType();
		ciLicenseType.setUsername(config.getProperty("dmg.package.licence.username"));
		ciLicenseType.setPassword(config.getProperty("dmg.package.licence.password"));

		ciPackageType.setLicense(ciLicenseType);

		CareProviderType ciCareProvider = new CareProviderType();
		NihiiType ciNihiiType = new NihiiType();
		ValueRefString nihiiRefString = new ValueRefString();
		nihiiRefString.setValue(config.getProperty("user.nihii"));

		ciNihiiType.setQuality("doctor");
		ciNihiiType.setValue(nihiiRefString);

		IdType ciPhysicalPerson = new IdType();

		ValueRefString ssinRefString = new ValueRefString();
		ssinRefString.setValue(config.getProperty("user.inss"));

		ciPhysicalPerson.setSsin(ssinRefString);

		ciCareProvider.setNihii(ciNihiiType);
		ciCareProvider.setPhysicalPerson(ciPhysicalPerson);

		ciOriginType.setPackage(ciPackageType);
		ciOriginType.setCareProvider(ciCareProvider);
		return ciOriginType;
	}

	@Override
	public DmgConsultation consultDmg(String token, HealthcareParty hcp, String patientNiss, String insurance, String
			regNrWithMut, String gender, Date requestDate) throws KeyStoreException, CertificateExpiredException, TokenNotAvailableException, ConnectorException, InstantiationException, DataFormatException, NoSuchAlgorithmException {
		DmgService service;

		assert patientNiss != null || insurance != null && regNrWithMut != null;
		if (token == null) {
			throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
		}
		Boolean istest = config.getProperty("endpoint.dmg.consultation.v1").contains("-acpt");
		String healthcarePartyId = sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId();

		// DMGReferences ref = DmgTestUtils.createDmgReferenceForTest();
		DMGReferences ref = new DMGReferences(true);

		Patient pI = new Patient();
		pI.setInss(patientNiss);
		pI.setMutuality(insurance);
		pI.setRegNrWithMut(regNrWithMut);

		DateTime dateReference = new DateTime();

		SelectRetrieveTransaction request = new SelectRetrieveTransaction();

		PatientType patient = new PatientType();
		addNissToPatientIds(patientNiss, patient.getIds());

		MemberinsuranceType member = new MemberinsuranceType();

		if (pI.getRegNrWithMut() != null) {
			member.setMembership(pI.getRegNrWithMut());
			setMemberId(pI, member);
		}
		if (member.getId() != null || member.getMembership() != null) {
			patient.setInsurancymembership(member);
			SexType sexType = new SexType();
			CDSEX cdsex = new CDSEX();
			gender = gender == null ? "i" : gender.toLowerCase();
			cdsex.setValue(
					gender.equals("f") ? CDSEXvalues.FEMALE :
							gender.equals("m") ? CDSEXvalues.MALE :
									gender.equals("c") ? CDSEXvalues.CHANGED : CDSEXvalues.UNKNOWN
			);
			patient.setSex(sexType);
		}

		request.setPatient(patient);

		TransactionType transaction = new TransactionType();

		CDTRANSACTION cdtransaction = new CDTRANSACTION();
		cdtransaction.setSV("1.0");
		cdtransaction.setValue("gmd");
		cdtransaction.setS(CDTRANSACTIONschemes.CD_TRANSACTION_MYCARENET);
		transaction.getCds().add(cdtransaction);

		transaction.setBegindate(new DateTime(requestDate.getTime()));

		request.setTransaction(transaction);

		ConsultGlobalMedicalFileRequest result = this.buildSendConsultRequest(istest, ref, hcp, pI, dateReference, request);

		//Patch time... This is pretty ugly stuff
		BlobType detail = result.getDetail();
		try {
			String fixedRequest = new String(detail.getValue(), "UTF8").replaceAll("(<ns[0-9]+:time>)([0-9]+:[0-9]+:[0-9]+)\\.[0-9]+\\+[0-9]+:[0-9]+(</ns[0-9]+:time>)", "$1$2$3");
			Blob blob = RequestBuilderFactory.getBlobBuilder("dmg").build(fixedRequest.getBytes("UTF8"), "none", detail.getId(), "text/xml");
			blob.setMessageName("GMD-CONSULT-HCP");
			result.setDetail(RequestObjectMapper.mapBlobTypefromBlob(blob));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}

		service = DmgSessionServiceFactory.getDmgService();
		SendResponseType resp = service.consultGlobalMedicalFile(result);
		ResponseObjectBuilder respBuilder = ResponseObjectBuilderFactory.getResponseObjectBuilder();
		DmgBuilderResponse response = respBuilder.handleSendResponseType(resp);

		if (!response.getEhealthStatus().equals("200")) {
			throw new RuntimeException("Wrong status code" + response.getEhealthStatus());
		}

		RetrieveTransactionResponse retrieveTransactionResponse = response.getRetrieveTransactionResponse();
		AcknowledgeType acknowledge = retrieveTransactionResponse.getAcknowledge();
		List<ErrorMyCarenetType> errors = acknowledge.getErrors();

		DmgConsultation dmgConsultation = new DmgConsultation(acknowledge.isIscomplete());

		if (errors != null && !errors.isEmpty()) {
			errors.stream().filter(errorType -> errorType != null).forEach(errorType -> {
				String code = errorType.getCds().size() > 0 ? errorType.getCds().get(0).getValue() : null;
				Error error = new Error(code, errorType.getUrl(), errorType.getDescription() == null ? null : errorType.getDescription().getValue(), errorMessages.get(":" + code) != null ? errorMessages.get(":" + code).getMessage() : null);
				dmgConsultation.getErrors().add(error);
				log.error(error.toString());
			});
			return dmgConsultation;
		}

		Kmehrmessage kmehrmessage = retrieveTransactionResponse.getKmehrmessage();

		if (kmehrmessage != null) {
			OUTER:
			for (FolderType f : kmehrmessage.getFolders()) {
				if (f.getPatient() != null) {
					dmgConsultation.setLastName(f.getPatient().getFamilyname());
					dmgConsultation.setFirstName(StringUtils.join(f.getPatient().getFirstnames(), " "));
					if (f.getPatient().getSex() != null) {
						dmgConsultation.setSex(f.getPatient().getSex().getCd().getValue().value());
					}
					if (f.getPatient().getBirthdate() != null) {
						dmgConsultation.setBirthday(Instant.ofEpochMilli(f.getPatient().getBirthdate().getDate().getMillis()));
					}
					f.getPatient().getIds().stream().filter(id -> id.getS().equals(IDPATIENTschemes.ID_PATIENT) || id.getS().equals(IDPATIENTschemes.INSS)).forEach(id -> dmgConsultation.setInss(id.getValue()));
					if (f.getPatient().getInsurancymembership() != null) {
						if (f.getPatient().getInsurancymembership().getId() != null) {
							dmgConsultation.setMutuality(f.getPatient().getInsurancymembership().getId().getValue());
						}
						if (f.getPatient().getInsurancymembership().getMembership() != null) {
							dmgConsultation.setRegNrWithMut(f.getPatient().getInsurancymembership().getMembership().toString());
						}
					}
				}
				for (be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType t : f.getTransactions()) {
					for (CDTRANSACTION cd : t.getCds()) {
						if (cd.getValue() != null && cd.getValue().toLowerCase().equals("gmd")) {
							for (ItemType i : t.getItem()) {
								for (CDITEM cdi : i.getCds()) {
									if (cdi.getValue() != null && cdi.getValue().equals("gmdmanager")) {
										if (i.getBeginmoment() != null && i.getBeginmoment().getDate() != null) {
											dmgConsultation.setFrom(Instant.ofEpochMilli(i.getBeginmoment().getDate().getMillis()));
										}
										if (i.getEndmoment() != null && i.getEndmoment().getDate() != null) {
											dmgConsultation.setTo(Instant.ofEpochMilli(i.getEndmoment().getDate().getMillis()));
										}

										for (ContentType c : i.getContents()) {
											if (c.getHcparty() != null) {
												fillHcParty(c.getHcparty(), healthcarePartyId);
												dmgConsultation.setHcParty(c.getHcparty());
												break; //Content
											}
										}
										break; //CD-ITEM
									}
									if (cdi.getValue() != null && cdi.getValue().equals("payment")) {
										for (ContentType c : i.getContents()) {
											if (c.isBoolean() != null) {
												dmgConsultation.setPayment(c.isBoolean());
												break; //Content
											}
										}
										break; //CD-ITEM
									}
								}
							}

							break OUTER;
						}
					}
				}
			}
		}
		return dmgConsultation;
	}

	private void setMemberId(Patient pI, MemberinsuranceType member) {
		if (pI.getMutuality() != null) {
			IDINSURANCE id = new IDINSURANCE();
			id.setSV("1.0");
			id.setValue(pI.getMutuality());
			member.setId(id);
		}
	}

	private void addNissToPatientIds(String patientNiss, List<IDPATIENT> ids) {
		if (patientNiss != null && !patientNiss.isEmpty()) {
			IDPATIENT idpatient = new IDPATIENT();
			idpatient.setSV("1.0");
			idpatient.setValue(patientNiss);
			idpatient.setS(IDPATIENTschemes.ID_PATIENT);
			ids.add(idpatient);
		}
	}

	public static class PostParameter {

		/**
		 * String which identifies the request, to be used in InputReference and as second part of the kmehr id.
		 */
		private String requestIdentifier;

		/**
		 * the Blob object to send with the business content.
		 */
		private Blob blob;

		/**
		 * boolean indicating its a test.
		 */
		private Boolean istest;

		/**
		 * the service name , used to retrieve parameters from config file.
		 */
		private String serviceName;

		/**
		 * boolean indicating if we must use xades or not. currently Xades is not supported and value true will cause an
		 * UnsupportedOperationException.
		 */
		private Boolean useXades;

		/**
		 * optional : the oaNumber to set in the to part of the header.
		 */
		private Integer oaNumber;

		/**
		 * the url to use for the
		 */
		private String addressingHeaderUrl;

		/**
		 *
		 */
		public PostParameter(Blob blob, Boolean istest, String serviceName, Boolean useXades, Integer oaNumber, String addressingHeaderUrl, String requestIdentifier) {
			this.blob = blob;
			this.istest = istest;
			this.serviceName = serviceName;
			this.useXades = useXades;
			this.oaNumber = oaNumber;
			this.addressingHeaderUrl = addressingHeaderUrl;
			this.requestIdentifier = requestIdentifier;
		}

		/**
		 * @param blob the blob to set
		 */
		public void setBlob(Blob blob) {
			this.blob = blob;
		}

		public String getRequestIdentifier() {
			return requestIdentifier;
		}

		public Blob getBlob() {
			return blob;
		}

		public Boolean getIstest() {
			return istest;
		}

		public String getServiceName() {
			return serviceName;
		}

		public Boolean getUseXades() {
			return useXades;
		}

		public Integer getOaNumber() {
			return oaNumber;
		}

		public String getAddressingHeaderUrl() {
			return addressingHeaderUrl;
		}


	}

	private class PatientWithDate {
		PersonType patient;
		Date date;

		public PatientWithDate(PersonType patient, Date date) {
			this.patient = patient;
			this.date = date;
		}
	}
}
