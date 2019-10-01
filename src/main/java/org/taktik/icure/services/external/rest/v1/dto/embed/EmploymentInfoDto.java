package org.taktik.icure.services.external.rest.v1.dto.embed;

import org.taktik.icure.entities.base.CodeStub;
import org.taktik.icure.entities.embed.Employer;

import java.io.Serializable;

public class EmploymentInfoDto implements Serializable {
    private Long startDate;
    private Long endDate;
    private CodeStub professionType;
    private EmployerDto employer;

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

    public CodeStub getProfessionType() {
        return professionType;
    }

    public void setProfessionType(CodeStub professionType) {
        this.professionType = professionType;
    }

    public EmployerDto getEmployer() {
        return employer;
    }

    public void setEmployer(EmployerDto employer) {
        this.employer = employer;
    }
}
