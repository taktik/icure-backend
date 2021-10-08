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

package org.taktik.icure.services.external.rest.v2.dto.gui.layout;




import org.taktik.icure.services.external.rest.v2.dto.gui.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FormLayout implements Serializable {
    private String name;
    private Double width;
    private Double height;
    private String descr;

    private Tag tag;

    private String guid;
    private String group;
    private List<FormSection> sections = new ArrayList<>();
    List<String> importedServiceXPaths;

	public FormLayout() {
	}

	public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getImportedServiceXPaths() {
        return importedServiceXPaths;
    }

    public void setImportedServiceXPaths(List<String> importedServiceXPaths) {
        this.importedServiceXPaths = importedServiceXPaths;
    }

	public List<FormSection> getSections() {
		return sections;
	}

	public void setSections(List<FormSection> sections) {
		this.sections = sections;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
}
