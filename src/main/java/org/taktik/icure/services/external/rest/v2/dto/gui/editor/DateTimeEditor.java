/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v2.dto.gui.editor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.taktik.icure.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v2.dto.gui.Editor;

/**
 * Created by aduchate on 19/11/13, 15:28
 */
@JsonPolymorphismRoot(Editor.class)
@JsonDeserialize(using= JsonDeserializer.None.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DateTimeEditor extends Editor {
	boolean displayTime;
	boolean showPicker;
	boolean webAgenda;
	boolean fullDateMode;

	public DateTimeEditor() {
		super();
	}

	public boolean isDisplayTime() {
		return displayTime;
	}

	public void setDisplayTime(boolean displayTime) {
		this.displayTime = displayTime;
	}

	public boolean isShowPicker() {
		return showPicker;
	}

	public void setShowPicker(boolean showPicker) {
		this.showPicker = showPicker;
	}

	public boolean isWebAgenda() {
		return webAgenda;
	}

	public void setWebAgenda(boolean webAgenda) {
		this.webAgenda = webAgenda;
	}

	public boolean isFullDateMode() { return fullDateMode; }

	public void setFullDateMode(boolean fullDateMode) {
		this.fullDateMode = fullDateMode;
	}
}
