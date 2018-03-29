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

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.entities.base.StoredDocument;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocalizedString extends StoredDocument implements Cloneable, Serializable, Comparable<LocalizedString> {
	private static final long serialVersionUID = 1L;
	protected Map<String, String> values = new HashMap<>();

    protected String identifier;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Map<String, String> getValues() {
		return values;
	}
	public void setValues(Map<String, String> values) {
		this.values = values;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LocalizedString that = (LocalizedString) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (identifier != null ? !identifier.equals(that.identifier) : that.identifier != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : identifier != null ? identifier.hashCode() : 0;
	}

	@Override
	public int compareTo(LocalizedString o) {
		return identifier != null ? identifier.compareTo(o.getIdentifier()):
                id != null ? id.compareTo(o.getId()) : 0;
	}

    public String getString(String locale) {
        return values.get(locale);
    }
}