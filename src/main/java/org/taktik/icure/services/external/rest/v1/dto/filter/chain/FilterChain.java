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

package org.taktik.icure.services.external.rest.v1.dto.filter.chain;

import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.services.external.rest.v1.dto.filter.FilterDto;
import org.taktik.icure.services.external.rest.v1.dto.filter.predicate.Predicate;

public class FilterChain<O extends Identifiable<String>> {
	FilterDto<O> filter;
	Predicate predicate;

	public FilterChain() {
	}

	public FilterChain(FilterDto<O> filter, Predicate predicate) {
		this.filter = filter;
		this.predicate = predicate;
	}

	public FilterDto<O> getFilter() {
		return filter;
	}

	public void setFilter(FilterDto<O> filter) {
		this.filter = filter;
	}

	public Predicate getPredicate() {
		return predicate;
	}

	public void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}
}
