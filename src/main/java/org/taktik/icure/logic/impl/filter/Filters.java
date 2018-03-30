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

package org.taktik.icure.logic.impl.filter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.taktik.icure.entities.base.Identifiable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Filters implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    private Map<String,Filter> filters = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public <T extends Serializable> Set<T> resolve(org.taktik.icure.dto.filter.Filter<T,?> filter) {
        String truncatedFullClassName = filter.getClass().getName().replaceAll(".+?filter\\.","");

        Filter f = filters.get(truncatedFullClassName);

        if (f==null) {
            try {
                filters.put(truncatedFullClassName, f = (Filter) applicationContext.getAutowireCapableBeanFactory().<Filter>createBean(Class.<Filter>forName("org.taktik.icure.logic.impl.filter." + truncatedFullClassName), AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return f.resolve(filter, this);
    }

    public static class ConstantFilter<T extends Serializable, O extends Identifiable<T>> implements Filter<T,O,org.taktik.icure.dto.filter.Filters.ConstantFilter<T,O>> {
        @Override
        public Set<T> resolve(org.taktik.icure.dto.filter.Filters.ConstantFilter<T,O> filter, Filters context) {
            return filter.getConstant();
        }
    }

    public static class UnionFilter<T extends Serializable, O extends Identifiable<T>> implements Filter<T,O,org.taktik.icure.dto.filter.Filters.UnionFilter<T,O>> {
        @Override
        public Set<T> resolve(org.taktik.icure.dto.filter.Filters.UnionFilter<T,O> filter, Filters context) {
            org.taktik.icure.dto.filter.Filter<T,O>[] filters = filter.getFilters();
            if (filters.length==0) { return new HashSet<>(); }

            Set<T> result = new HashSet<>();
			for (org.taktik.icure.dto.filter.Filter<T, O> f : filters) {
				result.addAll(context.resolve(f));
			}

            return result;
        }
    }

    public static class IntersectionFilter<T extends Serializable, O extends Identifiable<T>> implements Filter<T,O,org.taktik.icure.dto.filter.Filters.IntersectionFilter<T,O>> {
        @Override
        public Set<T> resolve(org.taktik.icure.dto.filter.Filters.IntersectionFilter<T,O> filter, Filters context) {
            org.taktik.icure.dto.filter.Filter<T,O>[] filters = filter.getFilters();
            if (filters.length==0) { return new HashSet<>(); }
            Set<T> result = new HashSet<>();
            for (int i=0;i< filters.length;i++) {
				if (i == 0) {
					result.addAll(context.resolve(filters[i]));
				} else {
					result.retainAll(context.resolve(filters[i]));
				}
            }

            return result;
        }
    }

    public static class ComplementFilter<T extends Serializable, O extends Identifiable<T>> implements Filter<T,O,org.taktik.icure.dto.filter.Filters.ComplementFilter<T,O>> {
        @Override
        public Set<T> resolve(org.taktik.icure.dto.filter.Filters.ComplementFilter<T,O> filter, Filters context) {
            Set<T> result = context.resolve(filter.getSuperSet());
            result.removeAll(context.resolve(filter.getSubSet()));

            return result;
        }
    }
}
