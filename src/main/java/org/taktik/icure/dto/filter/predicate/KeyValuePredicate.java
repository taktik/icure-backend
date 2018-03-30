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

package org.taktik.icure.dto.filter.predicate;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.BiFunction;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.xstream.NumberConverter;

@JsonPolymorphismRoot(Predicate.class)
public class KeyValuePredicate implements Predicate {
	private String key;
	private Operator operator;
	private Object value;

	PropertyUtilsBean pub = new PropertyUtilsBean();

	public KeyValuePredicate() {
	}

	public KeyValuePredicate(String key, Operator operator, Object value) {
		this.key = key;
		this.operator = operator;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public boolean apply(Identifiable<String> input) {
		try {
			return operator.apply((Comparable) pub.getProperty(input, key), (Comparable) value);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public enum Operator {
		EQUAL("==", (a, b) -> {
			if (a!= null && a instanceof Number && b != null && b instanceof Number) {
				if (((Number) a).longValue() == ((Number) b).longValue()) {
					return true;
				}
				if (((Number) a).doubleValue() == ((Number) b).doubleValue()) {
					return true;
				}

			}
			return Objects.equals(a, b);
		}),
		NOTEQUAL("!=", (a, b) -> !(EQUAL.lambda.apply(a,b))),
		GREATERTHAN(">", (a, b) -> a.compareTo(b)<0),
		SMALLERTHAN("<", (a, b) -> a.compareTo(b)>0),
		GREATERTHANOREQUAL(">=", (a, b) -> a.compareTo(b)<=0),
		SMALLERTHANOREQUAL("<=", (a, b) -> a.compareTo(b)>=0),
		LIKE("%=", (a, b) -> a.toString().matches(b.toString())),
		ILIKE("%%=", (a, b) -> a.toString().toLowerCase().matches(b.toString().toLowerCase()));

		String code;
		BiFunction<Comparable,Comparable,Boolean> lambda;

		Operator(String code, BiFunction<Comparable,Comparable,Boolean> op) {
			this.code = code;
			this.lambda = op;
		}

		@Override
		public String toString() {
			return code;
		}

		public boolean apply(Comparable a, Comparable b) {
			return lambda.apply(a,b);
		}
	}
}
