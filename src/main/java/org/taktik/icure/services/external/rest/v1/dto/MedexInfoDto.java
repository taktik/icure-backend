package org.taktik.icure.services.external.rest.v1.dto;

public class MedexInfoDto {
    protected HealthcarePartyDto author;
    protected PatientDto patient;
    protected String patientLanguage;
    protected String incapacityType; // incapacity or incapacityextension
    /*
        Possible values:
        illness
        hospitalisation
        sickness
        pregnancy
        workaccident
        occupationaldisease
     */
    protected String incapacityReason;
    protected Boolean outOfHomeAllowed;
    /*
    "Optional field
    But mandatory when incapacityreason = workaccident; this field must contain the accident date.
    when incapacityreason = occupationaldisease this field must contain the request date for a dossier for occupatialdesease.
    This date must be < or =  beginmoment of the incapacity period."
     */
    protected Long certificateDate;
    protected Long contentDate;
    protected Long beginDate;
    protected Long endDate;
    protected String diagnosisICPC;
    protected String diagnosisICD;
    protected String diagnosisDescr;

    public HealthcarePartyDto getAuthor() {
        return author;
    }

    public void setAuthor(HealthcarePartyDto author) {
        this.author = author;
    }

    public PatientDto getPatient() {
        return patient;
    }

    public void setPatient(PatientDto patient) {
        this.patient = patient;
    }

    public String getPatientLanguage() {
        return patientLanguage;
    }

    public void setPatientLanguage(String patientLanguage) {
        this.patientLanguage = patientLanguage;
    }

    public String getIncapacityType() {
        return incapacityType;
    }

    public void setIncapacityType(String incapacityType) {
        this.incapacityType = incapacityType;
    }

    public String getIncapacityReason() {
        return incapacityReason;
    }

    public void setIncapacityReason(String incapacityReason) {
        this.incapacityReason = incapacityReason;
    }

    public Boolean getOutOfHomeAllowed() {
        return outOfHomeAllowed;
    }

    public void setOutOfHomeAllowed(Boolean outOfHomeAllowed) {
        this.outOfHomeAllowed = outOfHomeAllowed;
    }

    public Long getContentDate() {
        return contentDate;
    }

    public void setContentDate(Long contentDate) {
        this.contentDate = contentDate;
    }

    public Long getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Long beginDate) {
        this.beginDate = beginDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Long getCertificateDate() {
        return certificateDate;
    }

    public void setCertificateDate(Long certificateDate) {
        this.certificateDate = certificateDate;
    }

    public String getDiagnosisICPC() {
        return diagnosisICPC;
    }

    public void setDiagnosisICPC(String diagnosisICPC) {
        this.diagnosisICPC = diagnosisICPC;
    }

    public String getDiagnosisICD() {
        return diagnosisICD;
    }

    public void setDiagnosisICD(String diagnosisICD) {
        this.diagnosisICD = diagnosisICD;
    }

    public String getDiagnosisDescr() {
        return diagnosisDescr;
    }

    public void setDiagnosisDescr(String diagnosisDescr) {
        this.diagnosisDescr = diagnosisDescr;
    }
}
