package org.taktik.icure.entities.embed;

public class Suspension {
    private Long beginMoment;
    private Long endMoment;
    private String suspensionReason;

    public Long getBeginMoment() { return beginMoment; }

    public void setBeginMoment(Long beginMoment) { this.beginMoment = beginMoment; }

    public Long getEndMoment() { return endMoment; }

    public void setEndMoment(Long endMoment) { this.endMoment = endMoment; }

    public String getSuspensionReason() { return suspensionReason; }

    public void setSuspensionReason(String suspensionReason) { this.suspensionReason = suspensionReason; }



    @Override
    public String toString() {
        return "Suspension{" +
                "beginMoment=" + beginMoment +
                ", endMoment=" + endMoment +
                ", suspensionReason='" + suspensionReason + '\'' +
                '}';
    }
}
