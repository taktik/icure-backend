package org.taktik.icure.services.external.rest.v1.dto.be.mikrono;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class MikronoAppointmentTypeRestDto implements Serializable {

    private String color; // "#123456"
    private int durationInMinutes;
    private String externalRef; // same as CalendarItemType.id, stored in mikrono to know linked topaz object has changed
    private String mikronoId;

    private List<String> docIds; // do not use

    private HashMap<String, String> otherInfos = new HashMap<String, String>();

    private HashMap<String, String> subjectByLanguage = new HashMap<String, String>();


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
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
