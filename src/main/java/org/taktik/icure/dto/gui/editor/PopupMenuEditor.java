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

package org.taktik.icure.dto.gui.editor;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.taktik.icure.dto.gui.Editor;
import org.taktik.icure.dto.gui.type.MenuOption;

import java.util.List;

@XStreamAlias("PopupMenuEditor")
public class PopupMenuEditor extends Editor implements ValueDateEditor {

    @XStreamImplicit(itemFieldName = "menuOption")
    private List<String> menuOptions;

    @XStreamAsAttribute
    boolean displayValueDate;

    @XStreamAsAttribute
    boolean displayAllAlways;

    @XStreamAsAttribute
    boolean isFreeText;

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
