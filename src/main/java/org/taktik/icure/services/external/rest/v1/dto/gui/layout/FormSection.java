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





import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aduchate on 07/02/13, 17:10
 */
public class FormSection implements Serializable {
    private String icon;

    private String title;

    private Integer columns;


    private List<FormColumn> formColumns = new ArrayList<FormColumn>();

	public FormSection() {
	}

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
