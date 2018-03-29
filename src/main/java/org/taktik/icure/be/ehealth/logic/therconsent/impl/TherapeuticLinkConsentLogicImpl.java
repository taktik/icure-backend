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

package org.taktik.icure.be.ehealth.logic.therconsent.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import be.ehealth.business.common.domain.Patient;
import be.ehealth.business.kmehrcommons.CDConsentBuilderUtil;
import be.ehealth.business.kmehrcommons.builders.HcPartyBuilder;
import be.ehealth.businessconnector.therlink.builders.CommonObjectBuilder;
import be.ehealth.businessconnector.therlink.builders.ProofBuilder;
import be.ehealth.businessconnector.therlink.domain.Author;
import be.ehealth.businessconnector.therlink.domain.HcParty;
import be.ehealth.businessconnector.therlink.domain.Proof;
import be.ehealth.businessconnector.therlink.domain.ProofTypeValues;
import be.ehealth.businessconnector.therlink.domain.TherapeuticLink;
import be.ehealth.businessconnector.therlink.domain.requests.BinaryProof;
import be.ehealth.businessconnector.therlink.domain.requests.GetTherapeuticLinkRequest;
import be.ehealth.businessconnector.therlink.domain.requests.PutTherapeuticLinkRequest;
import be.ehealth.businessconnector.therlink.domain.requests.RevokeTherapeuticLinkRequest;
import be.ehealth.businessconnector.therlink.domain.requests.TherapeuticLinkStatus;
import be.ehealth.businessconnector.therlink.domain.responses.TherapeuticLinkResponse;
import be.ehealth.businessconnector.therlink.exception.TherLinkBusinessConnectorException;
import be.ehealth.businessconnector.therlink.mappers.HcPartyMapper;
import be.ehealth.businessconnector.therlink.mappers.MapperFactory;
import be.ehealth.businessconnector.therlink.mappers.RequestObjectMapper;
import be.ehealth.businessconnector.therlink.mappers.ResponseObjectMapper;
import be.ehealth.businessconnector.therlink.session.TherLinkService;
import be.ehealth.businessconnector.therlink.session.TherlinkSessionServiceFactory;
import be.ehealth.businessconnector.wsconsent.builders.RequestObjectBuilderFactory;
import be.ehealth.businessconnector.wsconsent.exception.WsConsentBusinessConnectorException;
import be.ehealth.businessconnector.wsconsent.session.WsConsentSessionServiceFactory;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.service.sts.security.impl.BeIDCredential;
import be.fgov.ehealth.hubservices.core.v2.AuthorWithPatientType;
import be.fgov.ehealth.hubservices.core.v2.ConsentType;
import be.fgov.ehealth.hubservices.core.v2.GetPatientConsentRequest;
import be.fgov.ehealth.hubservices.core.v2.GetPatientConsentResponse;
import be.fgov.ehealth.hubservices.core.v2.PatientIdType;
import be.fgov.ehealth.hubservices.core.v2.PutPatientConsentRequest;
import be.fgov.ehealth.hubservices.core.v2.PutPatientConsentResponse;
import be.fgov.ehealth.hubservices.core.v2.PutTherapeuticLinkResponse;
import be.fgov.ehealth.hubservices.core.v2.RevokePatientConsentRequest;
import be.fgov.ehealth.hubservices.core.v2.RevokePatientConsentResponse;
import be.fgov.ehealth.hubservices.core.v2.RevokeTherapeuticLinkResponse;
import be.fgov.ehealth.hubservices.core.v2.SelectGetPatientConsentType;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONSENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONSENTvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDERROR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.ErrorType;
import be.fgov.ehealth.technicalconnector.signature.AdvancedElectronicSignatureEnumeration;
import be.fgov.ehealth.technicalconnector.signature.SignatureBuilder;
import be.fgov.ehealth.technicalconnector.signature.SignatureBuilderFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.dto.consent.ConsentMessage;
import org.taktik.icure.be.ehealth.dto.therlink.TherapeuticLinkMessage;
import org.taktik.icure.be.ehealth.logic.therconsent.TherapeuticLinkConsentLogic;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.be.ehealth.dto.Error;

@org.springframework.stereotype.Service
public class TherapeuticLinkConsentLogicImpl implements TherapeuticLinkConsentLogic {
    Log log = LogFactory.getLog(this.getClass());
    private static TherLinkService therlinkWS;
    private static final String BEID_SCOPE = "TherapeuticLinkConsentLogic";

    PatientLogic patientLogic;
    SessionLogic sessionLogic;
    HealthcarePartyLogic healthcarePartyLogic;

    @Override
    public ConsentMessage registerPatientConsent(String token, String niss, String eid, String firstName, String lastName) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException {
        if (token == null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }

        String healthcarePartyId = sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId();
        HealthcareParty party = healthcarePartyLogic.getHealthcareParty(healthcarePartyId);

		ConsentMessage cm = new ConsentMessage();

		List<CDCONSENT> consentList = new ArrayList<>();
		consentList.add(CDConsentBuilderUtil.createCDConsent("1.0", CDCONSENTvalues.RETROSPECTIVE));
		ConsentType consentType = RequestObjectBuilderFactory.getConsentBuilder().createNewConsent(getPatientForConsent(niss, eid, firstName, lastName), consentList, new DateTime(), getAuthor(party.getNihii(), party.getSsin(), party.getFirstName(), party.getLastName()));

		PutPatientConsentRequest consentRequest = RequestObjectBuilderFactory.getRequestObjectBuilder().createPutRequest(getAuthor(party.getNihii(), party.getSsin(), party.getFirstName(), party.getLastName()), consentType);
		// Service
		be.ehealth.businessconnector.wsconsent.session.WsConsentService service = WsConsentSessionServiceFactory.getWsConsentService();
		PutPatientConsentResponse response = service.putPatientConsent(consentRequest);

		if (!response.getAcknowledge().isIscomplete()) {
			cm.setComplete(false);
			for (ErrorType e : response.getAcknowledge().getErrors()) {
				List<String> errorCodes = e.getCds().stream().map(CDERROR::getValue).collect(Collectors.toList());

				cm.getErrors().add(new Error(StringUtils.join(errorCodes, ","), e.getUrl(), e.getDescription().getValue(), new HashMap<>()));
			}
		} else {
			cm.setComplete(true);
			cm.setConsent(consentType);
		}
        return cm;
    }

    @Override
    public ConsentMessage getPatientConsent(String token, String niss) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException {
        if (token == null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }

        String healthcarePartyId = sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId();
        HealthcareParty party = healthcarePartyLogic.getHealthcareParty(healthcarePartyId);

        be.ehealth.businessconnector.wsconsent.session.WsConsentService service = WsConsentSessionServiceFactory.getWsConsentService();

        List<CDCONSENT> consentList = new ArrayList<>();
        consentList.add(CDConsentBuilderUtil.createCDConsent("1.0", CDCONSENTvalues.RETROSPECTIVE));

        PatientIdType patient = getPatientForConsent(niss, null, null, null);
        SelectGetPatientConsentType consentType = RequestObjectBuilderFactory.getConsentBuilder().createSelectGetPatientConsent(patient, consentList);
        AuthorWithPatientType author = getAuthor(party.getNihii(), party.getSsin(), party.getFirstName(), party.getLastName());

        GetPatientConsentRequest consentRequest = RequestObjectBuilderFactory.getRequestObjectBuilder().createGetRequest(author, consentType);

        GetPatientConsentResponse response = service.getPatientConsent(consentRequest);

        ConsentMessage cm = new ConsentMessage();
        if (!response.getAcknowledge().isIscomplete()) {
            cm.setComplete(false);
            for (ErrorType e : response.getAcknowledge().getErrors()) {
                List<String> errorCodes = e.getCds().stream().map(CDERROR::getValue).collect(Collectors.toList());

                cm.getErrors().add(new Error(StringUtils.join(errorCodes,","), e.getUrl(), e.getDescription().getValue(), new HashMap<>()));
            }
        } else {
            cm.setComplete(true);
            cm.setConsent(response.getConsent());
        }

        return cm;
    }

    @Override
    public ConsentMessage revokePatientConsent(String token, String eid, ConsentType consentType) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException {
        if (token == null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }
        if (eid == null) {
            throw new IllegalArgumentException("patient eid is missing");
        }

        String healthcarePartyId = sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId();
        HealthcareParty party = healthcarePartyLogic.getHealthcareParty(healthcarePartyId);


        IDPATIENT idEid = new IDPATIENT();

        idEid.setS(IDPATIENTschemes.EID_CARDNO);
        idEid.setSV("1.0");
        idEid.setValue(eid);

	    consentType.getPatient().getIds().removeIf(id -> IDPATIENTschemes.EID_CARDNO.equals(id.getS()));
        consentType.getPatient().getIds().add(idEid);

		consentType.setRevokedate(new DateTime());
        AuthorWithPatientType author = getAuthor(party.getNihii(), party.getSsin(), party.getFirstName(), party.getLastName());

        RevokePatientConsentRequest consentRequest = RequestObjectBuilderFactory.getRequestObjectBuilder().createRevokeRequest(author, consentType);

        // Service
        be.ehealth.businessconnector.wsconsent.session.WsConsentService service = WsConsentSessionServiceFactory.getWsConsentService();
        RevokePatientConsentResponse response = service.revokePatientConsent(consentRequest);

        ConsentMessage cm = new ConsentMessage();
        if (!response.getAcknowledge().isIscomplete()) {
            cm.setComplete(false);
            for (ErrorType e : response.getAcknowledge().getErrors()) {
                List<String> errorCodes = e.getCds().stream().map(CDERROR::getValue).collect(Collectors.toList());
                cm.getErrors().add(new Error(StringUtils.join(errorCodes,","), e.getUrl(), e.getDescription().getValue(), new HashMap<>()));
            }
        } else {
            cm.setComplete(true);
        }

        return cm;
    }

	@Override
	public PatientIdType getPatientForConsent(String niss, String eid, String firstName, String lastName) {
		PatientIdType id = new PatientIdType();

		IDPATIENT idNiss = new IDPATIENT();

		idNiss.setS(IDPATIENTschemes.INSS);
		idNiss.setSV("1.0");
		idNiss.setValue(niss);

		id.getIds().add(idNiss);

		IDPATIENT idEid = new IDPATIENT();

		idEid.setS(IDPATIENTschemes.EID_CARDNO);
		idEid.setSV("1.0");
		idEid.setValue(eid);

		id.getIds().add(idEid);

		id.setFamilyname(lastName);
		id.setFirstname(firstName);

		return id;
	}

    private static TherLinkService getTherlinkWs() throws ConnectorException {
        if (therlinkWS == null) {
            therlinkWS = TherlinkSessionServiceFactory.getTherlinkService();
        }
        return therlinkWS;
    }


    @Override
    public List<TherapeuticLinkMessage> getAllTherapeuticLinks(String token, HcParty author, Patient patient, String type, boolean sign) throws InstantiationException, ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException {
        return getAllTherapeuticLinks(token, author, patient, null, null, type, sign);
    }

    @Override
    public List<TherapeuticLinkMessage> getAllTherapeuticLinks(String token, HcParty author, Patient patient, Date startDate, Date endDate, String type, boolean sign) throws InstantiationException, ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException {
        TherapeuticLink queryLink = new TherapeuticLink.Builder().withHcParty(author).withPatient(patient).withStartDateTime(startDate==null?null:new DateTime(startDate.getTime())).withEndDateTime(endDate == null ? null : new DateTime(endDate.getTime())).withStatus(TherapeuticLinkStatus.ACTIVE).withType(type).build();
        return getAllTherapeuticLinksWithQueryLink(token, queryLink, sign);
    }

    @Override
    public List<TherapeuticLinkMessage> getAllTherapeuticLinksWithQueryLink(String token, TherapeuticLink queryLink, boolean sign) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException {
        ProofBuilder proofBuilder = be.ehealth.businessconnector.therlink.builders.RequestObjectBuilderFactory.getProofBuilder();
		Proof proof = sign?createProofForEidSigning(queryLink.getPatient(), queryLink.getHcParty(), BeIDCredential.getInstance(BEID_SCOPE, "Signature")):queryLink.getPatient().getEidCardNumber()!=null?proofBuilder.createProofForEidReading():queryLink.getPatient().getIsiCardNumber()!=null?proofBuilder.createProofForIsiReading():null;

		return getAllTherapeuticLinksWithQueryLink(token, queryLink, proof);
    }

    private List<TherapeuticLinkMessage> getAllTherapeuticLinksWithQueryLink(String token, TherapeuticLink queryLink, Proof proof) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException {
        if (token == null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }
        String healthcarePartyId = sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId();
        HealthcareParty party = healthcarePartyLogic.getHealthcareParty(healthcarePartyId);

        Patient patient = queryLink.getPatient();

        ResponseObjectMapper responseObjectMapper = MapperFactory.getResponseObjectMapper();
        RequestObjectMapper requestObjectMapper = MapperFactory.getRequestObjectMapper();

        queryLink.setPatient(patient);
        be.ehealth.businessconnector.therlink.domain.requests.GetTherapeuticLinkRequest createGetTherapeuticLinkRequest = new GetTherapeuticLinkRequest(DateTime.now(), party.getNihii(),  getAuthorForTherLink(party.getNihii(), party.getSsin(), party.getFirstName(), party.getLastName()), queryLink, 100, proof); /*requestObjectBuilder.createGetTherapeuticLinkRequest(queryLink, proof);*/
        be.fgov.ehealth.hubservices.core.v2.GetTherapeuticLinkResponse response = getTherlinkWs().getTherapeuticLink(requestObjectMapper.mapGetTherapeuticLinkRequest(createGetTherapeuticLinkRequest));

        if (response.getAcknowledge().isIscomplete()) {
            be.ehealth.businessconnector.therlink.domain.responses.GetTherapeuticLinkResponse mappedGetTherapeuticLinkResponse = responseObjectMapper.mapJaxbToGetTherapeuticLinkResponse(response);

            List<TherapeuticLinkResponse> listOfTherapeuticLinks = mappedGetTherapeuticLinkResponse.getListOfTherapeuticLinks();

            ArrayList<TherapeuticLink> result = new ArrayList<>();
            if (listOfTherapeuticLinks != null) {
                result.addAll(listOfTherapeuticLinks);
            }
            return result.stream().map(TherapeuticLinkMessage::new).collect(Collectors.toList());
        } else {
            TherapeuticLinkMessage tlm = new TherapeuticLinkMessage();
            tlm.setComplete(response.getAcknowledge().isIscomplete());

            List<String> errorsToIgnore = new ArrayList<>();
            errorsToIgnore.add("NIP.META.TlServiceBean");

            responseObjectMapper.mapAcknowledge(response.getAcknowledge()).getListOfErrors().stream().filter(e -> !errorsToIgnore.contains(e.getErrorCode()))
                    .forEach(e -> tlm.getErrors().add(new Error(e.getErrorCode(), null, e.getErrorDescription(), new HashMap<>())));
            return Collections.singletonList(tlm);
        }
    }

    @Override
    public TherapeuticLink doesLinkExist(String token, TherapeuticLink therLink) throws InstantiationException, ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException {
        List<TherapeuticLinkMessage> allTherapeuticLinks = getAllTherapeuticLinksWithQueryLink(token, therLink, false);
        return (allTherapeuticLinks != null && allTherapeuticLinks.size() >= 1 && allTherapeuticLinks.get(0).isComplete()) ? allTherapeuticLinks.get(0).getTherapeuticLink():null;
    }

    @Override
    public TherapeuticLinkMessage registerTherapeuticLink(String token, HcParty hcp, Patient patient, Date start, Date end, String therLinkType, String comment, Boolean sign) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException {
        if (token == null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }
        String healthcarePartyId = sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId();
        HealthcareParty party = healthcarePartyLogic.getHealthcareParty(healthcarePartyId);

        CommonObjectBuilder commonBuilder = be.ehealth.businessconnector.therlink.builders.RequestObjectBuilderFactory.getCommonBuilder();
        ProofBuilder proofBuilder = be.ehealth.businessconnector.therlink.builders.RequestObjectBuilderFactory.getProofBuilder();

        ResponseObjectMapper responseObjectMapper = MapperFactory.getResponseObjectMapper();
        RequestObjectMapper requestObjectMapper = MapperFactory.getRequestObjectMapper();

        TherapeuticLink therLink = commonBuilder.createTherapeuticLink(start == null ? null : new DateTime(start.getTime()), end == null ? null : new DateTime(end.getTime()), patient, "persphysician", therLinkType, comment, hcp);

	    List<TherapeuticLinkMessage> allTherapeuticLinks = getAllTherapeuticLinksWithQueryLink(token, therLink, false);
	    for (TherapeuticLinkMessage existingLink: allTherapeuticLinks) {
		    if (existingLink.isComplete()) { revokeLink(patient, hcp, token, existingLink.getTherapeuticLink(), patient.getEidCardNumber());}
	    }

        Proof proof = sign?createProofForEidSigning(patient, hcp, BeIDCredential.getInstance(BEID_SCOPE, "Signature")):patient.getEidCardNumber()!=null?proofBuilder.createProofForEidReading():patient.getIsiCardNumber()!=null?proofBuilder.createProofForIsiReading():null;

        be.ehealth.businessconnector.therlink.domain.requests.PutTherapeuticLinkRequest createPutTherapeuticLinkRequest = new PutTherapeuticLinkRequest(new DateTime(), party.getNihii(), getAuthorForTherLink(party.getNihii(), party.getSsin(), party.getFirstName(), party.getLastName()), therLink, proof);
        be.fgov.ehealth.hubservices.core.v2.PutTherapeuticLinkRequest mapPutTherapeuticLinkRequest = requestObjectMapper.mapPutTherapeuticLinkRequest(createPutTherapeuticLinkRequest);
        PutTherapeuticLinkResponse putResponse = getTherlinkWs().putTherapeuticLink(mapPutTherapeuticLinkRequest);


        TherapeuticLinkMessage tlm = new TherapeuticLinkMessage();
        tlm.setComplete(putResponse.getAcknowledge().isIscomplete());

        List<String> errorsToIgnore = new ArrayList<>();
        errorsToIgnore.add("NIP.META.TlServiceBean");

        tlm.setTherapeuticLink(therLink);
        responseObjectMapper.mapAcknowledge(putResponse.getAcknowledge()).getListOfErrors().stream().filter(e -> !errorsToIgnore.contains(e.getErrorCode()))
                .forEach(e -> tlm.getErrors().add(new Error(e.getErrorCode(), null, e.getErrorDescription(), new HashMap<>())));
        return tlm;
    }

    @Override
    public TherapeuticLinkMessage revokeLink(Patient patient, HcParty hcp, String token, TherapeuticLink therLink, String eidCardNumber) throws ConnectorException, InstantiationException, TokenNotAvailableException, EidSessionCreationFailedException {
        if (token == null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }
        Patient patientFromTherapeuticLink = therLink.getPatient();
        // add eidcard number to retrieved therapeutic link, so revoke with eidReading is possible
        patientFromTherapeuticLink.setEidCardNumber(eidCardNumber);

		ProofBuilder proofBuilder = be.ehealth.businessconnector.therlink.builders.RequestObjectBuilderFactory.getProofBuilder();
		Proof proof = patient.getEidCardNumber()!=null?proofBuilder.createProofForEidReading():patient.getIsiCardNumber()!=null?proofBuilder.createProofForIsiReading():null;

        RequestObjectMapper requestObjectMapper = MapperFactory.getRequestObjectMapper();
        ResponseObjectMapper responseObjectMapper = MapperFactory.getResponseObjectMapper();

        be.ehealth.businessconnector.therlink.domain.requests.RevokeTherapeuticLinkRequest createRevokeTherapeuticLinkRequest = this.createRevokeTherapeuticLinkRequestWithProof(therLink, proof);

        be.fgov.ehealth.hubservices.core.v2.RevokeTherapeuticLinkRequest mapRevokeTherapeuticLinkRequest = requestObjectMapper.mapRevokeTherapeuticLinkRequest(createRevokeTherapeuticLinkRequest);

        RevokeTherapeuticLinkResponse response = getTherlinkWs().revokeTherapeuticLink(mapRevokeTherapeuticLinkRequest);

        TherapeuticLinkMessage tlm = new TherapeuticLinkMessage();
        tlm.setComplete(response.getAcknowledge().isIscomplete());

        List<String> errorsToIgnore = new ArrayList<>();
        errorsToIgnore.add("NIP.META.TlServiceBean");

        tlm.setTherapeuticLink(therLink);
        responseObjectMapper.mapAcknowledge(response.getAcknowledge()).getListOfErrors().stream().filter(e -> !errorsToIgnore.contains(e.getErrorCode()))
                .forEach(e -> tlm.getErrors().add(new Error(e.getErrorCode(), null, e.getErrorDescription(), new HashMap<>())));
        return tlm;
    }

	private RevokeTherapeuticLinkRequest createRevokeTherapeuticLinkRequestWithProof(TherapeuticLink link, Proof proof) throws TechnicalConnectorException, TherLinkBusinessConnectorException, InstantiationException {
		DateTime startDate = link.getStartDate() == null?null:link.getStartDate().toDateTime(LocalTime.MIDNIGHT);
		DateTime endDate = link.getEndDate() == null?null:link.getEndDate().toDateTime(LocalTime.MIDNIGHT);
		return this.createRevokeTherapeuticLinkRequest(startDate, endDate, link.getPatient(), link.getHcParty(), link.getType(), link.getComment(), proof);
	}

	private RevokeTherapeuticLinkRequest createRevokeTherapeuticLinkRequest(DateTime startDate, DateTime endDate, Patient patient, HcParty hcp, String therLinkType, String commentAboutRevokal, Proof proof) throws InstantiationException, TherLinkBusinessConnectorException, TechnicalConnectorException {
		if(patient != null && hcp != null && therLinkType != null) {
			CommonObjectBuilder commonBuilder = be.ehealth.businessconnector.therlink.builders.RequestObjectBuilderFactory.getCommonBuilder();

			String nihii = hcp.getIds().stream().filter(id-> IDHCPARTYschemes.ID_HCPARTY.equals(id.getS())).map(IDHCPARTY::getValue).findFirst().orElse(null);
			if (null == nihii) { nihii = hcp.getNihii(); }
			String inss = hcp.getIds().stream().filter(id-> IDHCPARTYschemes.INSS.equals(id.getS())).map(IDHCPARTY::getValue).findFirst().orElse(null);
			if (null == inss) { inss = hcp.getInss(); }

			Author createAuthor = getAuthorForTherLink(nihii, inss, hcp.getFirstName(), hcp.getFamilyName());
			DateTime newDate = new DateTime();
			DateTime startDateNotNull = startDate == null?newDate:startDate;
			TherapeuticLink createTherapeuticLink = commonBuilder.createTherapeuticLink(startDateNotNull, endDate, patient, "persphysician", therLinkType, commentAboutRevokal, hcp);
			return new RevokeTherapeuticLinkRequest(newDate, nihii, createAuthor, createTherapeuticLink, proof);
		} else {
			throw new IllegalArgumentException("Patient, HcParty and Therapeutic link type are required to create a RevokeTherapeutiCLinkType");
		}
	}


	@Override
    public List<TherapeuticLinkMessage> revokeNonReferrals(String token, HcParty hcParty, Patient patient) throws InstantiationException, ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException {
        TherapeuticLink queryLink = new TherapeuticLink.Builder().withHcParty(hcParty).withPatient(patient).withStatus(TherapeuticLinkStatus.ACTIVE).build();

        Proof proof = createProofForEidSigning(patient, hcParty, BeIDCredential.getInstance(BEID_SCOPE, "Signature"));

        List<TherapeuticLinkMessage> allTherapeuticLinks = getAllTherapeuticLinksWithQueryLink(token, queryLink, proof);

        if (allTherapeuticLinks==null || allTherapeuticLinks.size()<1 || !allTherapeuticLinks.get(0).isComplete()) { return allTherapeuticLinks; }

        return revokeAllLinks(token, allTherapeuticLinks.stream().map(TherapeuticLinkMessage::getTherapeuticLink).collect(Collectors.toList()), patient, proof);
    }

    @Override
    public List<TherapeuticLinkMessage> revokeAllLinks(String token, List<TherapeuticLink> listOfTherapeuticLinks, Patient patient, Proof proof) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException {
        if (token == null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }
        if (proof == null) { proof = be.ehealth.businessconnector.therlink.builders.RequestObjectBuilderFactory.getProofBuilder().createProofForEidSigning(BeIDCredential.getInstance(BEID_SCOPE, "Signature")); }

        ResponseObjectMapper responseObjectMapper = MapperFactory.getResponseObjectMapper();

        List<TherapeuticLinkMessage> result = new ArrayList<>();
        if (listOfTherapeuticLinks != null) {
            log.debug("revoking " + listOfTherapeuticLinks.size() + " links");
            RequestObjectMapper requestObjectMapper = MapperFactory.getRequestObjectMapper();
            for (TherapeuticLink retrievedLink : listOfTherapeuticLinks) {
                retrievedLink.setPatient(fillTherlinkPatientWithCorrectDataForProof(retrievedLink.getPatient(), patient, proof));
                be.ehealth.businessconnector.therlink.domain.requests.RevokeTherapeuticLinkRequest revokeTherapeuticLinkRequest = createRevokeTherapeuticLinkRequest(retrievedLink.getStartDate().toDateTimeAtStartOfDay(), retrievedLink.getEndDate().toDateTimeAtStartOfDay(), retrievedLink.getPatient(), retrievedLink.getHcParty(), retrievedLink.getType(), "revokeAllLinks : therLink to revoke existing link ", proof);
                RevokeTherapeuticLinkResponse revokeTherapeuticLinkResponse = getTherlinkWs().revokeTherapeuticLink(requestObjectMapper.mapRevokeTherapeuticLinkRequest(revokeTherapeuticLinkRequest));

                // ignore error messages with 'no therapeutic link found' = NIP.META.TlServiceBean
                List<String> errorsToIgnore = new ArrayList<>();
                errorsToIgnore.add("NIP.META.TlServiceBean");

                TherapeuticLinkMessage tlm = new TherapeuticLinkMessage();
                result.add(tlm);
                tlm.setComplete(revokeTherapeuticLinkResponse.getAcknowledge().isIscomplete());

                tlm.setTherapeuticLink(retrievedLink);
                responseObjectMapper.mapAcknowledge(revokeTherapeuticLinkResponse.getAcknowledge()).getListOfErrors().stream().filter(e -> !errorsToIgnore.contains(e.getErrorCode()))
                        .forEach(e -> tlm.getErrors().add(new Error(e.getErrorCode(), null,  e.getErrorDescription(), new HashMap<>())));
            }
        }

        return result;
    }

	private Patient fillTherlinkPatientWithCorrectDataForProof(Patient therlinkPatient, Patient templatePatient, Proof proof) throws TechnicalConnectorException {
		log.debug("replacing patient in therLink with data from templatePatient");
		if (therlinkPatient == null || therlinkPatient.getInss() == null) {
			throw new IllegalArgumentException("the therLink , its patient and the niss number should be filled out!");
		}
		if (templatePatient == null) {
			templatePatient = getPatientForTherapeuticLink(therlinkPatient.getInss(), therlinkPatient.getEidCardNumber(), therlinkPatient.getIsiCardNumber(), therlinkPatient.getFirstName(), therlinkPatient.getLastName());
		}
		if (templatePatient == null || templatePatient.getInss() == null) {
			throw new IllegalArgumentException("the patient to use as template and its niss should be filled out!!");
		}
		if (!templatePatient.getInss().equals(therlinkPatient.getInss())) {
			throw new IllegalArgumentException("templatePatient is not for same niss number!!");
		}
		Patient patientToUse = new Patient.Builder().withFamilyName(templatePatient.getLastName()).withFirstName(templatePatient.getFirstName()).withInss(templatePatient.getInss()).build();

		if (proof.getType().equals(ProofTypeValues.EIDREADING.getValue())) {
			String cardNumber = templatePatient.getEidCardNumber();
			if (cardNumber == null) {
				throw new IllegalArgumentException(" the template patient used is missing the required cardNumber for Proof " + ProofTypeValues.EIDREADING.getValue());
			}
			patientToUse.setEidCardNumber(cardNumber);
		} else if (proof.getType().equals(ProofTypeValues.ISIREADING.getValue())) {
			String cardNumber = templatePatient.getIsiCardNumber();
			if (cardNumber == null) {
				throw new IllegalArgumentException(" the template patient used is missing the required cardNumber for Proof " + ProofTypeValues.ISIREADING.getValue());
			}
			patientToUse.setIsiCardNumber(cardNumber);
		} else if (proof.getType().equals(ProofTypeValues.SISREADING.getValue())) {
			String cardNumber = templatePatient.getSisCardNumber();
			if (cardNumber == null) {
				throw new IllegalArgumentException(" the template patient used is missing the required cardNumber for Proof " + ProofTypeValues.SISREADING.getValue());
			}
			patientToUse.setSisCardNumber(cardNumber);
		} else if (proof.getType().equals(ProofTypeValues.EIDSIGNING.getValue())) {
			String cardNumber = templatePatient.getEidCardNumber();
			if (cardNumber == null) {
				throw new IllegalArgumentException(" the template patient used is missing the required cardNumber for Proof " + ProofTypeValues.EIDSIGNING.getValue());
			}
			patientToUse.setEidCardNumber(cardNumber);
		} else {
			throw new UnsupportedOperationException("fillTherlinkPatientWithCorrectDataForProof : unsupported proofType " + proof.getType());
		}
		return patientToUse;
	}

	private Proof createProofForEidSigning(Patient patient, HcParty hcp, BeIDCredential signature) throws InstantiationException, TherLinkBusinessConnectorException, TechnicalConnectorException {
		Proof proof = new Proof(ProofTypeValues.EIDSIGNING.getValue());

		CommonObjectBuilder commonBuilder = be.ehealth.businessconnector.therlink.builders.RequestObjectBuilderFactory.getCommonBuilder();
		TherapeuticLink therapeuticLink =  commonBuilder.createTherapeuticLink(new DateTime(), new DateTime().plusMinutes(5), patient, "persphysician", "ignored", null, hcp);
		RequestObjectMapper requestObjectMapper = MapperFactory.getRequestObjectMapper();
		String contentToSign = requestObjectMapper.createTherapeuticLinkAsXmlString(therapeuticLink);
		SignatureBuilder signatureBuilder = SignatureBuilderFactory.getSignatureBuilder(AdvancedElectronicSignatureEnumeration.CAdES);
		Map<String,Object> options = new HashMap<>();
		options.put("encapsulate", Boolean.TRUE);
		byte[] signatureBytes = signatureBuilder.sign(signature, contentToSign.getBytes(), options);
		BinaryProof binaryProof = new BinaryProof("CMS", signatureBytes);
		proof.setBinaryProof(binaryProof);

		return proof;
	}

    @Override
    public Patient getPatientForTherapeuticLink(String niss, String eid, String isi, String firstName, String lastName) {
        return new Patient.Builder().withEid(eid).withIsiPlus(isi).withFamilyName(lastName).withFirstName(firstName).withInss(niss).build();
    }

    @Override
    public HcParty getHcParty(String nihii, String inss, String firstname, String lastname) {
        try {
            return HcPartyMapper.mapHcParty(new HcPartyBuilder().idHcPartyId(nihii, "1.0").inssId(inss, "1.0").cdHcPartyCd("persphysician","1.0").firstname(firstname).lastname(lastname).build());
        } catch (TechnicalConnectorException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private AuthorWithPatientType getAuthor(String nihii, String inss, String firstname, String lastname) throws TechnicalConnectorException, WsConsentBusinessConnectorException, InstantiationException {
        AuthorWithPatientType author = new AuthorWithPatientType();
        author.getHcparties().add(new HcPartyBuilder().idHcPartyId(nihii, "1.0").inssId(inss, "1.0").cdHcPartyCd("persphysician","1.0").firstname(firstname).lastname(lastname).build());

        return author;
    }


    private Author getAuthorForTherLink(String nihii, String inss, String firstname, String lastname) throws TechnicalConnectorException, InstantiationException, TherLinkBusinessConnectorException {
        Author author = new Author();
        author.getHcParties().add(new HcParty.Builder().withNihii(nihii).withInss(inss).withType("persphysician").withFirstName(firstname).withFamilyName(lastname).build());

        return author;
    }

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
}
