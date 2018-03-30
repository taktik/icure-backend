/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.dto.be.vitalink;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataEntry implements Serializable {
    private Map<String,String> metadata = new HashMap<String, String>();
    private String businessDataString;
    private String dataEntryURI;
    private String reference;
    private Integer nodeVersion;

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getBusinessDataString() {
        return businessDataString;
    }

    public void setBusinessDataString(String businessDataString) {
        this.businessDataString = businessDataString;
    }

    public String getDataEntryURI() {
        return dataEntryURI;
    }

    public void setDataEntryURI(String dataEntryURI) {
        this.dataEntryURI = dataEntryURI;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getNodeVersion() {
        return nodeVersion;
    }

    public void setNodeVersion(Integer nodeVersion) {
        this.nodeVersion = nodeVersion;
    }
}