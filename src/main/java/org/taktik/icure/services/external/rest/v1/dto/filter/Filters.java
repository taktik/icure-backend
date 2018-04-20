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

package org.taktik.icure.services.external.rest.v1.dto.filter;

import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Filters {
    public static UnionFilter union(Filter... filters) {
        return new UnionFilter(filters);
    }
    public static IntersectionFilter intersection(Filter... filters) {
        return new IntersectionFilter(filters);
    }
    public static  Filter complement(Filter superSet, Filter subset) {
        return new ComplementFilter(superSet, subset);
    }
    public static  ConstantFilter constant(Set set) {
        return new ConstantFilter(set);
    }

	@JsonPolymorphismRoot(Filter.class)
    public static class ConstantFilter<O extends Identifiable<String>> extends Filter<O> implements org.taktik.icure.dto.filter.Filters.ConstantFilter<String,O> {
        private Set constant;

        public ConstantFilter() {
        }

        public ConstantFilter(Set constant) {
            this.constant = constant;
        }

        public Set getConstant() {
            return constant;
        }

		@Override
		public boolean matches(O item) {
			return constant.contains(item.getId());
		}
	}

	@JsonPolymorphismRoot(Filter.class)
     public static class UnionFilter<O extends Identifiable<String>> extends Filter<O> implements org.taktik.icure.dto.filter.Filters.UnionFilter<String,O> {
        private Filter<O>[] filters;

        public UnionFilter() {
        }

        public UnionFilter(Filter<O>[] filters) {
            this.filters = filters;
        }
        public UnionFilter(List<Filter<O>> filters) {
            this.filters = filters.toArray(new Filter[filters.size()]);
        }

		@Override
		public boolean matches(O item) {
			for (Filter<O> f : filters) {
				if (f.matches(item)) { return true; }
			}
			return false;
		}

         @Override
         public org.taktik.icure.dto.filter.Filter<String,O>[] getFilters() {
             return filters;
         }
     }

	@JsonPolymorphismRoot(Filter.class)
    public static class IntersectionFilter<O extends Identifiable<String>> extends Filter<O> implements org.taktik.icure.dto.filter.Filters.IntersectionFilter<String,O> {
        private Filter<O>[] filters;

        public IntersectionFilter() {
        }

        public IntersectionFilter(Filter<O>[] filters) {
            this.filters = filters;
        }
        public IntersectionFilter(List<Filter<O>> filters) {
            this.filters = filters.toArray(new Filter[filters.size()]);
        }

        @Override
        public org.taktik.icure.dto.filter.Filter<String,O>[] getFilters() {
            return filters;
        }

		@Override
		public boolean matches(O item) {
			for (Filter<O> f : filters) {
				if (!f.matches(item)) { return false; }
			}
			return true;
		}
	}

	@JsonPolymorphismRoot(Filter.class)
    public static class ComplementFilter<O extends Identifiable<String>> extends Filter<O> implements org.taktik.icure.dto.filter.Filters.ComplementFilter<String,O> {
        private Filter<O> superSet;
        private Filter<O> subSet;

        public ComplementFilter() {
        }

        public ComplementFilter(Filter<O> superSet, Filter<O> subset) {
            this.superSet = checkNotNull(superSet);
            this.subSet = checkNotNull(subset);
        }

        public org.taktik.icure.dto.filter.Filter<String,O> getSuperSet() {
            return superSet;
        }

        public org.taktik.icure.dto.filter.Filter<String,O> getSubSet() {
            return subSet;
        }

		@Override
		public boolean matches(O item) {
			return superSet.matches(item) && !subSet.matches(item);
		}
	}

}
