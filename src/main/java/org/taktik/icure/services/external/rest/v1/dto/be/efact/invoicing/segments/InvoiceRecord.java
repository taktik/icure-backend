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

package org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing.segments;

import java.util.Map;

@SuppressWarnings("unused")
public abstract class InvoiceRecord {

	private String message;
	private Map<String, String> valuesByZone;
	private Map<String, ZoneDescription> zoneDescriptionsByZone;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, String> getValuesByZone() {
		return valuesByZone;
	}

	public void setValuesByZone(Map<String, String> valuesByZone) {
		this.valuesByZone = valuesByZone;
	}

	public Map<String, ZoneDescription> getZoneDescriptionsByZone() {
		return zoneDescriptionsByZone;
	}

	public void setZoneDescriptionsByZone(Map<String, ZoneDescription> zoneDescriptionsByZone) {
		this.zoneDescriptionsByZone = zoneDescriptionsByZone;
	}
}
