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

package org.taktik.commons.filters;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.taktik.commons.serialization.SerializableValue;
import org.taktik.commons.service.rest.gson.JsonDiscriminated;

@JsonDiscriminated("filteronproperty")
@JsonTypeName("filteronproperty")
public class FilterOnProperty implements Filter {
	private static final long serialVersionUID = 7248119011716265805L;

	public enum Operator {
		EQUAL,
		GREATER_THAN,
		GREATER_OR_EQUAL_THAN,
		LESS_THAN,
		LESS_OR_EQUAL_THAN,
		LIKE,
		ILIKE,
		NOT_EQUAL
	}

	private Operator operator;
	private boolean reversed;
	private String keyPath;
	private SerializableValue value;

	private transient Map<String, SerializableValue> parameters;

	public FilterOnProperty() {
	}

	public FilterOnProperty(String keyPath, Operator operator, Serializable value) {
		this(keyPath, operator, new SerializableValue(value));
	}

	public FilterOnProperty(String keyPath, Operator operator, boolean reversed, Serializable value) {
		this(keyPath, operator, reversed, new SerializableValue(value));
	}

	public FilterOnProperty(String keyPath, Operator operator, SerializableValue value) {
		this(keyPath, operator, false, value);
	}

	public FilterOnProperty(String keyPath, Operator operator, boolean reversed, SerializableValue value) {
		this.operator = operator;
		this.reversed = reversed;
		this.keyPath = keyPath;
		this.value = value;
	}

	public Operator getOperator() {
		return operator;
	}

	public boolean isReversed() {
		return reversed;
	}

	public String getKeyPath() {
		String resultKeyPath = keyPath;

		if (resultKeyPath != null && parameters != null) {
			for (String parameterKey : parameters.keySet()) {
				resultKeyPath = resultKeyPath.replaceAll("\\$" + parameterKey, parameters.get(parameterKey).toString());
			}
		}

		return resultKeyPath;
	}

	public SerializableValue getValue() {
		return value;
	}

	public Serializable computeValue() {
		Serializable resultValue = (value != null) ? value.getValue() : null;

		if (resultValue != null && parameters != null) {
			for (String parameterKey : parameters.keySet()) {
				if (resultValue instanceof String) {
					if (resultValue.equals("$" + parameterKey)) {
						resultValue = parameters.get(parameterKey);
						break;
					}
					resultValue = ((String) resultValue).replaceAll("\\$" + parameterKey, parameters.get(parameterKey).toString());
				}
			}
		}

		// Cast the value if necessary
		if (resultValue != null && resultValue instanceof String) {
			Boolean booleanValue = null;
			try {
				booleanValue = Boolean.valueOf((String) resultValue);
			} catch (Exception e) {
			}
			if (booleanValue != null && booleanValue.toString().equals(resultValue)) {
				resultValue = booleanValue;
			}

			Integer integerValue = null;
			try {
				integerValue = Integer.valueOf((String) resultValue);
			} catch (Exception e) {
			}
			if (integerValue != null && integerValue.toString().equals(resultValue)) {
				resultValue = integerValue;
			}
		}

		return resultValue;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public void setReversed(boolean reversed) {
		this.reversed = reversed;
	}

	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

	public void setValue(SerializableValue value) {
		this.value = value;
	}

	@Override
	public void loadParameters(Map<String, SerializableValue> parameters) {
		this.parameters = parameters;
	}
}