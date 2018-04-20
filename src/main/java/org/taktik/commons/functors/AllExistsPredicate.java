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

package org.taktik.commons.functors;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.PredicateDecorator;

public class AllExistsPredicate<T> implements PredicateDecorator<T> {

	private Predicate<T>[] subPredicates;

	public AllExistsPredicate(Predicate<T>[] subPredicates) {
		this.subPredicates = subPredicates;
	}

	@Override
	public boolean evaluate(T o) {
		for (Predicate<T> subPredicate : subPredicates) {
			if (!subPredicate.evaluate(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Predicate<T>[] getPredicates() {
		return subPredicates;
	}
}