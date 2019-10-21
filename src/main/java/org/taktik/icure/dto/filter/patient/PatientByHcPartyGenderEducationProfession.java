package org.taktik.icure.dto.filter.patient;

import org.taktik.icure.dto.filter.Filter;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.embed.Gender;

public interface PatientByHcPartyGenderEducationProfession extends Filter<String, Patient> {
    Gender getGender();
    String getEducation();
    String getProfession();
    String getHealthcarePartyId();
}
