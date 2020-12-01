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

package org.taktik.icure.services.external.rest.v1.dto.gui;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.taktik.icure.handlers.JacksonEditorDeserializer;
import org.taktik.icure.handlers.JsonDiscriminator;
import org.taktik.icure.services.external.rest.v1.dto.gui.type.Data;

/**
 * Created by aduchate on 19/11/13, 15:28
 */
@JsonDiscriminator("key")
@JsonDeserialize(using = JacksonEditorDeserializer.class)
public abstract class Editor implements Serializable {
    private Double left;
    private Double top;
    private Double width;
    private Double height;

	boolean multiline;

	LabelPosition labelPosition;

	private boolean readOnly;

	private Data defaultValue;

	public Editor() {
	}

	public Double getLeft() {
        return left;
    }

    public void setLeft(Double left) {
        this.left = left;
    }

    public Double getTop() {
        return top;
    }

    public void setTop(Double top) {
        this.top = top;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public LabelPosition getLabelPosition() {
        return labelPosition;
    }

    public void setLabelPosition(LabelPosition labelPosition) {
        this.labelPosition = labelPosition;
    }

	public boolean isMultiline() {
		return multiline;
	}

	public void setMultiline(boolean multiline) {
		this.multiline = multiline;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

    public Data getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Data defaultValue) {
        this.defaultValue = defaultValue;
    }

    @JsonProperty("key")
    private String includeDiscriminator() {
	    return this.getClass().getSimpleName();
    }
}
