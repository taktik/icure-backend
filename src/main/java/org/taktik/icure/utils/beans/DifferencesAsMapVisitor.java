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

package org.taktik.icure.utils.beans;

import de.danielbechler.diff.node.Node;
import de.danielbechler.diff.visitor.Visit;

public class DifferencesAsMapVisitor<T, R> extends DifferencesVisitor<T, R> {

	public DifferencesAsMapVisitor(T original) {
		super(original);
	}

	@Override
	protected void addDifference(Node node, Visit visit) {
		differences.put(node.getPropertyPath().toString(), node.canonicalGet(original));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected R getDifferences() {
		return (R) differences;
	}

	@Override
	protected void rootNodeAction(Node node, Visit visit) {
		// Nothing needs to be done in this implementation
	}
}
