package org.taktik.icure.entities.embed;

import org.taktik.icure.entities.base.CodeStub;

import java.io.Serializable;
import java.util.Objects;

public class EmploymentInfo implements Serializable {
    private Long startDate;
    private Long endDate;
    private CodeStub professionType;
    private Employer employer;

    public Long getStartDate() { return startDate; }

    public void setStartDate(Long startDate) { this.startDate = startDate; }

    public Long getEndDate() { return endDate; }

    public void setEndDate(Long endDate) { this.endDate = endDate; }

    public CodeStub getProfessionType() { return professionType; }

    public void setProfessionType(CodeStub professionType) { this.professionType = professionType; }

    public Employer getEmployer() { return employer; }

    public void setEmployer(Employer employer) { this.employer = employer; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmploymentInfo that = (EmploymentInfo) o;
        return Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(professionType, that.professionType) &&
                Objects.equals(employer, that.employer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, professionType, employer);
    }
}
