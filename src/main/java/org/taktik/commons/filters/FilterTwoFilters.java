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

package org.taktik.commons.filters;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.taktik.commons.serialization.SerializableValue;
import org.taktik.commons.service.rest.gson.JsonDiscriminated;

@JsonDiscriminated("filtertwofilters")
@JsonTypeName("filtertwofilters")
public class FilterTwoFilters implements Filter {
	private static final long serialVersionUID = -7572043050452627037L;

	public enum Operator {
		AND,
		OR
	}

	private Operator operator;
	private Filter filterA;
	private Filter filterB;

	public FilterTwoFilters() {
	}

	public FilterTwoFilters(Filter filterA, Operator operator, Filter filterB) {
		this.operator = operator;
		this.filterA = filterA;
		this.filterB = filterB;
	}

	public Operator getOperator() {
		return operator;
	}

	public Filter getFilterA() {
		return filterA;
	}

	public Filter getFilterB() {
		return filterB;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public void setFilterA(Filter filterA) {
		this.filterA = filterA;
	}

	public void setFilterB(Filter filterB) {
		this.filterB = filterB;
	}

	@Override
	public void loadParameters(Map<String, SerializableValue> parameters) {
		if (filterA != null) {
			filterA.loadParameters(parameters);
		}
		if (filterB != null) {
			filterB.loadParameters(parameters);
		}
	}
}