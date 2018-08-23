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
import org.taktik.icure.validation.NotNull;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeTableItem {
    @NotNull()
    private List<String> days;
    @NotNull()
    private List<TimeTableHour> hours;
    @NotNull()
    private List<String> recurrenceTypes;
    @NotNull()
    private String calendarItemTypeId;
    @NotNull()
    private boolean homeVisit;
    private String placeId;

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public List<TimeTableHour> getHours() {
        return hours;
    }

    public void setHours(List<TimeTableHour> hours) {
        this.hours = hours;
    }

    public List<String> getRecurrenceTypes() {
        return recurrenceTypes;
    }

    public void setRecurrenceTypes(List<String> recurrenceTypes) {
        this.recurrenceTypes = recurrenceTypes;
    }

    public String getCalendarItemTypeId() {
        return calendarItemTypeId;
    }

    public void setCalendarItemTypeId(String calendarItemTypeId) {
        this.calendarItemTypeId = calendarItemTypeId;
    }

    public boolean isHomeVisit() {
        return homeVisit;
    }

    public void setHomeVisit(boolean homeVisit) {
        this.homeVisit = homeVisit;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
