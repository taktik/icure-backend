package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.StoredDocument;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalendarItemType extends StoredDocument implements Serializable {

    //mikrono API: Put /rest/appointmentTypeResource

    private String name;

    private String color; //"#123456"

    private int duration; // mikrono: int durationInMinutes;
    private String externalRef; // same as topaz Id, to be used by mikrono
    private String mikronoId;

    private List<String> docIds;

    private HashMap<String,String> otherInfos= new HashMap<String,String>();

    private HashMap<String,String> subjectByLanguage= new HashMap<String,String>();

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
