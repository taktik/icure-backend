package org.taktik.icure.services.external.rest.v1.dto.embed;

import org.taktik.icure.entities.base.CodeStub;

import java.util.Objects;

public class CareMemberDto {
    private String healthcarePartyId;
    private CodeStub quality;

    public String getHealthcarePartyId() {
        return healthcarePartyId;
    }

    public void setHealthcarePartyId(String healthcarePartyId) {
        this.healthcarePartyId = healthcarePartyId;
    }

    public CodeStub getQuality() {
        return quality;
    }

    public void setQuality(CodeStub quality) {
        this.quality = quality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CareMemberDto)) return false;
        CareMemberDto that = (CareMemberDto) o;
        return Objects.equals(healthcarePartyId, that.healthcarePartyId) &&
                Objects.equals(quality, that.quality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(healthcarePartyId, quality);
    }
}
