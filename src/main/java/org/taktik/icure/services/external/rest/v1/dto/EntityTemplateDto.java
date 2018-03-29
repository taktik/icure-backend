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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class EntityTemplateDto extends StoredDto implements Serializable {
	String userId;
	String descr;

	String entityType;
	Boolean defaultTemplate;
	List<Map<String,Object>> entity;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public List<Map<String, Object>> getEntity() {
		return entity;
	}

	public void setEntity(List<Map<String, Object>> entity) {
		this.entity = entity;
	}

	public Boolean isDefaultTemplate() {
		return defaultTemplate;
	}

	public void setDefaultTemplate(Boolean defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}
}
