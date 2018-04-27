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

package org.taktik.commons.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.functors.AndPredicate;
import org.apache.commons.collections4.functors.AnyPredicate;
import org.apache.commons.collections4.functors.NotPredicate;
import org.taktik.commons.beanutils.BeanPropertyValueComparableGreaterOrEqualThanPredicate;
import org.taktik.commons.beanutils.BeanPropertyValueComparableGreaterThanPredicate;
import org.taktik.commons.beanutils.BeanPropertyValueComparableIsDifferentPredicate;
import org.taktik.commons.beanutils.BeanPropertyValueComparableLessOrEqualThanPredicate;
import org.taktik.commons.beanutils.BeanPropertyValueComparableLessThanPredicate;
import org.taktik.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.taktik.commons.beanutils.BeanPropertyValueWildcardComparePredicate;
import org.taktik.commons.functors.AllExistsPredicate;
import org.taktik.commons.functors.AnyExistsPredicate;

public class FilterUtils {
	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> getPredicateAllExist(List<Filter> filters) {
		List<Predicate<T>> convertedSubpredicates = (List<Predicate<T>>) CollectionUtils.collect(filters, new Transformer<Filter, Predicate<T>>() {
			@Override
			public Predicate<T> transform(Filter filter) {
				return getPredicate(filter);
			}
		});
		return new AllExistsPredicate<>(convertedSubpredicates.toArray(new Predicate[]{}));
	}

	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> getPredicateAnyExist(List<Filter> filters) {
		List<Predicate<T>> convertedSubpredicates = (List<Predicate<T>>) CollectionUtils.collect(filters, new Transformer<Filter, Predicate<T>>() {
			@Override
			public Predicate<T> transform(Filter filter) {
				return getPredicate(filter);
			}
		});
		return new AnyExistsPredicate<>(convertedSubpredicates.toArray(new Predicate[]{}));
	}

	public static <T> Predicate<T> getPredicateAll(List<Filter> filters) {
		Predicate<T> finalPredicate = null;

		if (filters != null) {
			for (Filter filter : filters) {
				Predicate<T> predicate = getPredicate(filter);
				if (predicate != null) {
					finalPredicate = (finalPredicate == null) ? predicate : new AndPredicate<>(finalPredicate, predicate);
				}
			}
		}

		return finalPredicate;
	}

	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> getPredicateAny(List<Filter> filters) {
		Predicate<T> finalPredicate = null;

		if (filters != null) {
			List<Predicate<T>> predicates = new ArrayList<>();
			for (Filter filter : filters) {
				Predicate<T> predicate = getPredicate(filter);
				if (predicate != null) {
					predicates.add(predicate);
				}
			}
			if (!predicates.isEmpty()) {
				finalPredicate = new AnyPredicate<>(predicates.toArray(new Predicate[predicates.size()]));
			}
		}

		return finalPredicate;
	}

	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> getPredicate(Filter filter) {
		Predicate<T> predicate = null;

		if (filter instanceof FilterOnProperty) {
			FilterOnProperty filterOnProperty = (FilterOnProperty) filter;
			switch (filterOnProperty.getOperator()) {
				case EQUAL :
					predicate = new BeanPropertyValueEqualsPredicate<>(filterOnProperty.getKeyPath(), filterOnProperty.computeValue());
					break;
				case NOT_EQUAL:
					predicate = new BeanPropertyValueComparableIsDifferentPredicate<>(filterOnProperty.getKeyPath(), filterOnProperty.computeValue());
					break;
				case GREATER_THAN :
					predicate = new BeanPropertyValueComparableGreaterThanPredicate<>(filterOnProperty.getKeyPath(), filterOnProperty.computeValue());
					break;
				case GREATER_OR_EQUAL_THAN :
					predicate = new BeanPropertyValueComparableGreaterOrEqualThanPredicate<>(filterOnProperty.getKeyPath(), filterOnProperty.computeValue());
					break;
				case LESS_THAN :
					predicate = new BeanPropertyValueComparableLessThanPredicate<>(filterOnProperty.getKeyPath(), filterOnProperty.computeValue());
					break;
				case LESS_OR_EQUAL_THAN :
					predicate = new BeanPropertyValueComparableLessOrEqualThanPredicate<>(filterOnProperty.getKeyPath(), filterOnProperty.computeValue());
					break;
				case LIKE :
					predicate = new BeanPropertyValueWildcardComparePredicate<>(filterOnProperty.getKeyPath(), filterOnProperty.computeValue());
					break;
				case ILIKE :
					predicate = new BeanPropertyValueWildcardComparePredicate<>(filterOnProperty.getKeyPath(), filterOnProperty.computeValue());
					((BeanPropertyValueWildcardComparePredicate<T>) predicate).setFlags(Pattern.CASE_INSENSITIVE);
					break;
			}
			if (filterOnProperty.isReversed()) {
				predicate = new NotPredicate<>(predicate);
			}
		} else if (filter instanceof FilterTwoFilters) {
			FilterTwoFilters filterTwoFilters = (FilterTwoFilters) filter;
			Predicate<T> predicateA = getPredicate(filterTwoFilters.getFilterA());
			Predicate<T> predicateB = getPredicate(filterTwoFilters.getFilterB());
			if (predicateA != null && predicateB != null) {
				switch (filterTwoFilters.getOperator()) {
					case AND :
						predicate = new AndPredicate<>(predicateA, predicateB);
						break;
					case OR :
						predicate = new AnyPredicate<>(predicateA, predicateB);
						break;
				}
			} else if (predicateA == null) {
				predicate = predicateB;
			} else if (predicateB == null) {
				predicate = predicateA;
			}
		} else if (filter instanceof FilterListFilters) {
			FilterListFilters filterListFilters = (FilterListFilters) filter;
			if (filterListFilters.getFilters() != null && !filterListFilters.getFilters().isEmpty()) {
				switch (filterListFilters.getOperator()) {
					case ALL :
						predicate = getPredicateAll(filterListFilters.getFilters());
						break;
					case ANY :
						predicate = getPredicateAny(filterListFilters.getFilters());
						break;
					case ALLEXIST :
						predicate = getPredicateAllExist(filterListFilters.getFilters());
						break;
					case ANYEXIST :
						predicate = getPredicateAnyExist(filterListFilters.getFilters());
						break;
				}
			}
		}

		return predicate;
	}
}