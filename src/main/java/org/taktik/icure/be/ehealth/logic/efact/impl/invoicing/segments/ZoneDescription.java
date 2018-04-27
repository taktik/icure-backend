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

package org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class ZoneDescription {

	private String label;
	private int length;
	private int position;
	private Type type;
	private String[] zones;
    private String zonesList;

	private ZoneDescription(String label, int position, int length, Type type, String[] zones) {
		this.label = label;
		this.length = length;
		this.position = position;
		this.type = type;
		this.zones = zones;
        this.zonesList = StringUtils.join(zones,",");
	}

	public static ZoneDescription build(String commaSeparatedZones, String label, String typeSymbol, int position, int length) {
		Type type = Type.fromSymbol(typeSymbol);
		String[] splitZones = commaSeparatedZones.split(",");
		String[] zones = new String[splitZones.length];
		for (int i = 0; i < splitZones.length; i++) {
			zones[i] = splitZones[i].trim();
		}
		return new ZoneDescription(label, position, length, type, zones);
	}

	public String getLabel() {
		return label;
	}

	public int getLength() {
		return length;
	}

	public int getPosition() {
		return position;
	}

	public Type getType() {
		return type;
	}

	public String[] getZones() {
		return zones;
	}

    public String getZone() {
        return zones[0];
    }

    public String getZonesList() {
        return zonesList != null ? zonesList : StringUtils.join(zones,",");
    }

    public void setZonesList(String zonesList) {
        this.zonesList = zonesList;
    }

    public enum Type {
		ALPHANUMERICAL("A"),
		NUMERICAL("N");

		private final static Map<String, Type> BY_SYMBOL = new HashMap<>();

		private final String symbol;

		static {
			for (Type type : Type.values()) {
				BY_SYMBOL.put(type.getSymbol(), type);
			}
		}

		public static Type fromSymbol(String symbol) {
			return BY_SYMBOL.get(symbol);
		}

		Type(String symbol) {
			this.symbol = symbol;
		}

		public String getSymbol() {
			return symbol;
		}
	}
}
