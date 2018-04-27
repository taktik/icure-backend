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

package org.taktik.icure.be.ehealth.dto.common;

import java.util.List;
import java.util.Map;

/**
 * Created by aduchate on 8/11/13, 15:52
 */
public class DocumentMessage extends Message {
    private Document document;
    private String freeText;
    private String patientInss;
    private List<Document> annex;

    private Map<String,String> customMetas;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public String getPatientInss() {
        return patientInss;
    }

    public void setPatientInss(String patientInss) {
        this.patientInss = patientInss;
    }

    public List<Document> getAnnex() {
        return annex;
    }

    public void setAnnex(List<Document> annex) {
        this.annex = annex;
    }

    public Map<String, String> getCustomMetas() {
        return customMetas;
    }

    public void setCustomMetas(Map<String, String> customMetas) {
        this.customMetas = customMetas;
    }
}
