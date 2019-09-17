package org.taktik.icure.entities.embed;

import java.util.Objects;

public class CareTeamMembership {
    private Long startDate;
    private Long endDate;
    private String careTeamMemberId;

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public String getCareTeamMemberId() {
        return careTeamMemberId;
    }

    public void setCareTeamMemberId(String careTeamMemberId) {
        this.careTeamMemberId = careTeamMemberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CareTeamMembership)) return false;
        CareTeamMembership that = (CareTeamMembership) o;
        return Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(careTeamMemberId, that.careTeamMemberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, careTeamMemberId);
    }
}
