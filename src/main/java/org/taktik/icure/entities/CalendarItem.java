/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.StoredICureDocument;
import org.taktik.icure.entities.embed.Address;
import org.taktik.icure.validation.AutoFix;
import org.taktik.icure.validation.NotNull;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarItem extends StoredICureDocument {

    @NotNull
    protected String title;

    private String calendarItemTypeId;

    private String masterCalendarItemId;

    @Deprecated
    protected String patientId;

    protected Boolean important;

    protected Boolean homeVisit;

    protected String phoneNumber;

    protected String placeId;

    protected Address address;

    protected String addressText;

    @NotNull(autoFix = AutoFix.FUZZYNOW)
    protected Long startTime; // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.

    @NotNull(autoFix = AutoFix.FUZZYNOW)
    protected Long endTime; // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.

    protected Long confirmationTime; // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.

    protected String recurrenceId;

    protected String confirmationId;

    protected Long duration;

    protected Boolean allDay;

    protected String details;

    protected Boolean wasMigrated;

    @NotNull
    protected String agendaId;

    protected Set<CalendarItemTag> meetingTags;

    protected FlowItem flowItem;

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

    public String getMasterCalendarItemId() {
        return masterCalendarItemId;
    }

    public void setMasterCalendarItemId(String masterCalendarItemId) {
        this.masterCalendarItemId = masterCalendarItemId;
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

    public String getRecurrenceId() {
        return recurrenceId;
    }

    public void setRecurrenceId(String recurrenceId) {
        this.recurrenceId = recurrenceId;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAddressText() {
        return addressText;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
    }

    public String getAgendaId() {
        return agendaId;
    }

    public void setAgendaId(String agendaId) {
        this.agendaId = agendaId;
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

    public Set<CalendarItemTag> getMeetingTags() {
        return meetingTags;
    }

    public void setMeetingTags(Set<CalendarItemTag> meetingTags) {
        this.meetingTags = meetingTags;
    }

    public FlowItem getFlowItem() {
        return flowItem;
    }

    public void setFlowItem(FlowItem flowItem) {
        this.flowItem = flowItem;
    }

    public Boolean getWasMigrated() { return wasMigrated; }

    public void setWasMigrated(Boolean wasMigrated) { this.wasMigrated = wasMigrated; }

    public Long getConfirmationTime() {
        return confirmationTime;
    }

    public void setConfirmationTime(Long confirmationTime) {
        this.confirmationTime = confirmationTime;
    }

    public String getConfirmationId() {
        return confirmationId;
    }

    public void setConfirmationId(String confirmationId) {
        this.confirmationId = confirmationId;
    }
}
