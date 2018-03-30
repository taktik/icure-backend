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

package org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class InvoiceRecord {

	private StringBuilder message = new StringBuilder();
	private Map<String, String> valuesByZone = new HashMap<>();

	protected static int register(Map<String, ZoneDescription> zoneDescriptionsByZone, String zones, String label, String typeSymbol, int position, int length) {
		ZoneDescription zoneDescription = ZoneDescription.build(zones, label, typeSymbol, position, length);
		for (String zone : zones.split(",")) {
			zoneDescriptionsByZone.put(zone.trim(), zoneDescription);
		}
		return position + length;
	}

	public void append(ZoneDescription zoneDescription, String value) {
		String[] zones = zoneDescription.getZones();

		for (String zone : zones) {
			valuesByZone.put(zone, value);
		}

		message.append(value);
	}

	public String getMessage() {
		return message.toString();
	}

	public Map<String, String> getValuesByZone() {
		return valuesByZone;
	}

	public abstract Map<String, ZoneDescription> getZoneDescriptionsByZone();

	public boolean contains(String zone) {
		return valuesByZone.containsKey(zone);
	}

    public String getZoneValue(String key) {
        return valuesByZone.get(key);
    }

    public String getRecordNumber() {
        return getZoneValue("1");
    }

    public String getZoneDescription(String key) {
        ZoneDescription zoneDescription = getZoneDescriptionsByZone().get(key);
        return zoneDescription == null ? null : zoneDescription.getLabel();
    }

    public List<ZoneDescription> getZoneDescriptions() {
        List<ZoneDescription> zoneDescriptions = new ArrayList<>(new HashSet<>(getZoneDescriptionsByZone().values()));
        Collections.sort(zoneDescriptions, (zd1, zd2) -> new Integer(zd1.getPosition()).compareTo(zd2.getPosition()));
        return zoneDescriptions;
    }
}
