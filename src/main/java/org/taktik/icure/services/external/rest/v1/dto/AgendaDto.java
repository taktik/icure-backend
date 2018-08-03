package org.taktik.icure.services.external.rest.v1.dto;

import org.taktik.icure.entities.CalendarItem;
import org.taktik.icure.entities.Right;

import java.util.List;

public class AgendaDto {

    private String id;
    private String name;
    private String user;
    private List<CalendarItem> events;
    private List<Right> rights;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<CalendarItem> getEvents() {
        return events;
    }

    public void setEvents(List<CalendarItem> events) {
        this.events = events;
    }

    public List<Right> getRights() {
        return rights;
    }

    public void setRights(List<Right> rights) {
        this.rights = rights;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
