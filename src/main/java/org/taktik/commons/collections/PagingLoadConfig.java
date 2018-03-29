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

package org.taktik.commons.collections;

import java.util.List;

import org.taktik.commons.filters.Filter;

public class PagingLoadConfig extends ListLoadConfig {
	private static final long serialVersionUID = 1L;

	private Integer offset;
	private Integer limit;

	public PagingLoadConfig() {
		offset = null;
		limit = null;
	}

	public PagingLoadConfig(Filter filter) {
		super(filter);
		offset = null;
		limit = null;
	}

	public PagingLoadConfig(List<SortOrder<String>> sortOrders) {
		super(sortOrders);
		offset = null;
		limit = null;
	}

	public PagingLoadConfig(Filter filter, List<SortOrder<String>> sortOrders) {
		super(filter, sortOrders);
		offset = null;
		limit = null;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}
}