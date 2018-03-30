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

package org.taktik.icure.utils.beans;

import de.danielbechler.diff.node.Node;
import de.danielbechler.diff.visitor.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class DifferencesVisitor<T, R> implements NodeVisitor {

	private static final Logger logger = LoggerFactory.getLogger(DifferencesVisitor.class);

	protected final Map<String, Object> differences = new HashMap<>();

	protected T original;

	protected DifferencesVisitor(T original) {
		this.original = original;
	}

	public void visit(Node node, Visit visit) {
		logger.debug("Visiting node '" + node.getPropertyPath().toString() + "'");

		if (node.isRootNode()) {
			logger.debug("It's the root node...");

			rootNodeAction(node, visit);

			if (node.hasChildren()) {
				node.getChildren().forEach(child -> visit(child, visit));
			}

		} else {
			if (node.hasChanges()) {
				logger.debug("Node '" + node.getPropertyPath().toString() + "' has changes...");

				if (node.isMapNode()) {
					Collection<Node> children = node.getChildren();
					children.forEach(child -> visit(child, visit));

				} else if (node.isCollectionNode()) {
					logger.debug("Node '" + node.getPropertyPath().toString() + "' is a collection");

					node.getChildren().forEach(child -> visit(child, visit));

				} else {
					addDifference(node, visit);
				}
			}
		}
	}

	protected abstract void addDifference(Node node, Visit visit);

	protected abstract R getDifferences();

	protected abstract void rootNodeAction(Node node, Visit visit);
}
