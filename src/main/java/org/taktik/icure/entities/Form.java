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

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.StoredICureDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aduchate on 18/07/13, 13:06
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Form extends StoredICureDocument {
	protected String descr;

    protected String formTemplateId;

    //Where in the hierarchy is the form attached
    protected String contactId;
    protected String healthElementId;
    protected String planOfActionId;

    //if form is not filled in with contact data but with patient data. If this is null, it means that the list of services comes from all sub-contacts with this formId
    protected String dataJXPath;

    protected List<String> dashboardIds = new ArrayList<String>();

    protected String parent;

    @JsonIgnore
    protected java.util.List<Form> children = new java.util.ArrayList<Form>();

    protected Boolean hasBeenInitialized;

	public Form solveConflictWith(Form other) {
		super.solveConflictsWith(other);

		this.descr = this.descr == null ? other.descr : this.descr;
		this.formTemplateId = this.formTemplateId == null ? other.formTemplateId : this.formTemplateId;
		this.contactId = this.contactId == null ? other.contactId : this.contactId;
		this.planOfActionId = this.planOfActionId == null ? other.planOfActionId : this.planOfActionId;
		this.healthElementId = this.healthElementId == null ? other.healthElementId : this.healthElementId;

		this.hasBeenInitialized = this.hasBeenInitialized == null ? other.hasBeenInitialized : other.hasBeenInitialized == null ? this.hasBeenInitialized : Boolean.valueOf(this.hasBeenInitialized || other.hasBeenInitialized);

		return this;
	}

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

    @JsonIgnore
    public List<Form> getChildren() {
    	if(children ==null) children = new ArrayList<>();
        return children;
    }

    @JsonIgnore
    public void setChildren(List<Form> children) {
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
	@Override
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	@Override
	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}
}
