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

package org.taktik.icure.services.external.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.taktik.icure.constants.TypedValuesType;

import java.io.Serializable;
import java.util.Date;

public class TypedValueDto implements Serializable, Comparable<TypedValueDto> {
	private static final long serialVersionUID = 1L;

	private TypedValuesType type;

	private Boolean booleanValue;
	private Integer integerValue;
	private Double doubleValue;
	private String stringValue;
	private Date dateValue;

	public TypedValueDto() {
	}

	public <T> TypedValueDto(T value) {
		setValue(value);
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
    public <T> void setValue(T value) {
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

	public <T> void setTypeAndValue(TypedValuesType type, T value) {
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
				case STRING : case CLOB: case JSON:
					if (value instanceof String) {
						stringValue = (String) value;
					}
					break;
				case DATE :
					if (value instanceof Date) {
						dateValue = (Date) value;
					}
					break;
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int compareTo(TypedValueDto other) {
		return ((Comparable) getValue()).compareTo(other.getValue());
	}

	public TypedValuesType getType() {
		return type;
	}

	public void setType(TypedValuesType type) {
		this.type = type;
	}

	public void setValueType(TypedValuesType type) {
		this.type = type;
	}

	public Boolean getBooleanValue() {
		return booleanValue == null ? null : booleanValue == 1;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue == null ? null : (booleanValue ? 1 : 0);
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((booleanValue == null) ? 0 : booleanValue.hashCode());
		result = prime * result + ((dateValue == null) ? 0 : dateValue.hashCode());
		result = prime * result + ((doubleValue == null) ? 0 : doubleValue.hashCode());
		result = prime * result + ((integerValue == null) ? 0 : integerValue.hashCode());
		result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		TypedValueDto other = (TypedValueDto) obj;
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
}
