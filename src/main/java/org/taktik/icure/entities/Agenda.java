package org.taktik.icure.entities;


import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.StoredDocument;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Agenda extends StoredDocument implements Serializable {

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
}
