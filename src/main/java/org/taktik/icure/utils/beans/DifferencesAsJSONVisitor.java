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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.danielbechler.diff.node.Node;
import de.danielbechler.diff.path.Element;
import de.danielbechler.diff.visitor.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DifferencesAsJSONVisitor<T, R> extends DifferencesVisitor<T, R> {

	private static final Logger logger = LoggerFactory.getLogger(DifferencesAsJSONVisitor.class);

	private ObjectMapper mapper = new ObjectMapper();
	private ObjectNode rootNode;

	public DifferencesAsJSONVisitor(T original) {
		super(original);
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	protected void addDifference(Node node, Visit visit) {

		JsonNode currentJsonNode = rootNode;
		Object value = node.canonicalGet(original);

		List<Element> pathElements = node.getPropertyPath().getElements();
		for (Element pathElement : pathElements) {

			String pathElementName = elementName(pathElement);

			if (pathElementName.trim().length() > 0) {
				String elementNodeName;

				if (isLastPathElement(pathElementName, pathElements)) {
					// We reached the last pathElement of the node

					if (isBetweenSquareBrackets(pathElementName)) {
						// Path elements within square brackets can either be a map of IndexedObjects or a set/list of elements

						if (value != null) {
							if (pathElementName.contains(IndexedObject.class.getCanonicalName())) {
								// Map of IndexedObjects are stored in a JSON set/list []
								storeInObjectNode((ObjectNode) currentJsonNode, (IndexedObject) value);

							} else {
								// end of path with simple collection values
								storeInArrayNode((ArrayNode) currentJsonNode, value);
							}
						}

					} else {
						elementNodeName = removeBrackets(pathElementName);

						if (value == null) {
							((ObjectNode) currentJsonNode).putNull(elementNodeName);
						} else {
							((ObjectNode) currentJsonNode).put(elementNodeName, value.toString());
						}
					}

				} else {

					if (isBetweenCurlyBraces(pathElementName)) {
						elementNodeName = removeBrackets(pathElementName);

						if (currentJsonNode.path(elementNodeName).isMissingNode()) {

							Node elementNode = getParentNodeOf(pathElement, node).getChild(pathElement);
							Class<?> elementNodeType = elementNode.getType();
							if (elementNode.hasChildren() && Set.class.isAssignableFrom(elementNodeType)) {

								// This node could be an array of elements [], a set of elements [] or a set of IndexedObjects {}
								Class<?> childType = getChildTypeOf(elementNode);
								if (IndexedObject.class.isAssignableFrom(childType)) {
									// Here the elementNode MUST be a JSON set!
									appendJsonSetTo(currentJsonNode, elementNodeName);

								} else {
									// Here the element node is either a list or a simple set. It's an array
									appendJsonArrayTo(currentJsonNode, elementNodeName);
								}

							} else {
								appendJsonSetTo(currentJsonNode, elementNodeName);
							}
						}

					} else if (isBetweenSquareBrackets(pathElementName)) {
						elementNodeName = removeBrackets(pathElementName);

						if (currentJsonNode.path(elementNodeName).isMissingNode()) {
							appendJsonSetTo(currentJsonNode, elementNodeName);
						}

					} else {
						elementNodeName = pathElementName;

						if (currentJsonNode.path(elementNodeName).isMissingNode()) {
							appendJsonSetTo(currentJsonNode, elementNodeName);
						}
					}

					currentJsonNode = nextJsonNode(currentJsonNode, elementNodeName);
				}
			}
		}

		logger.debug("value: " + value);
	}

	private void appendJsonArrayTo(JsonNode currentJsonNode, String arrayPropertyName) {
		if (ObjectNode.class.isAssignableFrom(currentJsonNode.getClass())) {
			((ObjectNode) currentJsonNode).putArray(arrayPropertyName);
		} else if (ArrayNode.class.isAssignableFrom(currentJsonNode.getClass())) {
			((ArrayNode) currentJsonNode).addArray();
		}
	}

	private void appendJsonSetTo(JsonNode currentJsonNode, String setPropertyName) {
		if (ObjectNode.class.isAssignableFrom(currentJsonNode.getClass())) {
			((ObjectNode)currentJsonNode).putObject(setPropertyName);
		} else if (ArrayNode.class.isAssignableFrom(currentJsonNode.getClass())) {
			((ArrayNode) currentJsonNode).addObject();
		}
	}

	private Class<?> getChildTypeOf(Node node) {
		Node child = node.getChildren().iterator().next();
		return child.getType();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected R getDifferences() {
		String json = null;
		try {
			json = mapper.writeValueAsString(rootNode);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		}
		return (R) json;
	}

	@Override
	protected void rootNodeAction(Node node, Visit visit) {
		rootNode = mapper.createObjectNode();
	}

	private String elementName(Element pathElement) {
		String elementName = pathElement.toString();
		logger.debug("pathElement: " + elementName + "/");
		return elementName;
	}

	/**
	 * Retrieves the parent node of the specified Element by examining the specified leaf node backwards. <br>
	 * The specified pathElement must not necessarily be a tree leaf, but it is important for the specified node to be
	 * farther in the tree than the parent node we're search for, otherwise the parent will not be found. <br>
	 * In other words, this method searches by going backwards, not forwards.
	 */
	private Node getParentNodeOf(Element pathElement, Node node) {
		Node elementParentNode = node.getParentNode();
		while (elementParentNode.getChild(pathElement) == null) {
			elementParentNode = elementParentNode.getParentNode();
		}
		return elementParentNode;
	}

	private boolean isBetweenCurlyBraces(String string) {
		return string.startsWith("{") && string.endsWith("}");
	}

	private boolean isBetweenSquareBrackets(String string) {
		return string.startsWith("[") && string.endsWith("]");
	}

	private boolean isLastPathElement(String pathElementName, List<Element> pathElements) {
		return pathElements.get(pathElements.size() - 1).toString().equals(pathElementName);
	}

	private JsonNode nextJsonNode(JsonNode currentJsonNode, String elementNodeName) {
		JsonNode nextJsonNode = currentJsonNode.get(elementNodeName);
		if (nextJsonNode == null) {
			// We are in a situation where the next element is a set or array pathElement, which is not identifiable as it is in the JsonNode.
			// Get last added child of this currentJsonNode as the nextJsonNode
			Iterator<JsonNode> childrenIterator = currentJsonNode.elements();
			while (childrenIterator.hasNext()) {
				nextJsonNode = childrenIterator.next();
			}
		}
		return nextJsonNode;
	}

	private String removeBrackets(String string) {
		String elementNodeName;
		if (isBetweenCurlyBraces(string)) {
			elementNodeName = string.substring(1, string.length() - 1);
		} else {
			elementNodeName = string;
		}
		return elementNodeName;
	}

	private void storeInArrayNode(ArrayNode arrayNode, Object value) {
		arrayNode.add(value.toString());
	}

	private void storeInObjectNode(ObjectNode objectNode, IndexedObject indexedObject) {
		objectNode.put(Integer.toString(indexedObject.getIndex()), indexedObject.getObject().toString());
	}
}
