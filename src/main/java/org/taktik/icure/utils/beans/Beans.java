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
import de.danielbechler.diff.visitor.Visit;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Beans<T> {

	private CanonicalBeans canonicalBeans = new CanonicalBeans();
	private final ExpressionParser expressionParser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

	public Map<String, Object> getAsMapOfValues(T bean, List<String> propertyExpressions) {
		Map<String, Object> values = new HashMap<>();

		for (String propertyExpression : propertyExpressions) {
			Expression expression = expressionParser.parseExpression(propertyExpression);

			if (!expression.getExpressionString().isEmpty() && expression.isWritable(bean)) {
				Object value = expression.getValue(bean);
				values.put(propertyExpression, value);
			}
		}

		return values;
	}

	public String getDifferencesAsJSON(T original, T changed) {
		CanonicalBean canonicalOriginal = canonicalBeans.getCanonical(original);
		CanonicalBean canonicalChanged = canonicalBeans.getCanonical(changed);

		Node node = ObjectDifferFactory.getInstance().compare(canonicalOriginal, canonicalChanged);

		DifferencesAsJSONVisitor<CanonicalBean, String> visitor = new DifferencesAsJSONVisitor<>(canonicalOriginal);
		visitor.visit(node, new Visit());

		return visitor.getDifferences();
	}

	public Map<String, Object> getDifferencesAsMap(T original, T changed) {

		CanonicalBean canonicalOriginal = canonicalBeans.getCanonical(original);
		CanonicalBean canonicalChanged = canonicalBeans.getCanonical(changed);

		Node node = ObjectDifferFactory.getInstance().compare(canonicalOriginal, canonicalChanged);

		DifferencesAsMapVisitor<CanonicalBean, Map<String, Object>> visitor = new DifferencesAsMapVisitor<>(canonicalOriginal);
		visitor.visit(node, new Visit());

		return visitor.getDifferences();
	}

	public void updateFromMapOfValues(T bean, Map<String, Object> values) {

		for (Map.Entry<String, Object> entry : values.entrySet()) {
			String propertyExpression = entry.getKey();
			Object value = entry.getValue();

			Expression expression = expressionParser.parseExpression(propertyExpression);

			if (expression.isWritable(bean)) {
				expression.setValue(bean, value);
			}
		}
	}
}
