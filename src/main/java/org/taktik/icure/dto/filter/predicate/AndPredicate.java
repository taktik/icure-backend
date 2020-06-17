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

package org.taktik.icure.dto.filter.predicate;

import java.util.List;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;

@JsonPolymorphismRoot(Predicate.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class AndPredicate implements Predicate {
	List<Predicate> predicates;

    public AndPredicate() {
    }

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
