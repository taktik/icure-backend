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

package org.taktik.icure.services.external.rest.v1.dto.filter.patient;

import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.embed.Gender;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

import java.util.Objects;

@JsonPolymorphismRoot(Filter.class)
public class PatientByHcPartyGenderEducationProfession extends Filter<Patient> implements org.taktik.icure.dto.filter.patient.PatientByHcPartyGenderEducationProfession {
    private String healthcarePartyId;
    private Gender gender;
    private String education;
    private String profession;

    public PatientByHcPartyGenderEducationProfession() {
    }

    public PatientByHcPartyGenderEducationProfession(Gender gender, String education, String profession, String healthcarePartyId) {
        this.healthcarePartyId = healthcarePartyId;
        this.gender = gender;
        this.education = education;
        this.profession = profession;
    }

    @Override
	public boolean matches(Patient item) {
        return (healthcarePartyId == null || item.getDelegations().keySet().contains(healthcarePartyId))
                && (gender == null || item.getGender() != null && item.getGender() == gender)
                && (education == null || item.getEducation() != null && item.getEducation().equals(education))
                && (profession == null || item.getProfession() != null && item.getProfession().equals(profession));
    }

    @Override
    public Gender getGender() {
        return this.gender;
    }

    @Override
    public String getEducation() {
        return this.education;
    }

    @Override
    public String getProfession() {
        return this.profession;
    }

    @Override
    public String getHealthcarePartyId() {
        return this.healthcarePartyId;
    }

    public void setHealthcarePartyId(String healthcarePartyId) {
        this.healthcarePartyId = healthcarePartyId;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatientByHcPartyGenderEducationProfession)) return false;
        PatientByHcPartyGenderEducationProfession that = (PatientByHcPartyGenderEducationProfession) o;
        return Objects.equals(healthcarePartyId, that.healthcarePartyId) &&
                gender == that.gender &&
                Objects.equals(education, that.education) &&
                Objects.equals(profession, that.profession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(healthcarePartyId, gender, education, profession);
    }
}
