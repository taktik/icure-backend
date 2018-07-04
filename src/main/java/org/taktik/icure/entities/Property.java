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

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.TypedValue;

import java.io.Serializable;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Property extends StoredDocument implements Identifiable<String>, Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	public static final Property EMPTY = new Property();

	private PropertyType type;
	protected TypedValue typedValue;

	public Property(PropertyType type, TypedValue typedValue) {
		this.type = type;
		this.typedValue = typedValue;
	}

	public Property() {
	}

	public TypedValue getTypedValue() {
		return typedValue;
	}

	public void setTypedValue(TypedValue value) {
		this.typedValue = value;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public PropertyType getType() {
		return type;
	}

	public void setType(PropertyType type) {
		this.type = type;
	}

	public Property(PropertyType type, Boolean value) {
		setType(type);
		setTypedValue(new TypedValue(value));
	}

	public Property(PropertyType type, Integer value) {
		setType(type);
		setTypedValue(new TypedValue(value));
	}

	public Property(PropertyType type, Double value) {
		setType(type);
		setTypedValue(new TypedValue(value));
	}

	public Property(PropertyType type, String value) {
		setType(type);
		setTypedValue(new TypedValue(value));
	}

	public Property(PropertyType type, Instant value) {
		setType(type);
		setTypedValue(new TypedValue(value));
	}

	public Property(PropertyType type, Long value) {
		setType(type);
		setTypedValue(new TypedValue(value));
	}


	@JsonIgnore
	public <T> T getValue() {
		return (T)(getTypedValue()!=null?getTypedValue().getValue():null);
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((typedValue == null) ? 0 : typedValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Property other = (Property) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (typedValue == null) {
            if (other.typedValue != null)
                return false;
        } else if (!typedValue.equals(other.typedValue))
            return false;
        return true;
    }
}