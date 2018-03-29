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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.taktik.commons.filters.queryfilterproviders.QueryFilterProvider;

public class QueryParser {
	public static final char PREFIX_SEPARATOR = ':';
	private FilterListFilters.Operator globalOperator;

	private Map<String, QueryFilterProvider> queryFilterProviderMap;

	private QueryFilterProvider defaultQueryFilterProvider;

	public QueryParser(QueryFilterProvider defaultQueryFilterProvider, FilterListFilters.Operator globalOperator) {
		this.defaultQueryFilterProvider = defaultQueryFilterProvider;
		this.globalOperator = globalOperator;
		this.queryFilterProviderMap = new HashMap<>();
	}

	public QueryParser(QueryFilterProvider defaultQueryFilterProvider) {
		this(defaultQueryFilterProvider, FilterListFilters.Operator.ALL);
	}

	public void registerQueryFilterProvider(String prefix, QueryFilterProvider provider) {
		queryFilterProviderMap.put(prefix, provider);
	}

	public void unRegisterQueryFilterProvider(String prefix) {
		queryFilterProviderMap.remove(prefix);
	}

	public Filter parseQuery(String query) {
		if (query == null) {
			return null;
		}
		query = query.trim();
		if (query.isEmpty()) {
			return null;
		}

		List<Filter> filters = new ArrayList<>();
		String[] tokens = query.split(" +");
		for (String token : tokens) {
			Filter filter = null;
			String queryPart = null;
			QueryFilterProvider queryFilterProvider = null;
			int idx = token.indexOf(PREFIX_SEPARATOR);
			if (idx == -1) {
				queryPart = token;
				queryFilterProvider = defaultQueryFilterProvider;
			} else if (idx < token.length() - 1) {
				String prefix = token.substring(0, idx);
				queryPart = token.substring(idx + 1);
				queryFilterProvider = queryFilterProviderMap.get(prefix);
				if (queryFilterProvider == null) {
					queryFilterProvider = defaultQueryFilterProvider;
				}
			}
			if (queryFilterProvider != null) {
				filter = queryFilterProvider.getFilterForQuery(queryPart);
			}
			if (filter != null) {
				filters.add(filter);
			}
		}

		if (filters.isEmpty()) {
			return null;
		}

		return new FilterListFilters(filters, globalOperator);
	}
}