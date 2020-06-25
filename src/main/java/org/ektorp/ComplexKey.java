package org.ektorp;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.taktik.icure.services.external.rest.handlers.JacksonComplexKeyDeserializer;
import org.taktik.icure.services.external.rest.handlers.JacksonComplexKeySerializer;

/**
 * Class for creating complex keys for view queries.
 * The keys's components can consists of any JSON-encodeable objects, but are most likely to be Strings and Integers.
 * @author henrik lundgren
 *
 */
@JsonDeserialize(using = JacksonComplexKeyDeserializer.class)
@JsonSerialize(using = JacksonComplexKeySerializer.class)
public class ComplexKey {
	private final List<Object> components;

	private static final Object EMPTY_OBJECT = new Object();
	private static final Object[] EMPTY_ARRAY = new Object[0];

	public List<Object> getComponents() {
		return components;
	}

	public static ComplexKey of(Object... components) {
		return new ComplexKey(components);
	}
	/**
	 * Add this Object to the key if an empty object definition is desired:
	 * ["foo",{}]
	 * @return an object that will serialize to {}
	 */
	public static Object emptyObject() {
		return EMPTY_OBJECT;
	}
	/**
	 * Add this array to the key if an empty array definition is desired:
	 * [[],"foo"]
	 * @return an object array that will serialize to []
	 */
	public static Object[] emptyArray() {
		return EMPTY_ARRAY;
	}

	private ComplexKey(Object[] components) {
		this.components = Arrays.asList(components);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ComplexKey that = (ComplexKey) o;
		return Objects.equals(components, that.components);
	}

	@Override
	public int hashCode() {
		return Objects.hash(components);
	}
}
