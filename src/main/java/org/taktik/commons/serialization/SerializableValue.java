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

package org.taktik.commons.serialization;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class SerializableValue implements Serializable {
	private static final long serialVersionUID = 1L;

	private Enum<?> enumObject;
	private String stringObject;
	private Date dateObject;
	private Character characterObject;
	private Byte byteObject;
	private Short shortObject;
	private Integer integerObject;
	private Long longObject;
	private Boolean booleanObject;
	private Float floatObject;
	private Double doubleObject;
	private UUID uuidObject;

	public SerializableValue() {
	}

	public SerializableValue(Serializable object) {
		setValue(object);
	}

	public Serializable getValue() {
		if (enumObject != null) {
			return enumObject;
		} else if (stringObject != null) {
			return stringObject;
		} else if (dateObject != null) {
			return dateObject;
		} else if (characterObject != null) {
			return characterObject;
		} else if (byteObject != null) {
			return byteObject;
		} else if (shortObject != null) {
			return shortObject;
		} else if (integerObject != null) {
			return integerObject;
		} else if (longObject != null) {
			return longObject;
		} else if (booleanObject != null) {
			return booleanObject;
		} else if (floatObject != null) {
			return floatObject;
		} else if (doubleObject != null) {
			return doubleObject;
		} else if (uuidObject != null) {
			return uuidObject;
		}

		return null;
	}

	public void setValue(Serializable object) {
		enumObject = null;
		stringObject = null;
		dateObject = null;
		characterObject = null;
		byteObject = null;
		shortObject = null;
		integerObject = null;
		longObject = null;
		booleanObject = null;
		floatObject = null;
		doubleObject = null;
		uuidObject = null;

		if (object instanceof Enum) {
			enumObject = (Enum<?>) object;
		} else if (object instanceof String) {
			stringObject = (String) object;
		} else if (object instanceof Date) {
			dateObject = new Date(((Date) object).getTime());
		} else if (object instanceof Character) {
			characterObject = (Character) object;
		} else if (object instanceof Byte) {
			byteObject = (Byte) object;
		} else if (object instanceof Short) {
			shortObject = (Short) object;
		} else if (object instanceof Integer) {
			integerObject = (Integer) object;
		} else if (object instanceof Long) {
			longObject = (Long) object;
		} else if (object instanceof Boolean) {
			booleanObject = (Boolean) object;
		} else if (object instanceof Float) {
			floatObject = (Float) object;
		} else if (object instanceof Double) {
			doubleObject = (Double) object;
		} else if (object instanceof UUID) {
			uuidObject = (UUID) object;
		}
	}

	public Enum<?> getEnumObject() {
		return enumObject;
	}

	public String getStringObject() {
		return stringObject;
	}

	public Date getDateObject() {
		return dateObject;
	}

	public Character getCharacterObject() {
		return characterObject;
	}

	public Byte getByteObject() {
		return byteObject;
	}

	public Short getShortObject() {
		return shortObject;
	}

	public Integer getIntegerObject() {
		return integerObject;
	}

	public Long getLongObject() {
		return longObject;
	}

	public Boolean getBooleanObject() {
		return booleanObject;
	}

	public Float getFloatObject() {
		return floatObject;
	}

	public Double getDoubleObject() {
		return doubleObject;
	}

	public UUID getUuidObject() {
		return uuidObject;
	}

	public void setEnumObject(Enum<?> enumObject) {
		this.enumObject = enumObject;
	}

	public void setStringObject(String stringObject) {
		this.stringObject = stringObject;
	}

	public void setDateObject(Date dateObject) {
		this.dateObject = dateObject;
	}

	public void setCharacterObject(Character characterObject) {
		this.characterObject = characterObject;
	}

	public void setByteObject(Byte byteObject) {
		this.byteObject = byteObject;
	}

	public void setShortObject(Short shortObject) {
		this.shortObject = shortObject;
	}

	public void setIntegerObject(Integer integerObject) {
		this.integerObject = integerObject;
	}

	public void setLongObject(Long longObject) {
		this.longObject = longObject;
	}

	public void setBooleanObject(Boolean booleanObject) {
		this.booleanObject = booleanObject;
	}

	public void setFloatObject(Float floatObject) {
		this.floatObject = floatObject;
	}

	public void setDoubleObject(Double doubleObject) {
		this.doubleObject = doubleObject;
	}

	public void setUuidObject(UUID uuidObject) {
		this.uuidObject = uuidObject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((booleanObject == null) ? 0 : booleanObject.hashCode());
		result = prime * result + ((byteObject == null) ? 0 : byteObject.hashCode());
		result = prime * result + ((characterObject == null) ? 0 : characterObject.hashCode());
		result = prime * result + ((dateObject == null) ? 0 : dateObject.hashCode());
		result = prime * result + ((doubleObject == null) ? 0 : doubleObject.hashCode());
		result = prime * result + ((enumObject == null) ? 0 : enumObject.hashCode());
		result = prime * result + ((floatObject == null) ? 0 : floatObject.hashCode());
		result = prime * result + ((integerObject == null) ? 0 : integerObject.hashCode());
		result = prime * result + ((longObject == null) ? 0 : longObject.hashCode());
		result = prime * result + ((shortObject == null) ? 0 : shortObject.hashCode());
		result = prime * result + ((stringObject == null) ? 0 : stringObject.hashCode());
		result = prime * result + ((uuidObject == null) ? 0 : uuidObject.hashCode());
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
		SerializableValue other = (SerializableValue) obj;
		if (booleanObject == null) {
			if (other.booleanObject != null)
				return false;
		} else if (!booleanObject.equals(other.booleanObject))
			return false;
		if (byteObject == null) {
			if (other.byteObject != null)
				return false;
		} else if (!byteObject.equals(other.byteObject))
			return false;
		if (characterObject == null) {
			if (other.characterObject != null)
				return false;
		} else if (!characterObject.equals(other.characterObject))
			return false;
		if (dateObject == null) {
			if (other.dateObject != null)
				return false;
		} else if (!dateObject.equals(other.dateObject))
			return false;
		if (doubleObject == null) {
			if (other.doubleObject != null)
				return false;
		} else if (!doubleObject.equals(other.doubleObject))
			return false;
		if (enumObject == null) {
			if (other.enumObject != null)
				return false;
		} else if (!enumObject.equals(other.enumObject))
			return false;
		if (floatObject == null) {
			if (other.floatObject != null)
				return false;
		} else if (!floatObject.equals(other.floatObject))
			return false;
		if (integerObject == null) {
			if (other.integerObject != null)
				return false;
		} else if (!integerObject.equals(other.integerObject))
			return false;
		if (longObject == null) {
			if (other.longObject != null)
				return false;
		} else if (!longObject.equals(other.longObject))
			return false;
		if (shortObject == null) {
			if (other.shortObject != null)
				return false;
		} else if (!shortObject.equals(other.shortObject))
			return false;
		if (stringObject == null) {
			if (other.stringObject != null)
				return false;
		} else if (!stringObject.equals(other.stringObject))
			return false;
		if (uuidObject == null) {
			if (other.uuidObject != null)
				return false;
		} else if (!uuidObject.equals(other.uuidObject))
			return false;
		return true;
	}
}