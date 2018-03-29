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

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.taktik.icure.entities.base.StoredDocument;

@Deprecated
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Filter extends StoredDocument {
	protected Integer checkMask; // A mask indicating the situations where one must automatically check the filter
	protected String data; // JSON description of the filter
	protected String filterEntity; // The entity the filter is applied on
	protected String name; // The name of the filter
	protected String userId; // The ID of the user this filter is linked to

	public Integer getCheckMask() {
		return checkMask;
	}

	public void setCheckMask(Integer checkMask) {
		this.checkMask = checkMask;
	}

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFilterEntity() {
        return filterEntity;
    }

    public void setFilterEntity(String filterEntity) {
        this.filterEntity = filterEntity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
