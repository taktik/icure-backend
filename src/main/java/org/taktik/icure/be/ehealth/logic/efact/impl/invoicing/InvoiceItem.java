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

package org.taktik.icure.be.ehealth.logic.efact.impl.invoicing;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 19/08/15
 * Time: 11:12
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unused")
public class InvoiceItem {
    private Long dateCode;
    private long codeNomenclature;
	private Long relatedCode;

    private EIDItem eidItem;
    private String insuranceRef;
    private Long insuranceRefDate;

    private int units;

    private long reimbursedAmount;
    private long patientFee;
    private long doctorSupplement;

    private InvoicingSideCode sideCode;
    private InvoicingTimeOfDay timeOfDay;

    private Integer override3rdPayerCode;
    private String gnotionNihii;

    private InvoicingPrescriberCode prescriberNorm;
    private String prescriberNihii;

    private Integer personalInterventionCoveredByThirdPartyCode;

    private String doctorIdentificationNumber;
    private String invoiceRef;
	private InvoicingPercentNorm percentNorm;

	public InvoiceItem() {
    }

    public Long getDateCode() {
        return dateCode;
    }

    public void setDateCode(Long dateCode) {
        this.dateCode = dateCode;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public long getCodeNomenclature() {
        return codeNomenclature;
    }

    public void setCodeNomenclature(long codeNomenclature) {
        this.codeNomenclature = codeNomenclature;
    }

	public Long getRelatedCode() {
		return relatedCode;
	}

	public void setRelatedCode(Long relatedCode) {
		this.relatedCode = relatedCode;
	}

	public long getReimbursedAmount() {
        return reimbursedAmount;
    }

    public void setReimbursedAmount(long reimbursedAmount) {
        this.reimbursedAmount = reimbursedAmount;
    }

    public long getPatientFee() {
        return patientFee;
    }

    public void setPatientFee(long patientFee) {
        this.patientFee = patientFee;
    }

    public long getDoctorSupplement() {
        return doctorSupplement;
    }

    public void setDoctorSupplement(long doctorSupplement) {
        this.doctorSupplement = doctorSupplement;
    }

    public EIDItem getEidItem() {
        return eidItem;
    }

    public void setEidItem(EIDItem eidItem) {
        this.eidItem = eidItem;
    }

    public Integer getOverride3rdPayerCode() {
        return override3rdPayerCode;
    }

    public void setOverride3rdPayerCode(Integer override3rdPayerCode) {
        this.override3rdPayerCode = override3rdPayerCode;
    }

    public Integer getPersonalInterventionCoveredByThirdPartyCode() {
        return personalInterventionCoveredByThirdPartyCode;
    }

    public void setPersonalInterventionCoveredByThirdPartyCode(Integer personalInterventionCoveredByThirdPartyCode) {
        this.personalInterventionCoveredByThirdPartyCode = personalInterventionCoveredByThirdPartyCode;
    }

    public String getInsuranceRef() {
        return insuranceRef;
    }

    public void setInsuranceRef(String insuranceRef) {
        this.insuranceRef = insuranceRef;
    }

    public Long getInsuranceRefDate() {
        return insuranceRefDate;
    }

    public void setInsuranceRefDate(Long insuranceRefDate) {
        this.insuranceRefDate = insuranceRefDate;
    }

    public String getGnotionNihii() {
        return gnotionNihii;
    }

    public void setGnotionNihii(String gnotionNihii) {
        this.gnotionNihii = gnotionNihii;
    }

    public InvoicingPrescriberCode getPrescriberNorm() {
        return prescriberNorm;
    }

    public void setPrescriberNorm(InvoicingPrescriberCode prescriberNorm) {
        this.prescriberNorm = prescriberNorm;
    }

    public InvoicingSideCode getSideCode() {
        return sideCode;
    }

    public void setSideCode(InvoicingSideCode sideCode) {
        this.sideCode = sideCode;
    }

    public InvoicingTimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(InvoicingTimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getPrescriberNihii() {
        return prescriberNihii;
    }

    public void setPrescriberNihii(String prescriberNihii) {
        this.prescriberNihii = prescriberNihii;
    }

    public String getDoctorIdentificationNumber() {
        return doctorIdentificationNumber;
    }

    public void setDoctorIdentificationNumber(String doctorIdentificationNumber) {
        this.doctorIdentificationNumber = doctorIdentificationNumber;
    }

    public String getInvoiceRef() {
        return invoiceRef;
    }

    public void setInvoiceRef(String invoiceRef) {
        this.invoiceRef = invoiceRef;
    }

	public void setPercentNorm(InvoicingPercentNorm percentNorm) {
		this.percentNorm = percentNorm;
	}

	public InvoicingPercentNorm getPercentNorm() {
		return percentNorm;
	}
}
