package org.taktik.icure.services.external.rest.v1.dto;

import java.io.Serializable;

public class CalendarItemTypeDto implements Serializable {

    private String id;

    private String code;

    private String name;

    private String color;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
