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

package org.taktik.icure.services.external.rest.v1.dto.gui.editor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;



import org.taktik.icure.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.gui.Editor;

import java.util.List;

@JsonPolymorphismRoot(Editor.class)
@JsonDeserialize(using= JsonDeserializer.None.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PopupMenuEditor extends Editor implements ValueDateEditor {


    private List<String> menuOptions;

    boolean displayValueDate;

    boolean displayAllAlways;

    boolean isFreeText;

	public PopupMenuEditor() {
		super();
	}

	@Override
    public boolean getDisplayValueDate() {
        return displayValueDate;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setDisplayValueDate(boolean displayValueDate) {
        this.displayValueDate = displayValueDate;
    }

    public boolean getDisplayAllAlways() {
        return displayAllAlways;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setDisplayAllAlways(boolean displayAllAlways) {
        this.displayAllAlways = displayAllAlways;
    }

    public boolean getIsFreeText() {
        return isFreeText;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setIsFreeText(boolean isFreeText) {
        this.isFreeText = isFreeText;
    }


    public List<String> getMenuOptions() {
        return menuOptions;
    }

    public void setMenuOptions(List<String> menuOptions) {
        this.menuOptions = menuOptions;
    }
}
