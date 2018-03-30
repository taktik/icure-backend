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

package org.taktik.icure.services.external.rest.v1.dto.be.kmehr;

import java.io.Serializable;
import java.util.List;

import org.taktik.icure.services.external.rest.v1.dto.HealthElementDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto;

public class SumehrContentDto implements Serializable {
	List<ServiceDto> services;
	List<HealthElementDto> healthElements;

	public List<HealthElementDto> getHealthElements() {
		return healthElements;
	}

	public void setHealthElements(List<HealthElementDto> healthElements) {
		this.healthElements = healthElements;
	}

	public List<ServiceDto> getServices() {
		return services;
	}

	public void setServices(List<ServiceDto> services) {
		this.services = services;
	}
}
