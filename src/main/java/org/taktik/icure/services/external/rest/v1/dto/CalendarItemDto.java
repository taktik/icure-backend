package org.taktik.icure.services.external.rest.v1.dto;

import org.taktik.icure.entities.Place;
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto;

import java.util.List;
import java.util.Set;

public class CalendarItemDto extends IcureDto {

    protected String title;

    private String calendarItemTypeId;

    protected String patientId;

    protected Boolean important;

    protected Boolean homeVisit;

    protected String phoneNumber;

    protected String placeId;

    protected AddressDto address;

    protected String addressText;

    protected Long startTime;

    protected Long endTime;

    protected Long duration;

    protected Boolean allDay;

    protected String details;

    protected String agendaId;

    protected Set<CalendarItemTagDto> meetingTags;

    protected FlowItemDto flowItem;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCalendarItemTypeId() {
        return calendarItemTypeId;
    }

    public void setCalendarItemTypeId(String calendarItemTypeId) {
        this.calendarItemTypeId = calendarItemTypeId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Boolean getImportant() {
        return important;
    }

    public void setImportant(Boolean important) {
        this.important = important;
    }

    public Boolean getHomeVisit() {
        return homeVisit;
    }

    public void setHomeVisit(Boolean homeVisit) {
        this.homeVisit = homeVisit;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getAddressText() {
        return addressText;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getAgendaId() {
        return agendaId;
    }

    public void setAgendaId(String agendaId) {
        this.agendaId = agendaId;
    }

    public Set<CalendarItemTagDto> getMeetingTags() {
        return meetingTags;
    }

    public void setMeetingTags(Set<CalendarItemTagDto> meetingTags) {
        this.meetingTags = meetingTags;
    }

    public FlowItemDto getFlowItem() {
        return flowItem;
    }

    public void setFlowItem(FlowItemDto flowItem) {
        this.flowItem = flowItem;
    }
}
