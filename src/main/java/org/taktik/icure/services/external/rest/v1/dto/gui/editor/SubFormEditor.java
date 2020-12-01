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

import java.util.List;

/**
 * Created by aduchate on 03/12/13, 17:42
 */
@JsonPolymorphismRoot(Editor.class)
@JsonDeserialize(using= JsonDeserializer.None.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubFormEditor extends Editor {

    List<String> optionalFormGuids;

    List<String> compulsoryFormGuids;
    Boolean growsHorizontally;
    Boolean collapsed;

    Boolean showHeader=true;

	public SubFormEditor() {
		super();
	}

	public Boolean getGrowsHorizontally() {
        return growsHorizontally;
    }

    public void setGrowsHorizontally(Boolean growsHorizontally) {
        this.growsHorizontally = growsHorizontally;
    }

    public List<String> getOptionalFormGuids() {
        return optionalFormGuids;
    }

    public void setOptionalFormGuids(List<String> optionalFormGuids) {
        this.optionalFormGuids = optionalFormGuids;
    }

    public List<String> getCompulsoryFormGuids() {
        return compulsoryFormGuids;
    }

    public void setCompulsoryFormGuids(List<String> compulsoryFormGuids) {
        this.compulsoryFormGuids = compulsoryFormGuids;
    }

	public Boolean getShowHeader() {
		return showHeader;
	}

	public void setShowHeader(Boolean showHeader) {
		this.showHeader = showHeader;
	}

	public Boolean getCollapsed() {
		return collapsed;
	}

	public void setCollapsed(Boolean collapsed) {
		this.collapsed = collapsed;
	}
}
