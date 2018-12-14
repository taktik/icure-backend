package org.taktik.icure.services.external.rest.v1.dto.filter.patient;

import com.google.common.base.Objects;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

import java.util.Optional;

import static org.taktik.icure.db.StringUtils.sanitizeString;

@JsonPolymorphismRoot(Filter.class)
public class PatientByHcPartyNameFilter extends Filter<Patient> implements org.taktik.icure.dto.filter.patient.PatientByHcPartyNameFilter {

    private String name;
    private String healthcarePartyId;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getHealthcarePartyId() {
        return healthcarePartyId;
    }

    public void setHealthcarePartyId(String healthcarePartyId) {
        this.healthcarePartyId = healthcarePartyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientByHcPartyNameFilter that = (PatientByHcPartyNameFilter) o;

        return Objects.equal(this.healthcarePartyId, that.healthcarePartyId) &&
            Objects.equal(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(healthcarePartyId, name);
    }

    @Override
    public boolean matches(Patient item) {
        String ss = sanitizeString(name);
        return (healthcarePartyId == null || item.getDelegations().keySet().contains(healthcarePartyId))
            && (sanitizeString(Optional.of(item.getLastName()).orElse("") + Optional.of(item.getFirstName()).orElse("")).contains(ss) ||
            sanitizeString(Optional.of(item.getMaidenName()).orElse("")).contains(ss) ||
            sanitizeString(Optional.of(item.getPartnerName()).orElse("")).contains(ss));
    }
}
