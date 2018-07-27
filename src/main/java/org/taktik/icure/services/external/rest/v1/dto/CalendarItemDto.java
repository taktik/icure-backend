package org.taktik.icure.services.external.rest.v1.dto;

public class CalendarItemDto extends IcureDto {

    protected String title;

    protected CalendarItemTypeDto type;

    protected Long startTime;

    protected Long endTime;

    protected String patient;

    protected String note;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CalendarItemTypeDto getType() {
        return type;
    }

    public void setType(CalendarItemTypeDto type) {
        this.type = type;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
