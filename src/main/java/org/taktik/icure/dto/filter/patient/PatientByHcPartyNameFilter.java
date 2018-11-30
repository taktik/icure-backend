package org.taktik.icure.dto.filter.patient;

import org.taktik.icure.dto.filter.Filter;
import org.taktik.icure.entities.Patient;

public interface PatientByHcPartyNameFilter extends Filter<String, Patient> {
    String getName();
    String getHealthcarePartyId();
}
