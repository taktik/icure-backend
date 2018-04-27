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

import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public class LocaleDto extends StoredDto implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	protected String identifier;
	protected String icon;
	protected LocalizedStringDto name;

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

		LocaleDto locale = (LocaleDto) o;

		if (id != null ? !id.equals(locale.id) : locale.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}