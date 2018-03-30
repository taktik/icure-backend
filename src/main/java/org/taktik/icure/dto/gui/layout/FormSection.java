/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.dto.gui.layout;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aduchate on 07/02/13, 17:10
 */
@XStreamAlias("FormSection")
public class FormSection implements Serializable {
    @XStreamAsAttribute
    private Integer columns;

    @XStreamImplicit(itemFieldName="FormColumn")
    private List<FormColumn> formColumns = new ArrayList<FormColumn>();

	public FormSection() {
	}

	public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

	public List<FormColumn> getFormColumns() {
		return formColumns;
	}

	public void setFormColumns(List<FormColumn> formColumns) {
		this.formColumns = formColumns;
	}
}
