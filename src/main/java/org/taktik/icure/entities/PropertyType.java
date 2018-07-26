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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.constants.PropertyTypeScope;
import org.taktik.icure.constants.TypedValuesType;
import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.entities.base.StoredDocument;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyType extends StoredDocument implements Cloneable, Serializable, Identifiable<String> {
	private static final long serialVersionUID = 1L;

	protected String identifier;

	protected TypedValuesType type;

	private PropertyTypeScope scope;

	protected boolean unique;

	protected String editor;

	protected boolean localized;


	public PropertyType() {
	}

	public PropertyType(TypedValuesType type, String identifier) {
		this.type = type;
		this.identifier = identifier;
	}

	public PropertyType(TypedValuesType type, PropertyTypeScope scope, String identifier) {
		this.type = type;
		this.scope = scope;
		this.identifier = identifier;
	}

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

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PropertyType that = (PropertyType) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}