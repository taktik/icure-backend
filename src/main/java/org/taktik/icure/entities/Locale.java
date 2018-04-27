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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.taktik.icure.entities.base.StoredDocument;

import javax.persistence.Entity;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Locale extends StoredDocument implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	protected String identifier;
	protected String icon;
	protected LocalizedString name;

	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String value) {
		this.identifier = value;
	}

	public String getIcon() {
		return icon;
	}
	public void setIcon(String value) {
		this.icon = value;
	}

	public LocalizedString getName() {
		return name;
	}
	public void setName(LocalizedString value) {
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

		Locale locale = (Locale) o;

		if (id != null ? !id.equals(locale.id) : locale.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}