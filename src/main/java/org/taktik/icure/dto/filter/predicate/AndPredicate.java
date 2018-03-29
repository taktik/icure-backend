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

package org.taktik.icure.dto.filter.predicate;

import java.util.List;

import org.taktik.icure.entities.base.Identifiable;

public class AndPredicate implements Predicate {
	List<Predicate> predicates;

	public AndPredicate(List<Predicate> predicates) {
		this.predicates = predicates;
	}

	public List<Predicate> getPredicates() {
		return predicates;
	}

	public void setPredicates(List<Predicate> predicates) {
		this.predicates = predicates;
	}

	@Override
	public boolean apply(Identifiable<String> input) {
		for (Predicate p : predicates) {
			if (!p.apply(input)) {
				return false;
			}
		}
		return true;
	}
}
