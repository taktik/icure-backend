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

import java.util.ArrayList;
import java.util.List;


/**
 * Created by aduchate on 18/07/13, 13:06
 */

public class FormDto extends IcureDto {
	protected String descr;

    protected String formTemplateId;

    //Where in the hierarchy is the form attached
    protected String contactId; //The contact in which th eform has bean created
    protected String healthElementId;
    protected String planOfActionId;

    //if form is not filled in with contact data but with patient data. If this is null, it means that the list of services comes from all sub-contacts with this formId
    protected String dataJXPath;

    protected List<String> dashboardIds = new ArrayList<String>();

    protected String parent;
    protected List<FormDto> children = new ArrayList<FormDto>();

    protected Boolean hasBeenInitialized;

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getFormTemplateId() {
        return formTemplateId;
    }

    public void setFormTemplateId(String formTemplateId) {
        this.formTemplateId = formTemplateId;
    }

    public String getPlanOfActionId() {
        return planOfActionId;
    }

    public void setPlanOfActionId(String planOfActionId) {
        this.planOfActionId = planOfActionId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<FormDto> getChildren() {
    	if(children ==null) children = new ArrayList<FormDto>();
        return children;
    }

    public void setChildren(List<FormDto> children) {
        this.children = children;
    }

	public String getDataJXPath() {
		return dataJXPath;
	}

	public void setDataJXPath(String dataJXPath) {
		this.dataJXPath = dataJXPath;
	}

	public List<String> getDashboardIds() {
			return dashboardIds;
	}

	public void setDashboardIds(List<String> dashboardIds) {
		this.dashboardIds = dashboardIds;
	}

    public String getHealthElementId() {
        return healthElementId;
    }

    public void setHealthElementId(String healthElementId) {
        this.healthElementId = healthElementId;
    }

    public Boolean isHasBeenInitialized() {
        return hasBeenInitialized;
    }

    public void setHasBeenInitialized(Boolean hasBeenInitialized) {
        this.hasBeenInitialized = hasBeenInitialized;
    }

	private String encryptedSelf;
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}
}
