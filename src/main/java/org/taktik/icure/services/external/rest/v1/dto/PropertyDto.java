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

package org.taktik.icure.services.external.rest.v1.dto;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;

@Entity
public class PropertyDto extends StoredDto implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

    private PropertyTypeDto type;
	protected TypedValueDto typedValue;

    public PropertyDto(PropertyTypeDto type, Boolean value) {
        setType(type);
        setTypedValue(new TypedValueDto(value));
    }

    public PropertyDto(PropertyTypeDto type, Integer value) {
        setType(type);
        setTypedValue(new TypedValueDto(value));
    }

    public PropertyDto(PropertyTypeDto type, Double value) {
        setType(type);
        setTypedValue(new TypedValueDto(value));
    }

    public PropertyDto(PropertyTypeDto type, String value) {
        setType(type);
        setTypedValue(new TypedValueDto(value));
    }

    public PropertyDto(PropertyTypeDto type, Date value) {
        setType(type);
        setTypedValue(new TypedValueDto(value));
    }

    public PropertyDto(PropertyTypeDto type, TypedValueDto typedValue) {
		this.type = type;
		this.typedValue = typedValue;
	}

	public PropertyDto() {
	}

    public TypedValueDto getTypedValue() {
		return typedValue;
	}
	public void setTypedValue(TypedValueDto value) {
		this.typedValue = value;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public PropertyTypeDto getType() {
		return type;
	}

	public void setType(PropertyTypeDto type) {
		this.type = type;
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
        PropertyDto other = (PropertyDto) obj;
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