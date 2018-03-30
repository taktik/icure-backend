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

package org.taktik.icure.dto.filter;

import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;

import java.io.Serializable;
import java.util.Set;

public interface Filters {
	@JsonPolymorphismRoot(Filter.class)
    interface ConstantFilter<T extends Serializable, O extends Identifiable<T>> extends Filter<T,O> {
        Set<T> getConstant();
    }

	@JsonPolymorphismRoot(Filter.class)
    interface UnionFilter<T extends Serializable, O extends Identifiable<T>> extends Filter<T, O> {
        Filter<T,O>[] getFilters();
    }

	@JsonPolymorphismRoot(Filter.class)
    interface IntersectionFilter<T extends Serializable, O extends Identifiable<T>> extends Filter<T,O> {
        Filter<T,O>[] getFilters();
    }

	@JsonPolymorphismRoot(Filter.class)
    interface ComplementFilter<T extends Serializable, O extends Identifiable<T>> extends Filter<T,O> {
        Filter<T,O> getSuperSet();
        Filter<T,O> getSubSet();
    }

}
