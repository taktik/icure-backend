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
