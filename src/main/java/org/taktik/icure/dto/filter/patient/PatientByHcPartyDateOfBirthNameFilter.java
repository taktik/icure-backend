package org.taktik.icure.dto.filter.patient;

import org.taktik.icure.dto.filter.Filter;
import org.taktik.icure.entities.Patient;

public interface PatientByHcPartyDateOfBirthNameFilter extends Filter<String, Patient> {
    Integer getDateOfBirth();
    String getName();
    String getHealthcarePartyId();
    String getSsin();
}
