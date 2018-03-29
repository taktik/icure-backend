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

package org.taktik.icure.services.external.rest.propertyeditors;

import org.taktik.icure.services.external.rest.v1.dto.embed.FilterType;

import java.beans.PropertyEditorSupport;

public class FilterTypePropertyEditor extends PropertyEditorSupport {

	@Override
	public String getAsText() {
		String text;
		Object value = getValue();
		if (value != null && value instanceof FilterType) {
			text = ((FilterType) value).value();
		} else {
			text = "";
		}
		return text;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		super.setValue(FilterType.fromValue(text));
	}
}
