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

package org.taktik.icure.dto.filter.chain;

import java.util.List;
import java.util.stream.Collectors;

import org.taktik.icure.dto.filter.Filter;
import org.taktik.icure.dto.filter.predicate.Predicate;
import org.taktik.icure.entities.base.Identifiable;

public class FilterChain<O extends Identifiable<String>> {
	private Filter<String,O> filter;
	private Predicate predicate;

	public FilterChain() {
	}

	public FilterChain(Filter<String,O> filter, Predicate predicate) {
		this.filter = filter;
		this.predicate = predicate;
	}

	public Filter<String,O> getFilter() {
		return filter;
	}

	public void setFilter(Filter<String,O> filter) {
		this.filter = filter;
	}

	public Predicate getPredicate() {
		return predicate;
	}

	public void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}

	public List<O> applyTo(List<O> items) {
		List<O> filteredItems = filter.applyTo(items);
		return predicate==null?filteredItems:filteredItems.stream().filter(predicate::apply).collect(Collectors.toList());
	}
}
