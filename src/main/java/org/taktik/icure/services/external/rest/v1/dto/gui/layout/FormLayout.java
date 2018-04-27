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

package org.taktik.icure.services.external.rest.v1.dto.gui.layout;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.taktik.icure.services.external.rest.v1.dto.gui.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("FormLayout")
public class FormLayout implements Serializable {
    @XStreamAsAttribute
    private String name;
    @XStreamAsAttribute
    private Double width;
    @XStreamAsAttribute
    private Double height;
    @XStreamAsAttribute
    private String descr;
    
    private Tag tag;
  
    @XStreamAsAttribute
    private String guid;
    @XStreamAsAttribute
    private String group;
    @XStreamImplicit(itemFieldName="FormSection")
    private List<FormSection> sections = new ArrayList<>();
    @XStreamImplicit(itemFieldName = "ImportedServiceXPath")
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