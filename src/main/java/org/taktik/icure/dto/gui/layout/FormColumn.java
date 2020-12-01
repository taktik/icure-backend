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

package org.taktik.icure.dto.gui.layout;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aduchate on 07/02/13, 17:10
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormColumn implements Serializable {

    private List<FormLayoutData> formDataList = new ArrayList<FormLayoutData>();

    String columns;

    Boolean shouldDisplay;

    public String getColumns() {
        return columns;
    }

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
}
