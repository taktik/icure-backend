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

package org.taktik.icure.entities.embed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.Identifiable;

@SuppressWarnings("UnusedDeclaration")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoicingCode implements Identifiable<String>, Comparable<InvoicingCode> {
	public static final long STATUS_PAID = 1;
	public static final long STATUS_PRINTED = 2;
	public static final long STATUS_PAIDPRINTED = 3;

	public static final long STATUS_PENDING = 4;
	public static final long STATUS_CANCELED = 8;
	public static final long STATUS_ACCEPTED = 16;
	public static final long STATUS_RESENT = 32;

	protected Long dateCode;

	protected String id;
	protected String logicalId; //Stays the same when a code is resent to the IO

	protected String label;

	protected String userId;
    protected String contactId;
    protected String serviceId;
    protected String tarificationId;

    //For obsolete codes or codes not linked to a tarification
    protected String code;

    protected PaymentType paymentType;

    protected Double paid;

    protected Double totalAmount; //=reimbursement+doctorSupplement+intervention
    protected Double reimbursement;
    protected Double patientIntervention;
    protected Double doctorSupplement;
	protected Double conventionAmount; //Should be reimbursement+intervention
	protected Double vat;

    //Etarif
    protected String error;
	//TODO... Might want to encrypt this as it could be used to identify the patient
    protected String contract;
	protected Long contractDate;
    protected Integer units;
    protected Integer side;
    protected Integer timeOfDay;
	protected Integer eidReadingHour;
	protected String eidReadingValue;
    protected Integer override3rdPayerCode;
    protected String override3rdPayerReason;

	protected Integer prescriberNorm;
	protected Integer percentNorm;
    protected String prescriberNihii;
    protected String relatedCode;
	protected Long prescriptionDate; // yyyyMMdd
    protected Integer derogationMaxNumber;


	protected String prescriberSsin;
	protected String prescriberLastName;
	protected String prescriberFirstName;
	protected String prescriberCdHcParty;

	protected String locationNihii;
	protected String locationCdHcParty;


	protected Boolean canceled;
	protected Boolean accepted;
	protected Boolean pending;
	protected Boolean resent;
	protected Boolean archived;

    protected Integer insuranceJustification;

	protected Integer cancelPatientInterventionReason;

	protected Long status;

	public InvoicingCode() {}

	public InvoicingCode(InvoicingCode other) {
		this.dateCode = this.dateCode == null ? other.dateCode : this.dateCode;
		this.id = this.id == null ? other.id : this.id;
		this.logicalId = this.logicalId == null ? other.logicalId : this.logicalId;
		this.label = this.label == null ? other.label : this.label;
		this.userId = this.userId == null ? other.userId : this.userId;
		this.contactId = this.contactId == null ? other.contactId : this.contactId;
		this.serviceId = this.serviceId == null ? other.serviceId : this.serviceId;
		this.tarificationId = this.tarificationId == null ? other.tarificationId : this.tarificationId;
		this.code = this.code == null ? other.code : this.code;
		this.paymentType = this.paymentType == null ? other.paymentType : this.paymentType;
		this.paid = this.paid == null ? other.paid : this.paid;
		this.totalAmount = this.totalAmount == null ? other.totalAmount : this.totalAmount;
		this.reimbursement = this.reimbursement == null ? other.reimbursement : this.reimbursement;
		this.patientIntervention = this.patientIntervention == null ? other.patientIntervention : this.patientIntervention;
		this.doctorSupplement = this.doctorSupplement == null ? other.doctorSupplement : this.doctorSupplement;
		this.vat = this.vat == null ? other.vat : this.vat;
		this.error = this.error == null ? other.error : this.error;
		this.contract = this.contract == null ? other.contract : this.contract;
		this.units = this.units == null ? other.units : this.units;
		this.side = this.side == null ? other.side : this.side;
		this.timeOfDay = this.timeOfDay == null ? other.timeOfDay : this.timeOfDay;
		this.eidReadingHour = this.eidReadingHour == null ? other.eidReadingHour : this.eidReadingHour;
		this.eidReadingValue = this.eidReadingValue == null ? other.eidReadingValue : this.eidReadingValue;
		this.override3rdPayerCode = this.override3rdPayerCode == null ? other.override3rdPayerCode : this.override3rdPayerCode;
		this.override3rdPayerReason = this.override3rdPayerReason == null ? other.override3rdPayerReason : this.override3rdPayerReason;
		this.prescriberNorm = this.prescriberNorm == null ? other.prescriberNorm : this.prescriberNorm;
		this.percentNorm = this.percentNorm == null ? other.percentNorm : this.percentNorm;
		this.derogationMaxNumber = this.derogationMaxNumber == null ? other.derogationMaxNumber : this.derogationMaxNumber;
		this.prescriberNihii = this.prescriberNihii == null ? other.prescriberNihii : this.prescriberNihii;
		this.relatedCode = this.relatedCode == null ? other.relatedCode : this.relatedCode;
		this.canceled = this.canceled == null ? other.canceled : this.canceled;
		this.accepted = this.accepted == null ? other.accepted : this.accepted;
		this.pending = this.pending == null ? other.pending : this.pending;
		this.resent = this.resent == null ? other.resent : this.resent;
		this.archived = this.archived == null ? other.archived : this.archived;
		this.insuranceJustification = this.insuranceJustification == null ? other.insuranceJustification : this.insuranceJustification;
		this.cancelPatientInterventionReason = this.cancelPatientInterventionReason == null ? other.cancelPatientInterventionReason : this.cancelPatientInterventionReason;
		this.status = this.status == null ? other.status : this.status;
		this.prescriberSsin = this.prescriberSsin == null ? other.prescriberSsin : this.prescriberSsin;
		this.prescriberLastName = this.prescriberLastName == null ? other.prescriberLastName : this.prescriberLastName;
		this.prescriberFirstName = this.prescriberFirstName == null ? other.prescriberFirstName : this.prescriberFirstName;
		this.prescriberCdHcParty = this.prescriberCdHcParty == null ? other.prescriberCdHcParty : this.prescriberCdHcParty;
		this.locationNihii = this.locationNihii == null ? other.locationNihii : this.locationNihii;
		this.locationCdHcParty = this.locationCdHcParty == null ? other.locationCdHcParty : this.locationCdHcParty;
	}

	public Long getDateCode() {
        return dateCode;
    }

    public void setDateCode(Long dateCode) {
        this.dateCode = dateCode;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getTarificationId() {
        return tarificationId;
    }

    public void setTarificationId(String tarificationId) {
        this.tarificationId = tarificationId;
    }

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getPatientIntervention() {
        return patientIntervention;
    }

    public void setPatientIntervention(Double patientIntervention) {
        this.patientIntervention = patientIntervention;
    }

    public Double getDoctorSupplement() {
        return doctorSupplement;
    }

    public void setDoctorSupplement(Double doctorSupplement) {
        this.doctorSupplement = doctorSupplement;
    }

	public Double getConventionAmount() {
		return conventionAmount;
	}

	public void setConventionAmount(Double conventionAmount) {
		this.conventionAmount = conventionAmount;
	}

	public Double getReimbursement() {
        return reimbursement;
    }

	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}

	public void setReimbursement(Double reimbursement) {
        this.reimbursement = reimbursement;
    }

	public int compareTo(InvoicingCode other) {
		return other == null ? -1 : this.dateCode.compareTo(other.dateCode);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public Integer getSide() {
        return side;
    }

    public void setSide(Integer side) {
        this.side = side;
    }

    public Integer getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(Integer timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

	public Integer getOverride3rdPayerCode() {
		return override3rdPayerCode;
	}

	public void setOverride3rdPayerCode(Integer override3rdPayerCode) {
		this.override3rdPayerCode = override3rdPayerCode;
	}

	public String getOverride3rdPayerReason() {
        return override3rdPayerReason;
    }

    public void setOverride3rdPayerReason(String override3rdPayerReason) {
        this.override3rdPayerReason = override3rdPayerReason;
    }

	public Long getContractDate() {
		return contractDate;
	}

	public void setContractDate(Long contractDate) {
		this.contractDate = contractDate;
	}

	public Integer getPrescriberNorm() {
        return prescriberNorm;
    }

    public void setPrescriberNorm(Integer prescriberNorm) {
        this.prescriberNorm = prescriberNorm;
    }

	public Integer getPercentNorm() {
		return percentNorm;
	}

	public void setPercentNorm(Integer percentNorm) {
		this.percentNorm = percentNorm;
	}

	public Integer getDerogationMaxNumber() {
		return derogationMaxNumber;
	}

	public void setDerogationMaxNumber(Integer derogationMaxNumber) {
		this.derogationMaxNumber = derogationMaxNumber;
	}

	public String getPrescriberNihii() {
        return prescriberNihii;
    }

    public void setPrescriberNihii(String prescriberNihii) {
        this.prescriberNihii = prescriberNihii;
    }

    public String getRelatedCode() {
        return relatedCode;
    }

    public void setRelatedCode(String relatedCode) {
        this.relatedCode = relatedCode;
    }

    public Integer getInsuranceJustification() {
        return insuranceJustification;
    }

    public void setInsuranceJustification(Integer insuranceJustification) {
        this.insuranceJustification = insuranceJustification;
    }

	public Integer getCancelPatientInterventionReason() {
		return cancelPatientInterventionReason;
	}

	public void setCancelPatientInterventionReason(Integer cancelPatientInterventionReason) {
		this.cancelPatientInterventionReason = cancelPatientInterventionReason;
	}

	public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

	public Integer getEidReadingHour() {
		return eidReadingHour;
	}

	public void setEidReadingHour(Integer eidReadingHour) {
		this.eidReadingHour = eidReadingHour;
	}

	public Boolean getCanceled() {
		return canceled;
	}

	public void setCanceled(Boolean canceled) {
		this.canceled = canceled;
	}

	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

	public Boolean getPending() {
		return pending;
	}

	public void setPending(Boolean pending) {
		this.pending = pending;
	}

	public Boolean getResent() {
		return resent;
	}

	public void setResent(Boolean resent) {
		this.resent = resent;
	}

	public Boolean getArchived() {
		return archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEidReadingValue() {
		return eidReadingValue;
	}

	public void setEidReadingValue(String eidReadingValue) {
		this.eidReadingValue = eidReadingValue;
	}

	public String getLogicalId() {
		return logicalId;
	}

	public void setLogicalId(String logicalId) {
		this.logicalId = logicalId;
	}

	public String getPrescriberSsin() {
		return prescriberSsin;
	}

	public void setPrescriberSsin(String prescriberSsin) {
		this.prescriberSsin = prescriberSsin;
	}

	public String getPrescriberLastName() {
		return prescriberLastName;
	}

	public void setPrescriberLastName(String prescriberLastName) {
		this.prescriberLastName = prescriberLastName;
	}

	public String getPrescriberFirstName() {
		return prescriberFirstName;
	}

	public void setPrescriberFirstName(String prescriberFirstName) {
		this.prescriberFirstName = prescriberFirstName;
	}

	public String getPrescriberCdHcParty() {
		return prescriberCdHcParty;
	}

	public void setPrescriberCdHcParty(String prescriberCdHcParty) {
		this.prescriberCdHcParty = prescriberCdHcParty;
	}

	public String getLocationNihii() {
		return locationNihii;
	}

	public void setLocationNihii(String locationNihii) {
		this.locationNihii = locationNihii;
	}

	public String getLocationCdHcParty() {
		return locationCdHcParty;
	}

	public void setLocationCdHcParty(String locationCdHcParty) {
		this.locationCdHcParty = locationCdHcParty;
	}

	public Long getPrescriptionDate() {	return prescriptionDate;	}

	public void setPrescriptionDate(Long prescriptionDate) {	this.prescriptionDate = prescriptionDate;	}

	public InvoicingCode solveConflictWith(InvoicingCode other) {
		this.dateCode = this.dateCode == null ? other.dateCode : this.dateCode;
		this.logicalId = this.logicalId == null ? other.logicalId : this.logicalId;
		this.label = this.label == null ? other.label : this.label;
		this.userId = this.userId == null ? other.userId : this.userId;
		this.contactId = this.contactId == null ? other.contactId : this.contactId;
		this.serviceId = this.serviceId == null ? other.serviceId : this.serviceId;
		this.tarificationId = this.tarificationId == null ? other.tarificationId : this.tarificationId;
		this.code = this.code == null ? other.code : this.code;
		this.paymentType = this.paymentType == null ? other.paymentType : this.paymentType;
		this.paid = this.paid == null ? other.paid : this.paid;
		this.totalAmount = this.totalAmount == null ? other.totalAmount : this.totalAmount;
		this.reimbursement = this.reimbursement == null ? other.reimbursement : this.reimbursement;
		this.patientIntervention = this.patientIntervention == null ? other.patientIntervention : this.patientIntervention;
		this.doctorSupplement = this.doctorSupplement == null ? other.doctorSupplement : this.doctorSupplement;
		this.vat = this.vat == null ? other.vat : this.vat;
		this.error = this.error == null ? other.error : this.error;
		this.contract = this.contract == null ? other.contract : this.contract;
		this.contractDate = this.contractDate == null ? other.contractDate : this.contractDate;
		this.units = this.units == null ? other.units : this.units;
		this.side = this.side == null ? other.side : this.side;
		this.timeOfDay = this.timeOfDay == null ? other.timeOfDay : this.timeOfDay;
		this.eidReadingHour = this.eidReadingHour == null ? other.eidReadingHour : this.eidReadingHour;
		this.eidReadingValue = this.eidReadingValue == null ? other.eidReadingValue : this.eidReadingValue;
		this.override3rdPayerCode = this.override3rdPayerCode == null ? other.override3rdPayerCode : this.override3rdPayerCode;
		this.override3rdPayerReason = this.override3rdPayerReason == null ? other.override3rdPayerReason : this.override3rdPayerReason;
		this.prescriberNorm = this.prescriberNorm == null ? other.prescriberNorm : this.prescriberNorm;
		this.derogationMaxNumber = this.derogationMaxNumber == null ? other.derogationMaxNumber : this.derogationMaxNumber;
		this.prescriberNihii = this.prescriberNihii == null ? other.prescriberNihii : this.prescriberNihii;
		this.relatedCode = this.relatedCode == null ? other.relatedCode : this.relatedCode;
		this.canceled = this.canceled == null ? other.canceled : this.canceled;
		this.accepted = this.accepted == null ? other.accepted : this.accepted;
		this.pending = this.pending == null ? other.pending : this.pending;
		this.resent = this.resent == null ? other.resent : this.resent;
		this.insuranceJustification = this.insuranceJustification == null ? other.insuranceJustification : this.insuranceJustification;
		this.cancelPatientInterventionReason = this.cancelPatientInterventionReason == null ? other.cancelPatientInterventionReason : this.cancelPatientInterventionReason;
		this.status = this.status == null ? other.status : this.status;
		this.prescriberSsin = this.prescriberSsin == null ? other.prescriberSsin : this.prescriberSsin;
		this.prescriberLastName = this.prescriberLastName == null ? other.prescriberLastName : this.prescriberLastName;
		this.prescriberFirstName = this.prescriberFirstName == null ? other.prescriberFirstName : this.prescriberFirstName;
		this.prescriberCdHcParty = this.prescriberCdHcParty == null ? other.prescriberCdHcParty : this.prescriberCdHcParty;
		this.locationNihii = this.locationNihii == null ? other.locationNihii : this.locationNihii;
		this.locationCdHcParty = this.locationCdHcParty == null ? other.locationCdHcParty : this.locationCdHcParty;
		return this;
	}
}
