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

import java.io.Serializable;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;

@JsonPolymorphismRoot(Predicate.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class NotPredicate implements Predicate {
	Predicate predicate;

	public NotPredicate(Predicate predicate) {
		this.predicate = predicate;
	}

	public Predicate getPredicate() {
		return predicate;
	}

	public void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}

	@Override
	public boolean apply(Identifiable<String> input) {
		return !predicate.apply(input);
	}
}
