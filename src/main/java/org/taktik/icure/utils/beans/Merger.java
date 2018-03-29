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

import de.danielbechler.diff.ObjectDifferFactory;
import de.danielbechler.diff.node.Node;
import de.danielbechler.diff.path.PropertyPath;
import de.danielbechler.diff.visitor.PropertyVisitor;
import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.utils.beans.annotations.ImplementedBy;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Merger {

	public <T> T merge(T original, T leader, T receiver) {

		Node leaderOriginalNode = getVisitedNode(leader, original);
		Node receiverOriginalNode = getVisitedNode(receiver, original);
		Node leaderReceiverNode = getVisitedNode(leader, receiver);

		merge(original, leader, receiver, leaderOriginalNode, receiverOriginalNode, leaderReceiverNode);

		return receiver;
	}

	@SuppressWarnings("unchecked")
	private <T> boolean applyDefaultBehaviors(T original, T leader, T receiver, Node leaderOriginalNode, Node receiverOriginalNode, Node leaderReceiverNode) {
		boolean goDeeper = true;

		// Get property values for the examined node (that is, the examined field)
		Object originalNodeValue = getOriginalNodeValueIfExists(original, leaderOriginalNode, receiverOriginalNode);
		Object leaderNodeValue = leaderReceiverNode.get(leader);
		Object receiverNodeValue = leaderReceiverNode.get(receiver);

		if (leaderReceiverNode.isCollectionNode()) {

			Collection<Node> leaderReceiverNodeChildren = leaderReceiverNode.getChildren();
			if (leaderReceiverNodeChildren.size() > 0 && Identifiable.class.isAssignableFrom(leaderReceiverNodeChildren.iterator().next().getType())) {
				// Collections contain identifiable objects: these objects must also be merged
				Collection<Identifiable> mergedCollection;
				Class<?> nodeType = leaderReceiverNode.getType();

				if (Set.class.isAssignableFrom(nodeType)) {
					mergedCollection = new HashSet<>();
				} else {
					mergedCollection = new ArrayList<>();
				}

				Map<Object, Identifiable> originalMap = convertIdentifiablesCollectionToMap((Collection<Identifiable>) originalNodeValue);
				Map<Object, Identifiable> leaderMap = convertIdentifiablesCollectionToMap((Collection<Identifiable>) leaderNodeValue);
				Map<Object, Identifiable> receiverMap = convertIdentifiablesCollectionToMap((Collection<Identifiable>) receiverNodeValue);

				// Get all the unique ids from the three collections
				Set<Object> identities = new HashSet<>();
				originalMap.keySet().forEach(identities::add);
				leaderMap.keySet().forEach(identities::add);
				receiverMap.keySet().forEach(identities::add);

				for (Object key : identities) {
					if (leaderMap.containsKey(key) && receiverMap.containsKey(key)) {
						mergedCollection.add(merge(originalMap.get(key), leaderMap.get(key), receiverMap.get(key)));

					} else if (leaderMap.containsKey(key) && !receiverMap.containsKey(key)) {
						mergedCollection.add(leaderMap.get(key));

					} else if (!leaderMap.containsKey(key) && receiverMap.containsKey(key)) {
						mergedCollection.add(receiverMap.get(key));
					}
				}

				leaderReceiverNode.set(receiver, mergedCollection);

			} else {
				// Collections are merged, we don't examine the collection's items
				Collection leaderCollection = (Collection) leaderNodeValue;
				Collection receiverCollection = (Collection) receiverNodeValue;
				//noinspection unchecked
				receiverCollection.addAll(leaderCollection);
			}

			goDeeper = false;

		} else if (leaderReceiverNode.isMapNode()) {
			// Maps are merged and we don't merge the map's items
			Map leaderMap = (Map) leaderNodeValue;
			Map receiverMap = (Map) receiverNodeValue;
			//noinspection unchecked
			receiverMap.putAll(leaderMap);

			goDeeper = false;

		} else {
			if (originalNodeValue == null) {

				// favour non-null values
				if (leaderNodeValue != null) {

					// ...otherwise favour leader
					leaderReceiverNode.set(receiver, leaderNodeValue);
				}

				goDeeper = false;

			} else {
				if (originalNodeValue.equals(leaderNodeValue) && !originalNodeValue.equals(receiverNodeValue)) {
					// Updated values in regards to original bean are favored
					leaderReceiverNode.set(receiver, receiverNodeValue);

				} else {
					// Favour non-null values
					if (leaderNodeValue != null) {

						// ...otherwise leader properties are favored
						leaderReceiverNode.set(receiver, leaderNodeValue);
					}
				}
			}
		}

		return goDeeper;
	}

	private Map<Object, Identifiable> convertIdentifiablesCollectionToMap(Collection<Identifiable> identifiables) {
		Map<Object, Identifiable> map = new HashMap<>();
		identifiables.forEach(item -> map.put(item.getId(), item));
		return map;
	}

	private <T> boolean doClassLevelMerge(T original, T leader, T receiver, Node leaderOriginalNode, Node receiverOriginalNode, Node leaderReceiverNode) {

		List<Annotation> classAnnotations = Arrays.asList(leader.getClass().getAnnotations());
		for (Annotation annotation : classAnnotations) {

			executeMergeLogicSpecifiedByTheAnnotation(annotation, original, leader, receiver, leaderOriginalNode, receiverOriginalNode, leaderReceiverNode);
		}

		return true; // After a class-level merge, always dig deeper
	}

	private <T> boolean doMerge(T original, T leader, T receiver, Node leaderOriginalNode, Node receiverOriginalNode, Node leaderReceiverNode) {
		boolean goDeeper = true;

		if (leaderReceiverNode.isRootNode()) {
			doClassLevelMerge(original, leader, receiver, leaderOriginalNode, receiverOriginalNode, leaderReceiverNode);

		} else {
			goDeeper = doMethodLevelMerge(original, leader, receiver, leaderOriginalNode, receiverOriginalNode, leaderReceiverNode);
		}

		return goDeeper;
	}

	private <T> boolean doMethodLevelMerge(T original, T leader, T receiver, Node leaderOriginalNode, Node receiverOriginalNode, Node leaderReceiverNode) {
		boolean goDeeper = true;

		Set<Annotation> nodePropertyAnnotations = leaderReceiverNode.getPropertyAnnotations();
		if (nodePropertyAnnotations.size() > 0 && hasAtLeastOneMergeRelatedAnnotation(nodePropertyAnnotations)) {

			for (Annotation annotation : nodePropertyAnnotations) {
				executeMergeLogicSpecifiedByTheAnnotation(annotation, original, leader, receiver, leaderOriginalNode, receiverOriginalNode, leaderReceiverNode);
			}

		} else {
			goDeeper = applyDefaultBehaviors(original, leader, receiver, leaderOriginalNode, receiverOriginalNode, leaderReceiverNode);
		}

		return goDeeper;
	}

	private <T> void executeMergeLogicSpecifiedByTheAnnotation(Annotation annotation, T original, T leader, T receiver, Node leaderOriginalNode, Node receiverOriginalNode, Node leaderReceiverNode) {
		Class<? extends Annotation> annotationType = annotation.annotationType();
		if (annotationType.isAnnotationPresent(ImplementedBy.class)) {

			Class<? extends MergeLogic> mergeLogicClass = annotationType.getAnnotation(ImplementedBy.class).value();

			try {
				MergeLogic mergeLogic = mergeLogicClass.newInstance();
				mergeLogic.merge(original, leader, receiver, leaderOriginalNode, receiverOriginalNode, leaderReceiverNode);

			} catch (InstantiationException e) {
				throw new RuntimeException("Could not get an instance of " + mergeLogicClass + ". Maybe the default constructor is missing?", e);

			} catch (IllegalAccessException e) {
				throw new RuntimeException("Could not access the constructor of " + mergeLogicClass + ". Is the default constructor accessible?", e);
			}
		}
	}

	private <T> Object getOriginalNodeValueIfExists(T original, Node leaderOriginalNode, Node receiverOriginalNode) {
		Object originalNodeValue = null;
		if (leaderOriginalNode != null) {
			originalNodeValue = leaderOriginalNode.get(original);
		} else if (receiverOriginalNode != null) {
			try {
				originalNodeValue = receiverOriginalNode.get(original);
			} catch (Exception e) {
				// This happens when the property in receiver node value is also null, making originalNodeValue null by deduction
				originalNodeValue = null;
			}
		}
		return originalNodeValue;
	}

	private <T> Node getVisitedNode(T bean1, T bean2) {
		Node bean1Node = ObjectDifferFactory.getInstance().compare(bean1, bean2);
		bean1Node.visit(new PropertyVisitor(PropertyPath.buildRootPath()));
		return bean1Node;
	}

	private boolean hasAtLeastOneMergeRelatedAnnotation(Set<Annotation> annotations) {
		boolean result = false;
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().isAnnotationPresent(ImplementedBy.class)) {
				result = true;
			}
		}
		return result;
	}

	private <T> void merge(T original, T leader, T receiver, Node leaderOriginalNode, Node receiverOriginalNode, Node leaderReceiverNode) {

		if (leaderReceiverNode != null && leaderReceiverNode.hasChanges()) {

			boolean goDeeper = doMerge(original, leader, receiver, leaderOriginalNode, receiverOriginalNode, leaderReceiverNode);

			// Iterate on the node's children, if any
			if (goDeeper) {
				Collection<Node> leaderReceiverNodeChildren = leaderReceiverNode.getChildren();

				for (Node leaderReceiverNodeChild : leaderReceiverNodeChildren) {
					Node leaderOriginalNodeChild = leaderOriginalNode.getChild(leaderReceiverNodeChild.getPathElement());

					Node receiverOriginalNodeChild = receiverOriginalNode.getChild(leaderReceiverNodeChild.getPathElement());

					merge(original, leader, receiver, leaderOriginalNodeChild, receiverOriginalNodeChild, leaderReceiverNodeChild);
				}
			}
		}
	}
}
