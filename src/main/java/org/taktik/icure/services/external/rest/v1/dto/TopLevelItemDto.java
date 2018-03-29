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

package org.taktik.icure.services.external.rest.v1.dto;

import org.taktik.icure.services.external.rest.v1.dto.data.DisplayableContent;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by aduchate on 22/04/13, 08:27
 */
public class TopLevelItemDto implements Serializable {
	String id;
	java.util.Map<String, String> descr;
	int position;

	DisplayableContent contextObject;
	DisplayableContent[] contextArray;
	boolean collection;
	String addButtonText;
	String formTemplateGuid;
	boolean display;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, String> getDescr() {
		return descr;
	}

	public void setDescr(Map<String, String> descr) {
		this.descr = descr;
	}

	public DisplayableContent getContextObject() {
		return contextObject;
	}

	public void setContextObject(DisplayableContent contextObject) {
		this.contextObject = contextObject;
	}

	public DisplayableContent[] getContextArray() {
		return contextArray;
	}

	public void setContextArray(DisplayableContent[] contextArray) {
		this.contextArray = contextArray;
	}

	public boolean isCollection() {
		return collection;
	}

	public void setCollection(boolean collection) {
		this.collection = collection;
	}

	public String getAddButtonText() {
		return addButtonText;
	}

	public void setAddButtonText(String addButtonText) {
		this.addButtonText = addButtonText;
	}

	public String getFormTemplateGuid() {
		return formTemplateGuid;
	}

	public void setFormTemplateGuid(String formTemplateGuid) {
		this.formTemplateGuid = formTemplateGuid;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
