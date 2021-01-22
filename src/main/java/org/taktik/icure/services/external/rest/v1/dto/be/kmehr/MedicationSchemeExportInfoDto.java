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

package org.taktik.icure.services.external.rest.v1.dto.be.kmehr;

import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto;

import java.io.Serializable;
import java.util.List;

public class MedicationSchemeExportInfoDto implements Serializable {
	List<String> secretForeignKeys;
    List<ServiceDto> services;
    List<HealthcarePartyDto> serviceAuthors; //TO pass hcp's that do not exist locally
	HealthcarePartyDto recipient;
	String comment;

	public List<String> getSecretForeignKeys() {
		return secretForeignKeys;
	}

	public void setSecretForeignKeys(List<String> secretForeignKeys) {
		this.secretForeignKeys = secretForeignKeys;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public HealthcarePartyDto getRecipient() {
		return recipient;
	}

	public void setRecipient(HealthcarePartyDto recipient) {
		this.recipient = recipient;
	}

    public List<ServiceDto> getServices() {
        return services;
    }

    public void setServices(List<ServiceDto> services) {
        this.services = services;
    }

    public List<HealthcarePartyDto> getServiceAuthors() {
        return serviceAuthors;
    }

    public void setServiceAuthors(List<HealthcarePartyDto> serviceAuthors) {
        this.serviceAuthors = serviceAuthors;
    }
}
