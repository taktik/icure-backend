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

package org.taktik.icure.be.ehealth.logic.generalinsurability.impl;

import be.ehealth.businessconnector.genins.builders.RequestObjectBuilderFactory;
import be.ehealth.businessconnector.genins.domain.RequestParameters;
import be.ehealth.businessconnector.genins.session.GenInsSessionServiceFactory;
import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.config.Configuration;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.session.SessionItem;
import be.fgov.ehealth.genericinsurability.core.v1.*;
import be.fgov.ehealth.genericinsurability.protocol.v1.GetInsurabilityAsXmlOrFlatRequestType;
import be.fgov.ehealth.genericinsurability.protocol.v1.GetInsurabilityResponse;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.be.ehealth.dto.insurability.HospitalizedInfo;
import org.taktik.icure.be.ehealth.dto.insurability.InsurabilityInfo;
import org.taktik.icure.be.ehealth.dto.insurability.InsurabilityItem;
import org.taktik.icure.be.ehealth.dto.insurability.MedicalHouseInfo;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.logic.generalinsurability.GeneralInsurabilityLogic;
import org.taktik.icure.be.ehealth.logic.sts.STSLogic;

import java.security.KeyStoreException;
import java.security.cert.CertificateExpiredException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 28/05/13
 * Time: 08:59
 * To change this template use File | Settings | File Templates.
 */
@org.springframework.stereotype.Service
public class GeneralInsurabilityLogicImpl implements GeneralInsurabilityLogic {

    private MapperFacade mapper;

    private static List<String> expectedProps = new ArrayList<String>();
    private static Configuration config = ConfigFactory.getConfigValidator(expectedProps);

    @Autowired
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

    @Override
    public InsurabilityInfo getGeneralInsurabity(String token, String patientNiss, String insurance, String
            registrationNumber, Date date, boolean hospitalized) throws TokenNotAvailableException, ConnectorException {

        assert patientNiss != null || insurance != null && registrationNumber != null;
        if (token == null) {
            throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
        }

        RequestParameters requestParameters = new RequestParameters();

        requestParameters.setInss(patientNiss);
        requestParameters.setMutuality(insurance);
        requestParameters.setRegNrWithMut(registrationNumber);

        // Date
        DateTime dateTime = date == null ? new DateTime() : new DateTime(date.getTime());

        requestParameters.setPeriodStart(dateTime);
        requestParameters.setPeriodEnd(dateTime);

        // InsurabilityReference
        requestParameters.setInsurabilityReference("" + System.currentTimeMillis());
        requestParameters.setInsurabilityContactType(hospitalized?InsurabilityContactTypeType.HOSPITALIZED_ELSEWHERE:InsurabilityContactTypeType.AMBULATORY_CARE);
        requestParameters.setInsurabilityRequestType(InsurabilityRequestTypeType.INFORMATION);

        GetInsurabilityAsXmlOrFlatRequestType request = null;
        try {
            request = RequestObjectBuilderFactory.getRequestObjectBuilder().createGetInsurabilityRequest(requestParameters, false/*config.getProperty("endpoint.genins").contains("-acpt")*/);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        }
        be.ehealth.businessconnector.genins.session.GenInsService service = GenInsSessionServiceFactory.getGenInsService();
        try {
            GetInsurabilityResponse response = service.getInsurability(request);
            InsurabilityInfo insurabilityInfo = new InsurabilityInfo();
            if (response.getResponse().getCareReceiverDetail() != null) {
                mapper.map(response.getResponse().getCareReceiverDetail(), insurabilityInfo);

                insurabilityInfo.setDateOfBirth(Instant.ofEpochMilli(response.getResponse().getCareReceiverDetail().getBirthday().toInstant().getMillis()));
            }
            if (response.getResponse().getInsurabilityResponseDetail() != null) {
                if (response.getResponse().getInsurabilityResponseDetail().getHospitalized() != null) {
                    insurabilityInfo.setHospitalizedInfo(mapper.map(response.getResponse().getInsurabilityResponseDetail().getHospitalized(), HospitalizedInfo.class));
                }
                if (response.getResponse().getInsurabilityResponseDetail().getMedicalHouse() != null) {
                    insurabilityInfo.setMedicalHouseInfo(mapper.map(response.getResponse().getInsurabilityResponseDetail().getMedicalHouse(), MedicalHouseInfo.class));
                    //Workaround for https://jira.smals.be/browse/EHCONEXT-38
                    insurabilityInfo.getMedicalHouseInfo().setMedical(response.getResponse().getInsurabilityResponseDetail().getMedicalHouse().isMedical());
                }
                if (response.getResponse().getInsurabilityResponseDetail().getInsurabilityList() != null) {
                    insurabilityInfo.setInsurabilities((List<InsurabilityItem>) CollectionUtils.collect(response.getResponse().getInsurabilityResponseDetail().getInsurabilityList().getInsurabilityItems(), new Transformer<InsurabilityItemType, InsurabilityItem>() {
                        @Override
                        public InsurabilityItem transform(InsurabilityItemType insurabilityItemType) {
                            InsurabilityItem insurabilityItem = mapper.map(insurabilityItemType, InsurabilityItem.class);

                            insurabilityItem.setCt1(insurabilityItemType.getCT1());
                            insurabilityItem.setCt2(insurabilityItemType.getCT2());

                            return insurabilityItem;
                        }
                    }));
                } else {
                    insurabilityInfo.setInsurabilities(new ArrayList<>());
                }
                if (response.getResponse().getInsurabilityResponseDetail().getGeneralSituation() != null && response.getResponse().getInsurabilityResponseDetail().getGeneralSituation().getEvent() != null) {
                    insurabilityInfo.setGeneralSituation(response.getResponse().getInsurabilityResponseDetail().getGeneralSituation().getEvent().value());
                }
                insurabilityInfo.setPaymentByIo(response.getResponse().getInsurabilityResponseDetail().getPayment().isPaymentByIo());
            }
            if (response.getResponse().getMessageFault() != null) {
                insurabilityInfo.setFaultCode(response.getResponse().getMessageFault().getFaultCode().toString());
                insurabilityInfo.setFaultSource(response.getResponse().getMessageFault().getFaultSource());
                StringBuilder message = new StringBuilder();
                for (DetailType d : response.getResponse().getMessageFault().getDetails().getDetails()) {
                    if (d.getMessage() != null) {
                        if (message.length() > 0) {
                            message.append('\n');
                        }
                        message.append(d.getMessage().getValue());
                    }
                }
                insurabilityInfo.setFaultMessage(message.toString());
            }
            return insurabilityInfo;
        } catch (javax.xml.ws.soap.SOAPFaultException e) {
            InsurabilityInfo insurabilityInfo = new InsurabilityInfo();
            insurabilityInfo.setFaultCode(e.getFault().getFaultCode());
            insurabilityInfo.setFaultSource(e.getMessage());
            insurabilityInfo.setFaultMessage(e.getFault().getFaultString());

            return insurabilityInfo;
        }
    }
}
