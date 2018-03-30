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

import java.util.ArrayList;
import java.util.List;

import org.taktik.commons.filters.Filter;
import org.taktik.commons.filters.FilterListFilters;

public class CompositeQueryFilterProvider implements QueryFilterProvider {
	private FilterListFilters.Operator operator;
	private List<QueryFilterProvider> childrenProviders;
	private int minimumLength;

	public CompositeQueryFilterProvider(FilterListFilters.Operator operator, List<QueryFilterProvider> childrenProviders, int minimumLength) {
		this.operator = operator;
		this.childrenProviders = childrenProviders;
		this.minimumLength = minimumLength;
	}

	public CompositeQueryFilterProvider(FilterListFilters.Operator operator, List<QueryFilterProvider> childrenProviders) {
		this(operator, childrenProviders, 0);
	}


	@Override
	public Filter getFilterForQuery(String q) {
		if (q == null || q.length() < minimumLength) {
			return null;
		}

		List<Filter> filters = new ArrayList<>();

		for (QueryFilterProvider filterOnPropertyProvider : childrenProviders) {
			Filter filter = filterOnPropertyProvider.getFilterForQuery(q);
			if (filter != null) {
				filters.add(filter);
			}
		}

		if (filters.isEmpty()) {
			return null;
		}

		return new FilterListFilters(filters, operator);
	}

	public int getMinimumLength() {
		return minimumLength;
	}

	public void setMinimumLength(int minimumLength) {
		this.minimumLength = minimumLength;
	}
}