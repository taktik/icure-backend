package org.taktik.icure.services.external.rest.v1.dto;

import org.taktik.icure.entities.CalendarItem;
import org.taktik.icure.entities.Right;
import org.taktik.icure.entities.base.StoredDocument;

import java.util.List;

public class AgendaDto extends StoredDto {

    private String name;
    private String userId;
    private List<CalendarItem> events;
    private List<Right> rights;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
