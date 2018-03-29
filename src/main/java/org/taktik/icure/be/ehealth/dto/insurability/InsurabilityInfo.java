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

package org.taktik.icure.be.ehealth.dto.insurability;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 28/05/13
 * Time: 09:07
 * To change this template use File | Settings | File Templates.
 */
public class InsurabilityInfo implements Serializable {
    protected String inss;
    protected String firstName;
    protected String lastName;
    protected Instant dateOfBirth;
    protected Instant deceased;
    protected String sex;

    HospitalizedInfo hospitalizedInfo;
    MedicalHouseInfo medicalHouseInfo;
    List<InsurabilityItem> insurabilities;

    private String faultMessage;
    private String faultSource;
    private String faultCode;
    private String generalSituation;
    private boolean paymentByIo;

    public String getInss() {
        return inss;
    }

    public void setInss(String inss) {
        this.inss = inss;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Instant getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Instant dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Instant getDeceased() {
        return deceased;
    }

    public void setDeceased(Instant deceased) {
        this.deceased = deceased;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public HospitalizedInfo getHospitalizedInfo() {
        return hospitalizedInfo;
    }

    public void setHospitalizedInfo(HospitalizedInfo hospitalizedInfo) {
        this.hospitalizedInfo = hospitalizedInfo;
    }

    public MedicalHouseInfo getMedicalHouseInfo() {
        return medicalHouseInfo;
    }

    public void setMedicalHouseInfo(MedicalHouseInfo medicalHouseInfo) {
        this.medicalHouseInfo = medicalHouseInfo;
    }

    public List<InsurabilityItem> getInsurabilities() {
        return insurabilities;
    }

    public void setInsurabilities(List<InsurabilityItem> insurabilities) {
        this.insurabilities = insurabilities;
    }

    public void setFaultMessage(String faultMessage) {
        this.faultMessage = faultMessage;
    }

    public void setFaultSource(String faultSource) {
        this.faultSource = faultSource;
    }

    public void setFaultCode(String faultCode) {
        this.faultCode = faultCode;
    }

    public String getFaultMessage() {
        return faultMessage;
    }

    public String getFaultSource() {
        return faultSource;
    }

    public String getFaultCode() {
        return faultCode;
    }

    public void setGeneralSituation(String value) {
        generalSituation = value;
    }

    public String getGeneralSituation() {
        return generalSituation;
    }

    public void setPaymentByIo(boolean paymentByIo) {
        this.paymentByIo = paymentByIo;
    }

    public boolean isPaymentByIo() {
        return paymentByIo;
    }
}
