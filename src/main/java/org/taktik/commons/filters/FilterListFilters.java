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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.taktik.commons.serialization.SerializableValue;
import org.taktik.commons.service.rest.gson.JsonDiscriminated;

@JsonDiscriminated("filterlistfilters")
@JsonTypeName("filterlistfilters")
public class FilterListFilters implements Filter {
	private static final long serialVersionUID = 6212837374354747414L;

	public enum Operator {
		ALL,
		ANY,
		ALLEXIST,
		ANYEXIST
	}

	private Operator operator;
	private List<Filter> filters;

	public FilterListFilters() {
	}

	public FilterListFilters(Filter[] filters, Operator operator) {
		this.operator = operator;
		this.filters = Arrays.asList(filters);
	}

	public FilterListFilters(List<Filter> filters, Operator operator) {
		this.operator = operator;
		this.filters = filters;
	}

	public Operator getOperator() {
		return operator;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	@Override
	public void loadParameters(Map<String, SerializableValue> parameters) {
		if (filters != null) {
			for (Filter filter : filters) {
				filter.loadParameters(parameters);
			}
		}
	}
}