/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.dto;

import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto;

import java.util.Map;

public class InsuranceDto extends StoredDto {
    protected boolean privateInsurance = false;
    protected boolean hospitalisationInsurance = false;
    protected boolean ambulatoryInsurance = false;

    protected String code;
    protected String parent;
    protected AddressDto address= new AddressDto();

    protected java.util.Map<String, String> name;

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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }
}
