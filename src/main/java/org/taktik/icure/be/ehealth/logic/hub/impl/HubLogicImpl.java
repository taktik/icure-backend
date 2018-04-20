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

package org.taktik.icure.be.ehealth.logic.hub.impl;

import java.security.KeyStoreException;
import java.security.cert.CertificateExpiredException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import be.ehealth.businessconnector.hub.builders.BuilderFactory;
import be.ehealth.businessconnector.hub.session.HubService;
import be.ehealth.businessconnector.hub.session.HubSessionServiceFactory;
import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.config.Configuration;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v1.ConsentHCPartyType;
import be.fgov.ehealth.hubservices.core.v1.ConsentType;
import be.fgov.ehealth.hubservices.core.v1.HCPartyIdType;
import be.fgov.ehealth.hubservices.core.v1.KmehrHeaderGetTransactionList;
import be.fgov.ehealth.hubservices.core.v1.LocalSearchType;
import be.fgov.ehealth.hubservices.core.v1.PatientIdType;
import be.fgov.ehealth.hubservices.core.v1.SelectGetHCPartyPatientConsentType;
import be.fgov.ehealth.hubservices.core.v1.SelectGetPatientConsentType;
import be.fgov.ehealth.hubservices.core.v1.TherapeuticLinkType;
import be.fgov.ehealth.hubservices.core.v1.TransactionBaseType;
import be.fgov.ehealth.hubservices.core.v1.TransactionSummaryType;
import be.fgov.ehealth.hubservices.core.v1.TransactionWithPeriodType;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONSENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONSENTschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONSENTvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDSEX;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDSEXvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTHERAPEUTICLINK;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTHERAPEUTICLINKschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType;
import be.fgov.ehealth.standards.kmehr.schema.v1.DateType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.PersonType;
import be.fgov.ehealth.standards.kmehr.schema.v1.SexType;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.dto.common.Consent;
import org.taktik.icure.be.ehealth.dto.common.HcPartyConsent;
import org.taktik.icure.be.ehealth.dto.common.HubTherapeuticLink;
import org.taktik.icure.be.ehealth.logic.hub.HubLogic;
import org.taktik.icure.be.ehealth.logic.sts.STSLogic;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.embed.Gender;
import org.taktik.icure.logic.ContactLogic;
import org.taktik.icure.logic.DocumentLogic;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.logic.SessionLogic;

/**
 * Created by aduchate on 9/11/13, 11:24
 */
@org.springframework.stereotype.Service
public class HubLogicImpl implements HubLogic{
    Log log = LogFactory.getLog(this.getClass());

    protected STSLogic stsLogic;
    protected MapperFacade mapper;
    protected DocumentLogic documentLogic;
    protected ContactLogic contactLogic;
    PatientLogic patientLogic;
    SessionLogic sessionLogic;
    HealthcarePartyLogic healthcarePartyLogic;

    private static List<String> expectedProps = new ArrayList<>();
    private static Configuration config = ConfigFactory.getConfigValidator(expectedProps);

    @Autowired
    public void setPatientLogic(PatientLogic patientLogic) {
        this.patientLogic = patientLogic;
    }

    @Autowired
    public void setDocumentLogic(DocumentLogic documentLogic) {
        this.documentLogic = documentLogic;
    }

    @Autowired
    public void setContactLogic(ContactLogic contactLogic) {
        this.contactLogic = contactLogic;
    }

    @Autowired
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

    @Autowired
    public void setStsLogic(STSLogic stsLogic) {
        this.stsLogic = stsLogic;
    }

    @Autowired
    public void setSessionLogic(SessionLogic sessionLogic) {
        this.sessionLogic = sessionLogic;
    }

    @Autowired
    public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
        this.healthcarePartyLogic = healthcarePartyLogic;
    }

    @Override
    public Consent getPatientConsent(String token, String nissPatient) throws ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException {
        if (token==null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }

        HubService hubs = HubSessionServiceFactory.getHubService();


        SelectGetPatientConsentType patient = new SelectGetPatientConsentType();

        PatientIdType patientId = new PatientIdType();
        IDPATIENT kmehrPatientId = new IDPATIENT();
        kmehrPatientId.setS(IDPATIENTschemes.INSS);
        kmehrPatientId.setSV("1.0");
        kmehrPatientId.setValue(nissPatient);
        patientId.getIds().add(kmehrPatientId);

        patient.setPatient(patientId);

        ConsentType result = hubs.getPatientConsent(patient);
        return (result != null) ? mapper.map(result, Consent.class) : null;
    }

    @Override
    public List<HubTherapeuticLink> getTherapeuticLinks(String token, String nissPatient, String inamiDoctor, String nissDoctor) throws ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException {
        if (token==null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }

        SelectGetHCPartyPatientConsentType link = new SelectGetHCPartyPatientConsentType();
        //link.setBegindate(Calendar.getInstance());
        //link.setEnddate(Calendar.getInstance());

        PatientIdType patient = new PatientIdType();

        IDPATIENT kmehrPatientId = new IDPATIENT();
        kmehrPatientId.setS(IDPATIENTschemes.INSS);
        kmehrPatientId.setSV("1.0");
        kmehrPatientId.setValue(nissPatient);
        patient.getIds().add(kmehrPatientId);

        HCPartyIdType doctor = null;

        if (inamiDoctor!=null || nissDoctor!=null) {
            doctor = new HCPartyIdType();

            IDHCPARTY party = new IDHCPARTY();
            party.setS(inamiDoctor!=null?IDHCPARTYschemes.ID_HCPARTY:IDHCPARTYschemes.INSS);
            party.setSV("1.0");
            party.setValue(inamiDoctor!=null?inamiDoctor:nissDoctor);
            doctor.getIds().add(party);
        }

        link.getPatientsAndHcparties().add(patient);
        if (doctor!=null) {
            link.getPatientsAndHcparties().add(doctor);
        }

        CDTHERAPEUTICLINK linkType = new CDTHERAPEUTICLINK();
        linkType.setSV("1.0");
        linkType.setS(CDTHERAPEUTICLINKschemes.CD_THERAPEUTICLINKTYPE);
        linkType.setValue("gmd");
        link.getCds().add(linkType);

		linkType = new CDTHERAPEUTICLINK();
		linkType.setSV("1.0");
		linkType.setS(CDTHERAPEUTICLINKschemes.CD_THERAPEUTICLINKTYPE);
		linkType.setValue("gpconsultation");
		link.getCds().add(linkType);

		HubService hubs = HubSessionServiceFactory.getHubService();
        Collection<TherapeuticLinkType> therapeuticLinkList = hubs.getTherapeuticLink(link);


        return therapeuticLinkList.stream().map((tl)->mapper.map(tl,HubTherapeuticLink.class)).collect(Collectors.toList());
    }

    @Override
    public void putPatient(String token, String niss, String firstName, String lastName, Gender gender, LocalDateTime dateOfBirth) throws ConnectorException, TokenNotAvailableException {
        if (token == null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }

        HubService hubs = HubSessionServiceFactory.getHubService();
        //Put patient first

        if (dateOfBirth != null && lastName != null && firstName != null && gender != null) {
            PersonType patient = new PersonType();

            IDPATIENT idpatient = new IDPATIENT();
            idpatient.setS(IDPATIENTschemes.INSS);
            idpatient.setSV("1.0");
            idpatient.setValue(niss);

            patient.getIds().add(idpatient);
            patient.setFamilyname(lastName);
            patient.getFirstnames().add(firstName);
            DateType birthdate = new DateType();
            birthdate.setDate(new DateTime(dateOfBirth.toInstant(ZoneOffset.of(ZoneId.systemDefault().getId()))));
            patient.setBirthdate(birthdate);
            SexType sex = new SexType();

            CDSEX cdsex = new CDSEX();
            cdsex.setS("CD-SEX");
            cdsex.setSV("1.0");
            cdsex.setValue(gender==Gender.male ? CDSEXvalues.MALE:gender==Gender.female ? CDSEXvalues.FEMALE:gender==Gender.changed?CDSEXvalues.CHANGED:CDSEXvalues.UNKNOWN);
            sex.setCd(cdsex);

            patient.setSex(sex);

            hubs.putPatient(patient);
        }
    }

    @Override
    public void registerPatientConsent(String token, LocalDateTime dateOfBirth, String niss, String firstName, String lastName, Gender gender) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException {
        if (token == null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }

        String healthcarePartyId = sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId();
        HealthcareParty party = healthcarePartyLogic.getHealthcareParty(healthcarePartyId);

        HubService hubs = HubSessionServiceFactory.getHubService();

        putPatient(token, niss, firstName, lastName, gender, dateOfBirth);

        CDHCPARTY cdHcParty = new CDHCPARTY();
        cdHcParty.setS(CDHCPARTYschemes.CD_HCPARTY);
        cdHcParty.setSV("1.0");
        cdHcParty.setValue(CDHCPARTYvalues.PERSPHYSICIAN.value());

        IDHCPARTY idHcparty = new IDHCPARTY();
        idHcparty.setS(IDHCPARTYschemes.INSS);
        idHcparty.setSV("1.0");
        idHcparty.setValue(party.getSsin());

        HcpartyType hcParty = new HcpartyType();
        hcParty.setFamilyname(party.getLastName());
        hcParty.setFirstname(party.getFirstName());
        hcParty.getCds().add(cdHcParty);
        hcParty.getIds().add(idHcparty);

        IDPATIENT idPatient = new IDPATIENT();
        idPatient.setS(IDPATIENTschemes.INSS);
        idPatient.setSV("1.0");
        idPatient.setValue(niss);

        be.fgov.ehealth.hubservices.core.v1.PatientIdType patient = new be.fgov.ehealth.hubservices.core.v1.PatientIdType();
        patient.getIds().add(idPatient);

        AuthorType author = new AuthorType();
        author.getHcparties().add(hcParty);

        be.fgov.ehealth.hubservices.core.v1.ConsentType consent = new be.fgov.ehealth.hubservices.core.v1.ConsentType();
        consent.setAuthor(author);
        consent.setPatient(patient);
        consent.setSigndate(new DateTime());
        CDCONSENT cdConsent = new CDCONSENT();
        cdConsent.setS(CDCONSENTschemes.CD_CONSENTTYPE);
        cdConsent.setSV("1.0");
        cdConsent.setValue(CDCONSENTvalues.RETROSPECTIVE);
        consent.getCds().add(cdConsent);

        hubs.putPatientConsent(consent);
    }

    @Override
    public void registerTherapeuticLink(String token, String ssin, Date start, String comment) throws TokenNotAvailableException, ConnectorException {
        if (token == null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }

        String healthcarePartyId = sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId();
        HealthcareParty party = healthcarePartyLogic.getHealthcareParty(healthcarePartyId);

        HubService hubs = HubSessionServiceFactory.getHubService();

        CDHCPARTY cdHcParty = new CDHCPARTY();
        cdHcParty.setS(CDHCPARTYschemes.CD_HCPARTY);
        cdHcParty.setSV("1.0");
        cdHcParty.setValue(CDHCPARTYvalues.PERSPHYSICIAN.value());

        IDHCPARTY idHcparty = new IDHCPARTY();
        idHcparty.setS(IDHCPARTYschemes.INSS);
        idHcparty.setSV("1.0");
        idHcparty.setValue(party.getSsin());

        HcpartyType hcParty = new HcpartyType();
        hcParty.setFamilyname(party.getLastName());
        hcParty.setFirstname(party.getFirstName());
        hcParty.getCds().add(cdHcParty);
        hcParty.getIds().add(idHcparty);

        HCPartyIdType hcPartyId = new HCPartyIdType();
        hcPartyId.getIds().add(idHcparty);


        IDPATIENT idPatient = new IDPATIENT();
        idPatient.setS(IDPATIENTschemes.INSS);
        idPatient.setSV("1.0");
        idPatient.setValue(ssin);

        be.fgov.ehealth.hubservices.core.v1.PatientIdType patient = new be.fgov.ehealth.hubservices.core.v1.PatientIdType();
        patient.getIds().add(idPatient);

        AuthorType author = new AuthorType();
        author.getHcparties().add(hcParty);

        TherapeuticLinkType therapeuticLink = new TherapeuticLinkType();
        CDTHERAPEUTICLINK cdTherLink = new CDTHERAPEUTICLINK();
        cdTherLink.setS(CDTHERAPEUTICLINKschemes.CD_THERAPEUTICLINKTYPE);
        cdTherLink.setSV("1.0");
        cdTherLink.setValue("gpconsultation");
        therapeuticLink.setCd(cdTherLink);
        therapeuticLink.setComment(comment);
        therapeuticLink.setHcparty(hcPartyId);
        therapeuticLink.setPatient(patient);
        therapeuticLink.setStartdate(new DateTime(start.getTime()));

        hubs.putTherapeuticLink(therapeuticLink);
    }


    @Override
    public List<org.taktik.icure.be.ehealth.dto.common.TransactionSummaryType> getTransactionsList(String token, String nissPatient, String documentType, Calendar from, Calendar to, String hcPartyType, String inamiHcParty, String nissHcParty, boolean isGlobal) throws ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException {
        if (token==null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }
        /*
         * Create the request
         */
        // Patient for which to retrieve the transaction list
        PatientIdType patient = new PatientIdType();
        IDPATIENT kmehrPatientId = new IDPATIENT();
        kmehrPatientId.setS(IDPATIENTschemes.INSS);
        kmehrPatientId.setSV("1.0");
        kmehrPatientId.setValue(nissPatient);
        patient.getIds().add(kmehrPatientId);

        // Transaction types to search
        TransactionWithPeriodType transaction = new TransactionWithPeriodType();

        if (from != null) { transaction.setBegindate(new DateTime(from.getTime())); }
        if (to != null) { transaction.setEnddate(new DateTime(to.getTime())); }
        if (documentType!=null && !documentType.equals("*")) {
            CDTRANSACTION trans = new CDTRANSACTION();
            trans.setS(CDTRANSACTIONschemes.CD_TRANSACTION);
            trans.setSV("1.0");
            trans.setValue(documentType);
            transaction.getCds().add(trans);
        }

        if (nissHcParty!=null || inamiHcParty!=null) {
            if (StringUtils.isEmpty(hcPartyType)) { hcPartyType = "*"; }
            AuthorType author = new AuthorType();
            HcpartyType party = new HcpartyType();
            author.getHcparties().add(party);

            IDHCPARTY id = new IDHCPARTY();
            id.setSV("1.0");
            if (StringUtils.isEmpty(inamiHcParty)) {
                id.setS(IDHCPARTYschemes.INSS);
                id.setValue(nissHcParty);
            } else {
                id.setS(IDHCPARTYschemes.ID_HCPARTY);
                id.setValue(inamiHcParty);
            }

            party.getIds().add(id);

            CDHCPARTY cd = new CDHCPARTY();
            cd.setS(CDHCPARTYschemes.CD_HCPARTY);
            cd.setSV("1.1");
            cd.setValue(hcPartyType);
            party.getCds().add(cd);

            transaction.setAuthor(author);
        }


        for (int i = 0; i < 1; i++) {
            try {
                HubService hubs = HubSessionServiceFactory.getHubService();
                KmehrHeaderGetTransactionList transactionList = hubs.getTransactionList(patient, isGlobal ? LocalSearchType.GLOBAL : LocalSearchType.LOCAL, transaction);

                if (transactionList == null) {
                    return new ArrayList<>();
                } else {
                    List<TransactionSummaryType> transactions = transactionList.getFolder().getTransactions();

                    return (List<org.taktik.icure.be.ehealth.dto.common.TransactionSummaryType>) CollectionUtils.collect(transactions, tst -> {
						org.taktik.icure.be.ehealth.dto.common.TransactionSummaryType summaryType = new org.taktik.icure.be.ehealth.dto.common.TransactionSummaryType();

						summaryType.setAuthor(tst.getAuthor());
						summaryType.setIds(tst.getIds());
						summaryType.setCds(tst.getCds());
						summaryType.setDate(tst.getDate().toGregorianCalendar());
						summaryType.setTime(tst.getTime().toGregorianCalendar());
						summaryType.setRecorddatetime(Instant.ofEpochMilli(tst.getRecorddatetime().toInstant().getMillis()));

						summaryType.refreshAuthorsList();
						summaryType.refreshDesc();
						summaryType.refreshDateTime();

						return summaryType;
					});
                }
            } catch (SOAPFaultException e) {
                log.warn(e);
            }
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        throw new IllegalStateException("Could not connect to Hub");
    }

    @Override
    public String getTransaction(String token, String nissPatient, String transactionSl, String transactionSv, String transactionId) throws ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException {
        if (token==null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }

        /*
         * Create the request
         */
        // Patient for which to retrieve the transaction list
        PatientIdType patient = new PatientIdType();
        IDPATIENT kmehrPatientId = new IDPATIENT();
        kmehrPatientId.setS(IDPATIENTschemes.INSS);
        kmehrPatientId.setSV("1.0");
        kmehrPatientId.setValue(nissPatient);
        patient.getIds().add(kmehrPatientId);

        // Transaction identification
        TransactionBaseType transaction = new TransactionBaseType();
        IDKMEHR transactionKmehrId = new IDKMEHR();
        transactionKmehrId.setS(IDKMEHRschemes.LOCAL);
        transactionKmehrId.setSV(transactionSv);
        transactionKmehrId.setSL(transactionSl);
        transactionKmehrId.setValue(transactionId);
        transaction.setId(transactionKmehrId);

        /*
         * Invoke the Business Connector
         */
        HubService hubs = HubSessionServiceFactory.getHubService();
        String kmehr;
        try {
            Kmehrmessage kmehrMessage = hubs.getTransaction(patient, transaction);
            kmehr = BuilderFactory.getInstance().getResponseBuilder().buildKmehrmessageResponse(kmehrMessage);
        } catch (WebServiceException e) {
            throw new IllegalStateException("Could not connect to Hub");
        }
        return kmehr;
    }

    @Override
    public void putTransaction(String token, String transaction) throws ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException {
        if (token==null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }

        /*
         * Invoke the Business Connector
         */
        HubService hubs = HubSessionServiceFactory.getHubService();
        try {
            Kmehrmessage msg = BuilderFactory.getInstance().getRequestBuilder().buildKmehrmessage(transaction);
            hubs.putTransaction(msg);
        } catch (WebServiceException e) {
            throw new IllegalStateException("Could not connect to Hub");
        }
    }

    @Override
    public void updateHubId(String identifier, String name, String wsdl, String endpoint) throws TechnicalConnectorException {
        config.setProperty("hub.id", identifier);
        config.setProperty("hub.name", name);
        config.setProperty("endpoint.hub.intra", endpoint);

        stsLogic.revokeToken();
    }

    @Override
    public HcPartyConsent checkHcPartyConsent(String token, String inss, String nihii) throws ConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, KeyStoreException, CertificateExpiredException {
        if (token==null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }

        HubService hubs = HubSessionServiceFactory.getHubService();
        HCPartyIdType hcPartyIdType = new HCPartyIdType();

        IDHCPARTY idhcparty = new IDHCPARTY();

        idhcparty.setS(nihii!=null?IDHCPARTYschemes.ID_HCPARTY:IDHCPARTYschemes.INSS);
        idhcparty.setSV("1.0");
        idhcparty.setValue(nihii!=null?nihii:inss);

        hcPartyIdType.getIds().add(idhcparty);

        ConsentHCPartyType hcPartyConsent = hubs.getHCPartyConsent(hcPartyIdType);

        HcPartyConsent partyConsent = null;
        if (hcPartyConsent!= null) {
            partyConsent = mapper.map(hcPartyConsent, HcPartyConsent.class);
            partyConsent.setHcparty(hcPartyConsent.getHcparty());
            partyConsent.setHubId(config.getProperty("hub.id"));
        }

        return partyConsent;
    }
}
