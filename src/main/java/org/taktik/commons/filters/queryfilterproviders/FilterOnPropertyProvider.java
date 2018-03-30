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

package org.taktik.commons.filters.queryfilterproviders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;

import org.taktik.commons.filters.Filter;
import org.taktik.commons.filters.FilterListFilters;
import org.taktik.commons.filters.FilterOnProperty;

public class FilterOnPropertyProvider implements QueryFilterProvider {
	private String property;
	private FilterOnProperty.Operator operator;
	private Function<String, ?> queryValueParser;

	public FilterOnPropertyProvider(String property, FilterOnProperty.Operator operator, Function<String, ?> queryValueParser) {
		this.property = property;
		this.operator = operator;
		this.queryValueParser = queryValueParser;
	}

	@Override
	public Filter getFilterForQuery(String q) {
		if (q == null) {
			return null;
		}
		Object queryValue = queryValueParser == null ? q : queryValueParser.apply(q);
		if (queryValue == null) {
			return null;
		}

		if (queryValue instanceof DateRange) {
			DateRange dateRange = (DateRange) queryValue;

			if (dateRange.isNull()) {
				return new FilterOnProperty(property, FilterOnProperty.Operator.EQUAL, null);
			}

			FilterListFilters filterListFilters = new FilterListFilters(new ArrayList<>(), FilterListFilters.Operator.ALL);
			if (dateRange.getFromDate() != null) {
				filterListFilters.getFilters().add(new FilterOnProperty(property, FilterOnProperty.Operator.GREATER_OR_EQUAL_THAN, dateRange.getFromDate()));
			}
			if (dateRange.getToDate() != null) {
				filterListFilters.getFilters().add(new FilterOnProperty(property, FilterOnProperty.Operator.LESS_THAN, dateRange.getToDate()));
			}
			return filterListFilters;
		}

		if (queryValue instanceof DoubleRange) {
			DoubleRange doubleRange = (DoubleRange) queryValue;

			if (doubleRange.isNull()) {
				return new FilterOnProperty(property, FilterOnProperty.Operator.EQUAL, null);
			}

			FilterListFilters filterListFilters = new FilterListFilters(new ArrayList<>(), FilterListFilters.Operator.ALL);
			if (doubleRange.getFromValue() != null) {
				filterListFilters.getFilters().add(new FilterOnProperty(property, FilterOnProperty.Operator.GREATER_OR_EQUAL_THAN, doubleRange.getFromValue()));
			}
			if (doubleRange.getToValue() != null) {
				filterListFilters.getFilters().add(new FilterOnProperty(property, FilterOnProperty.Operator.LESS_THAN, doubleRange.getToValue()));
			}
			return filterListFilters;
		}

        if (queryValue instanceof LongRange) {
            LongRange longRange = (LongRange) queryValue;

            if (longRange.isNull()) {
                return new FilterOnProperty(property, FilterOnProperty.Operator.EQUAL, null);
            }

            FilterListFilters filterListFilters = new FilterListFilters(new ArrayList<>(), FilterListFilters.Operator.ALL);
            if (longRange.getFromValue() != null) {
                filterListFilters.getFilters().add(new FilterOnProperty(property, FilterOnProperty.Operator.GREATER_OR_EQUAL_THAN, longRange.getFromValue()));
            }
            if (longRange.getToValue() != null) {
                filterListFilters.getFilters().add(new FilterOnProperty(property, FilterOnProperty.Operator.LESS_THAN, longRange.getToValue()));
            }
            return filterListFilters;
        }

		return new FilterOnProperty(property, operator, (Serializable) queryValue);
	}
}
