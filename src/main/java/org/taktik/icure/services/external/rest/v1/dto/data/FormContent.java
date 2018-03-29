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

package org.taktik.icure.services.external.rest.v1.dto.data;

import org.taktik.icure.services.external.rest.v1.dto.CodeDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by aduchate on 01/02/13, 12:20
 */
public class FormContent extends FormItem implements DisplayableContent {
    protected String formTemplateGuid; //to query formLayout
	protected String dashboardGuid; //to query dashboard

	protected String entityId; //A patient id or a form id
	protected String entityClass; //Patient.class.canonicalName or Form.class.canonicalName
	protected String dataJXPath; 
	protected String descr;
	protected String id;
	protected boolean allowMultiple;//Allow multiple instances (ex for collection) will trigger +and - buttons to appear
	protected boolean deleted;

	protected List<FormItem> items = new ArrayList<FormItem>();

	public FormContent() {
	}

	public FormContent(String descr,String guid, CodeDto... tags) {
		
		this.descr = descr;
		this.guid =guid;
		this.tags = Arrays.asList(tags);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<FormItem> getItems() {
		if(items==null) items= new ArrayList<FormItem>();
		return items;
	}

	public void setItems(List<FormItem> items) {
		this.items = items;
	}
	
	public void addFormItem(FormItem item){
		if(items==null) items= new ArrayList<FormItem>();
		items.add(item);
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}


	public String getDataJXPath() {
		return dataJXPath;
	}

	public void setDataJXPath(String dataJXPath) {
		this.dataJXPath = dataJXPath;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public boolean isAllowMultiple() {
		return allowMultiple;
	}

	public void setAllowMultiple(boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
	}

	public String getFormTemplateGuid() {
		return formTemplateGuid;
	}

	public void setFormTemplateGuid(String formTemplateGuid) {
		this.formTemplateGuid = formTemplateGuid;
	}

	public String getDashboardGuid() {
		return dashboardGuid;
	}

	public void setDashboardGuid(String dashboardGuid) {
		this.dashboardGuid = dashboardGuid;
	}


}
