package org.taktik.icure.services.external.rest.v1.dto.embed;

import org.taktik.icure.entities.base.CodeStub;

import java.io.Serializable;

public class SchoolingInfoDto implements Serializable {
    private Long startDate;
    private Long endDate;
    private String school;
    private CodeStub typeOfEducation;

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

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public CodeStub getTypeOfEducation() {
        return typeOfEducation;
    }

    public void setTypeOfEducation(CodeStub typeOfEducation) {
        this.typeOfEducation = typeOfEducation;
    }
}
