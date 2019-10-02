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

package org.taktik.icure.services.external.rest.v1.dto.filter.service;


import com.google.common.base.Objects;
import org.taktik.icure.entities.embed.Service;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

import java.util.Set;

@JsonPolymorphismRoot(Filter.class)
public class ServiceBySecretForeignKeys extends Filter<Service> implements org.taktik.icure.dto.filter.service.ServiceBySecretForeignKeys {
	String healthcarePartyId;
    Set<String> patientSecretForeignKeys;

	public ServiceBySecretForeignKeys() {
	}

	public ServiceBySecretForeignKeys(String healthcarePartyId, Set<String> patientSecretForeignKeys) {
		this.healthcarePartyId = healthcarePartyId;
		this.patientSecretForeignKeys = patientSecretForeignKeys;
	}

	@Override
	public String getHealthcarePartyId() {
		return healthcarePartyId;
	}

	public void setHealthcarePartyId(String healthcarePartyId) {
		this.healthcarePartyId = healthcarePartyId;
	}

	@Override
	public Set<String> getPatientSecretForeignKeys() {
		return patientSecretForeignKeys;
	}

	public void setPatientSecretForeignKey(Set<String> patientSecretForeignKeys) {
		this.patientSecretForeignKeys = patientSecretForeignKeys;
	}

    public void setPatientSecretForeignKeys(Set<String> patientSecretForeignKeys) {
        this.patientSecretForeignKeys = patientSecretForeignKeys;
    }

    @Override
	public boolean matches(Service item) {
		return patientSecretForeignKeys != null && !patientSecretForeignKeys.isEmpty() && (healthcarePartyId == null || item.getDelegations().keySet().contains(healthcarePartyId))
				&& (item.getSecretForeignKeys() != null && item.getSecretForeignKeys().stream().anyMatch(sfk -> patientSecretForeignKeys.contains(sfk)));
	}
}
