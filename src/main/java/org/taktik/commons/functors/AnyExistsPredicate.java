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

package org.taktik.commons.functors;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.PredicateDecorator;

public class AnyExistsPredicate<T> implements PredicateDecorator<T> {
	private Predicate<T>[] subPredicates;

	public AnyExistsPredicate(Predicate<T>[] subPredicates) {
		this.subPredicates = subPredicates;
	}

	@Override
	public boolean evaluate(T o) {
		for (Predicate<T> subPredicate : subPredicates) {
			if (subPredicate.evaluate(o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Predicate<T>[] getPredicates() {
		return subPredicates;
	}
}
