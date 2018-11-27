package org.taktik.icure.services.external.rest.v1.dto;

import java.util.List;

public class ImportResultDto {
    private PatientDto patient;
    private List<HealthElementDto> hes;
    private List<ContactDto> ctcs;
    private List<String> warnings;
    private List<String> errors;
    private List<FormDto> forms;
    private List<HealthcarePartyDto> hcps;
    private List<DocumentDto> documents;

    public PatientDto getPatient() {
        return patient;
    }

    public void setPatient(PatientDto patient) {
        this.patient = patient;
    }

    public List<HealthElementDto> getHes() {
        return hes;
    }

    public void setHes(List<HealthElementDto> hes) {
        this.hes = hes;
    }

    public List<ContactDto> getCtcs() {
        return ctcs;
    }

    public void setCtcs(List<ContactDto> ctcs) {
        this.ctcs = ctcs;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<FormDto> getForms() { return forms; }

    public void setForms(List<FormDto> forms) { this.forms = forms; }

    public List<HealthcarePartyDto> getHcps() { return hcps; }

    public void setHcps(List<HealthcarePartyDto> hcps) { this.hcps = hcps; }

    public List<DocumentDto> getDocuments() { return documents; }

    public void setDocuments(List<DocumentDto> documents) { this.documents = documents; }
}
