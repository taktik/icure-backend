package org.taktik.icure.services.external.rest.v1.dto.embed;

import org.taktik.icure.entities.embed.MembershipType;

import java.io.Serializable;

public class CareTeamMembershipDto implements Serializable {
    private Long startDate;
    private Long endDate;
    private String careTeamMemberId;
    private MembershipType membershipType;

    public Long getStartDate() { return startDate; }

    public void setStartDate(Long startDate) { this.startDate = startDate; }

    public Long getEndDate() { return endDate; }

    public void setEndDate(Long endDate) { this.endDate = endDate; }

    public String getCareTeamMemberId() { return careTeamMemberId;}

    public void setCareTeamMemberId(String careTeamMemberId) { this.careTeamMemberId = careTeamMemberId; }

    public MembershipType getMembershipType() { return membershipType;}

    public void setMembershipType(MembershipType membershipType) { this.membershipType = membershipType; }
}
