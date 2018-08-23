package org.taktik.icure.services.external.rest.v1.dto;

import java.util.List;

public class ImportResultDto {
    private PatientDto patient;
    private List<HealthElementDto> hes;
    private List<ContactDto> ctcs;
    private List<String> warnings;
    private List<String> errors;

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
}
