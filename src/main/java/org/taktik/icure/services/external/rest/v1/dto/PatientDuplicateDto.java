package org.taktik.icure.services.external.rest.v1.dto;

import java.util.HashSet;
import java.util.Set;

public class PatientDuplicateDto {
    protected String id;
    protected String firstName;
    protected String lastName;
    protected Boolean active = true;
    protected String ssin;
    protected Integer dateOfBirth;
    protected Set<String> nonDuplicateIds = new HashSet<>();
    protected String mergeToPatientId;
    protected Set<String> mergedIds = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getSsin() {
        return ssin;
    }

    public void setSsin(String ssin) {
        this.ssin = ssin;
    }

    public Integer getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Integer dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Set<String> getNonDuplicateIds() {
        return nonDuplicateIds;
    }

    public void setNonDuplicateIds(Set<String> nonDuplicateIds) {
        this.nonDuplicateIds = nonDuplicateIds;
    }

    public String getMergeToPatientId() {
        return mergeToPatientId;
    }

    public void setMergeToPatientId(String mergeToPatientId) {
        this.mergeToPatientId = mergeToPatientId;
    }

    public Set<String> getMergedIds() {
        return mergedIds;
    }

    public void setMergedIds(Set<String> mergedIds) {
        this.mergedIds = mergedIds;
    }
}
