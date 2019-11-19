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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aduchate on 07/02/13, 17:10
 */
@XStreamAlias("FormColumn")
public class FormColumn implements Serializable {
    @XStreamImplicit(itemFieldName="FormLayoutData")
    private List<FormLayoutData> formDataList = new ArrayList<FormLayoutData>();

    @XStreamAsAttribute
    String columns;

    @XStreamAsAttribute
    Boolean shouldDisplay;


	public FormColumn() {
	}

	/**
     * Determines the columns span of the object
     *
     * @param columns: 1=column 1, 1-2=column 1 and 2. Null means all columns.
     */
    public void setColumns(String columns) {
        this.columns = columns;
    }

    public List<FormLayoutData> getFormDataList() {
        return formDataList;
    }

    public void setFormDataList(List<FormLayoutData> formDataList) {
        this.formDataList = formDataList;
    }

    public void addFormData(FormLayoutData fd) {
        formDataList.add(fd);
    }

    public Boolean getShouldDisplay() {
        return shouldDisplay;
    }

    public void setShouldDisplay(Boolean shouldDisplay) {
        this.shouldDisplay = shouldDisplay;
    }

    public String getColumns() {
        return columns;
    }

}
