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

package org.taktik.icure.be.healthdata.dto;

import org.taktik.icure.be.ehealth.dto.common.IdentifierType;

import java.io.Serializable;

import static org.apache.commons.lang.StringUtils.defaultString;
import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Created by aduchate on 8/11/13, 16:16
 */
public class Addressee implements Serializable {
    private String identificationValue;
    private String identificationType;
    private String quality;
    private String applicationId;
    private String lastName;
    private String firstName;
    private String organizationName;
    private String personInOrganisation;

	public String getIdentificationValue() {
		return identificationValue;
	}

	public void setIdentificationValue(String identificationValue) {
		this.identificationValue = identificationValue;
	}

	public String getIdentificationType() {
		return identificationType;
	}

	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

	public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getPersonInOrganisation() {
        return personInOrganisation;
    }

    public void setPersonInOrganisation(String personInOrganisation) {
        this.personInOrganisation = personInOrganisation;
    }

    @Override
    public String toString() {
        return isEmpty(organizationName) ? String.format("%s %s %s",defaultString(quality.toString()),defaultString(firstName),defaultString(lastName)) :
                String.format("%s %s - %s %s %s",defaultString(organizationName),defaultString(personInOrganisation),defaultString(quality.toString()),defaultString(firstName),defaultString(lastName));
    }
}
