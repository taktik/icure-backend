/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v2.dto.be.mikrono;

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
