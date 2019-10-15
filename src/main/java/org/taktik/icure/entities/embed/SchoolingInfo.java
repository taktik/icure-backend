package org.taktik.icure.entities.embed;

import org.taktik.icure.entities.base.CodeStub;

import java.util.Objects;

public class SchoolingInfo{
    private Long startDate;
    private Long endDate;
    private String school;
    private CodeStub typeOfEducation;

    public Long getStartDate() { return startDate; }

    public void setStartDate(Long startDate) { this.startDate = startDate; }

    public Long getEndDate() { return endDate; }

    public void setEndDate(Long endDate) { this.endDate = endDate; }

    public String getSchool() { return school; }

    public void setSchool(String school) { this.school = school; }

    public CodeStub getTypeOfEducation() { return typeOfEducation; }

    public void setTypeOfEducation(CodeStub typeOfEducation) { this.typeOfEducation = typeOfEducation; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolingInfo that = (SchoolingInfo) o;
        return Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(school, that.school) &&
                Objects.equals(typeOfEducation, that.typeOfEducation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, school, typeOfEducation);
    }
}
