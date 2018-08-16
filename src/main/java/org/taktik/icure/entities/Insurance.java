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

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.Address;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Insurance extends StoredDocument {
    protected boolean privateInsurance = false;
    protected boolean hospitalisationInsurance = false;
    protected boolean ambulatoryInsurance = false;

    protected String code;

    protected String parent; //ID of the parent

    protected Address address= new Address();

    protected java.util.Map<String, String> name;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isPrivateInsurance() {
        return privateInsurance;
    }

    public void setPrivateInsurance(boolean privateInsurance) {
        this.privateInsurance = privateInsurance;
    }

    public boolean isHospitalisationInsurance() {
        return hospitalisationInsurance;
    }

    public void setHospitalisationInsurance(boolean hospitalisationInsurance) {
        this.hospitalisationInsurance = hospitalisationInsurance;
    }

    public boolean isAmbulatoryInsurance() {
        return ambulatoryInsurance;
    }

    public void setAmbulatoryInsurance(boolean ambulatoryInsurance) {
        this.ambulatoryInsurance = ambulatoryInsurance;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
