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

package org.taktik.icure.services.external.rest.v1.dto.gui.editor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.taktik.icure.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.gui.Editor;

@JsonPolymorphismRoot(Editor.class)
@JsonDeserialize(using= JsonDeserializer.None.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckBoxEditor extends Editor implements ValueDateEditor {
    boolean displayValueDate;

    String groupRadio;

	public CheckBoxEditor() {
		super();
	}

	@Override
    public boolean getDisplayValueDate() {
        return displayValueDate;
    }

    @Override
    public void setDisplayValueDate(boolean displayValueDate) {
        this.displayValueDate = displayValueDate;
    }

    public String getGroupRadio() { return groupRadio; }

    public void setGroupRadio(String groupRadio) { this.groupRadio = groupRadio; }
}
