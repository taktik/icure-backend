package org.taktik.icure.services.external.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CalendarItemTypeDto extends StoredDto implements Serializable {

    private String name;

    private String color;

    private int duration;

    private boolean visit;

    private String externalRef;
    private String mikronoId;

    private List<String> docIds;

    private HashMap<String,String> otherInfos= new HashMap<String,String>();

    private HashMap<String,String> subjectByLanguage= new HashMap<String,String>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isVisit() {
        return visit;
    }

    public void setVisit(boolean visit) {
        this.visit = visit;
    }

    public String getExternalRef() {
        return externalRef;
    }

    public void setExternalRef(String externalRef) {
        this.externalRef = externalRef;
    }

    public String getMikronoId() {
        return mikronoId;
    }

    public void setMikronoId(String mikronoId) {
        this.mikronoId = mikronoId;
    }

    public List<String> getDocIds() {
        return docIds;
    }

    public void setDocIds(List<String> docIds) {
        this.docIds = docIds;
    }

    public HashMap<String, String> getOtherInfos() {
        return otherInfos;
    }

    public void setOtherInfos(HashMap<String, String> otherInfos) {
        this.otherInfos = otherInfos;
    }

    public HashMap<String, String> getSubjectByLanguage() {
        return subjectByLanguage;
    }

    public void setSubjectByLanguage(HashMap<String, String> subjectByLanguage) {
        this.subjectByLanguage = subjectByLanguage;
    }
}
