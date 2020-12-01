/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.commons.collections;

import java.io.Serializable;
import java.util.List;

import org.taktik.commons.filters.Filter;

public class ListLoadConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	private Filter filter;
	private List<SortOrder<String>> sortOrders;

	public ListLoadConfig() {
	}

	public ListLoadConfig(Filter filter) {
		this.filter = filter;
	}

	public ListLoadConfig(List<SortOrder<String>> sortOrders) {
		this.sortOrders = sortOrders;
	}

	public ListLoadConfig(Filter filter, List<SortOrder<String>> sortOrders) {
		this.filter = filter;
		this.sortOrders = sortOrders;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public List<SortOrder<String>> getSortOrders() {
		return sortOrders;
	}

	public void setSortOrders(List<SortOrder<String>> sortOrders) {
		this.sortOrders = sortOrders;
	}
}
