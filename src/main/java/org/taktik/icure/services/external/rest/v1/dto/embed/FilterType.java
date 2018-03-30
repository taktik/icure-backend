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

package org.taktik.icure.services.external.rest.v1.dto.embed;

import java.util.HashMap;
import java.util.Map;

public enum FilterType {
	BIRTH_DATE("birthdate"),
	NAME("name"),
	SSIN("ssin"),
	OPENING_DATE("openingdate");

	private static final Map<String, FilterType> enumsByValue = new HashMap<>();

	private String filterType;

	FilterType(String filterType) {
		this.filterType = filterType;
	}

	public String value() {
		return filterType;
	}

	public static FilterType fromValue(String value) {
		return enumsByValue().get(value);
	}

	private static Map<String, FilterType> enumsByValue() {
		if (enumsByValue.isEmpty()) {
			for (FilterType filterType : FilterType.values()) {
				enumsByValue.put(filterType.value(), filterType);
			}
		}
		return enumsByValue;
	}
}
