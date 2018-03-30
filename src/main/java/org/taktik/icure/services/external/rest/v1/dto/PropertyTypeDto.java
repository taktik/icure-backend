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

package org.taktik.icure.services.external.rest.v1.dto;

import io.swagger.annotations.ApiModelProperty;
import org.taktik.icure.constants.PropertyTypeScope;
import org.taktik.icure.constants.TypedValuesType;

import javax.persistence.Entity;

@Entity
public class PropertyTypeDto extends StoredDto implements Cloneable {
	private static final long serialVersionUID = 1L;

	protected String identifier;
    @ApiModelProperty(dataType = "string")
	protected TypedValuesType type;
	private PropertyTypeScope scope;
	protected boolean unique;
	protected String editor;
	protected boolean localized;
	protected LocalizedStringDto name;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String value) {
		this.identifier = value;
	}

	public TypedValuesType getType() {
		return type;
	}

	public void setType(TypedValuesType value) {
		this.type = value;
	}

	public PropertyTypeScope getScope() {
		return scope;
	}

	public void setScope(PropertyTypeScope scope) {
		this.scope = scope;
	}

	public boolean getUnique() {
		return unique;
	}

	public void setUnique(boolean value) {
		this.unique = value;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String value) {
		this.editor = value;
	}

	public boolean getLocalized() {
		return localized;
	}

	public void setLocalized(boolean value) {
		this.localized = value;
	}

	public LocalizedStringDto getName() {
		return name;
	}

	public void setName(LocalizedStringDto value) {
		this.name = value;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PropertyTypeDto that = (PropertyTypeDto) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}