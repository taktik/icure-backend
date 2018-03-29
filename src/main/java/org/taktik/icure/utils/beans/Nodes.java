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

import com.google.common.primitives.Primitives;
import de.danielbechler.diff.ObjectDifferFactory;
import de.danielbechler.diff.node.Node;
import de.danielbechler.diff.path.PropertyPath;
import de.danielbechler.diff.visitor.PropertyVisitor;

public class Nodes {

	public static Class<?> getTypeOrWrappedPrimitiveType(Node node) {
		Class<?> nodeType = node.getType();
		if (nodeType.isPrimitive()) {
			nodeType = Primitives.wrap(nodeType);
		}
		return nodeType;
	}
}
