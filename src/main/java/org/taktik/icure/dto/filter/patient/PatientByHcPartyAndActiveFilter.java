package org.taktik.icure.dto.filter.patient;

import org.taktik.icure.dto.filter.Filter;
import org.taktik.icure.entities.Patient;

public interface PatientByHcPartyAndActiveFilter extends Filter<String, Patient> {
    boolean getActive();
    String getHealthcarePartyId();
}
