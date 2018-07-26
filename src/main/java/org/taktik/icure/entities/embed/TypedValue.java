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

package org.taktik.icure.entities.embed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.taktik.icure.constants.TypedValuesType;
import org.taktik.icure.utils.InstantDeserializer;
import org.taktik.icure.utils.InstantSerializer;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TypedValue implements Comparable<TypedValue>, Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	protected TypedValuesType type;
	protected Boolean booleanValue;
	protected Integer integerValue;
	protected Double doubleValue;
	protected String stringValue;
    @JsonSerialize(using = InstantSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = InstantDeserializer.class)
	protected Instant dateValue;

	public TypedValue() {
	}

	public <T> TypedValue(T value) {
		setValue(value);
	}

	public <T> TypedValue(TypedValuesType type, T value) {
		setTypeAndValue(type, value);

		// Check value has been set
		if (value != null && getValue() == null) {
			throw new IllegalArgumentException("value type incompatible with typedvalue type !");
		}
	}

	@SuppressWarnings("unchecked")
	@JsonIgnore
	public <T> T getValue() {
		if (type == null) {
			return null;
		}

		switch (type) {
			case BOOLEAN :
				return (T) booleanValue;
			case INTEGER :
				return (T) integerValue;
			case DOUBLE :
				return (T) doubleValue;
			case STRING : case CLOB: case JSON:
				return (T) stringValue;
			case DATE :
				return (T) dateValue;
		}

		return null;
	}

	@JsonIgnore
	private <T> void setValue(T value) {
		// Auto-detect type
		TypedValuesType type = null;
		if (value instanceof Boolean) {
			type = TypedValuesType.BOOLEAN;
		} else if (value instanceof Integer) {
			type = TypedValuesType.INTEGER;
		} else if (value instanceof Double) {
			type = TypedValuesType.DOUBLE;
		} else if (value instanceof String) {
			type = TypedValuesType.STRING;
		} else if (value instanceof Date) {
			type = TypedValuesType.DATE;
		}

		// Set type and value
		setTypeAndValue(type, value);
	}

	private <T> void setTypeAndValue(TypedValuesType type, T value) {
		// Set type
		this.type = type;

		// Reset value to null
		booleanValue = null;
		integerValue = null;
		doubleValue = null;
		stringValue = null;
		dateValue = null;

		// Set value if it matches the chosen type
		if (type != null && value != null) {
			switch (type) {
				case BOOLEAN :
					if (value instanceof Boolean) {
						booleanValue = (Boolean) value;
					}
					break;
				case INTEGER :
					if (value instanceof Integer) {
						integerValue = (Integer) value;
					}
					break;
				case DOUBLE :
					if (value instanceof Double) {
						doubleValue = (Double) value;
					}
					break;
				case STRING : case JSON: case CLOB:
					if (value instanceof String) {
						stringValue = (String) value;
					}
					break;
				case DATE :
					if (value instanceof Instant) {
						dateValue = (Instant) value;
					}
					else if (value instanceof Date) {
						dateValue = ((Date) value).toInstant();
					}
					break;
			}
		}
	}

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public int compareTo(TypedValue other) {
        return ((Comparable) getValue()).compareTo(other.getValue());
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((booleanValue == null) ? 0 : booleanValue.hashCode());
		result = prime * result + ((integerValue == null) ? 0 : integerValue.hashCode());
		result = prime * result + ((doubleValue == null) ? 0 : doubleValue.hashCode());
		result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
		result = prime * result + ((dateValue == null) ? 0 : dateValue.hashCode());
		return result;
	}

	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        TypedValue other = (TypedValue) obj;
        if (booleanValue == null) {
            if (other.booleanValue != null)
                return false;
        } else if (!booleanValue.equals(other.booleanValue))
            return false;
        if (dateValue == null) {
            if (other.dateValue != null)
                return false;
        } else if (!dateValue.equals(other.dateValue))
            return false;
        if (doubleValue == null) {
            if (other.doubleValue != null)
                return false;
        } else if (!doubleValue.equals(other.doubleValue))
            return false;
        if (integerValue == null) {
            if (other.integerValue != null)
                return false;
        } else if (!integerValue.equals(other.integerValue))
            return false;
        if (stringValue == null) {
            if (other.stringValue != null)
                return false;
        } else if (!stringValue.equals(other.stringValue))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

	public TypedValuesType getType() {
		return type;
	}

	public void setType(TypedValuesType value) {
		this.type = value;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean value) {
		this.booleanValue = value;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(Integer value) {
		this.integerValue = value;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double value) {
		this.doubleValue = value;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String value) {
		this.stringValue = value;
	}

	public Instant getDateValue() {
		return dateValue;
	}

	public void setDateValue(Instant value) {
		this.dateValue = value;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		if (type != null) {
			switch (type) {
				case BOOLEAN :
					return booleanValue.toString();
				case INTEGER :
					return integerValue.toString();
				case DOUBLE :
					return doubleValue.toString();
				case STRING : case CLOB: case JSON:
					return stringValue;
				case DATE :
					return dateValue.toString();
			}
		}
		return super.toString();
	}
}