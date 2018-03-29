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

package org.taktik.icure.dto.gui;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.taktik.icure.dto.gui.editor.ActionButton;
import org.taktik.icure.dto.gui.editor.Audiometry;
import org.taktik.icure.dto.gui.editor.CheckBoxEditor;
import org.taktik.icure.dto.gui.editor.DashboardEditor;
import org.taktik.icure.dto.gui.editor.DateTimeEditor;
import org.taktik.icure.dto.gui.editor.HealthcarePartyEditor;
import org.taktik.icure.dto.gui.editor.IntegerSliderEditor;
import org.taktik.icure.dto.gui.editor.Label;
import org.taktik.icure.dto.gui.editor.MeasureEditor;
import org.taktik.icure.dto.gui.editor.MedicationEditor;
import org.taktik.icure.dto.gui.editor.MedicationTableEditor;
import org.taktik.icure.dto.gui.editor.NumberEditor;
import org.taktik.icure.dto.gui.editor.PopupMenuEditor;
import org.taktik.icure.dto.gui.editor.SchemaEditor;
import org.taktik.icure.dto.gui.editor.StringEditor;
import org.taktik.icure.dto.gui.editor.StringTableEditor;
import org.taktik.icure.dto.gui.editor.StyledStringEditor;
import org.taktik.icure.dto.gui.editor.SubFormEditor;
import org.taktik.icure.dto.gui.editor.TokenFieldEditor;
import org.taktik.icure.dto.gui.editor.TypeValueStringEditor;
import org.taktik.icure.dto.gui.type.Data;
import org.taktik.icure.services.external.rest.handlers.JsonDiscriminator;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismSupport;

/**
 * Created by aduchate on 19/11/13, 15:28
 */
@XStreamAlias("Editor")
@JsonPolymorphismSupport({
		ActionButton.class, StringEditor.class, CheckBoxEditor.class, DashboardEditor.class, DateTimeEditor.class,
		IntegerSliderEditor.class, MeasureEditor.class, MedicationEditor.class, MedicationTableEditor.class, NumberEditor.class,
        PopupMenuEditor.class, SchemaEditor.class, StringTableEditor.class, StyledStringEditor.class, SubFormEditor.class, StringTableEditor.class,
		TokenFieldEditor.class, TypeValueStringEditor.class, Label.class, StringTableEditor.class, HealthcarePartyEditor.class, Audiometry.class
})
@JsonDiscriminator("key")
public abstract class Editor implements Serializable {
    @XStreamAsAttribute
    private Double left;
    @XStreamAsAttribute
    private Double top;
    @XStreamAsAttribute
    private Double width;
    @XStreamAsAttribute
    private Double height;

	@XStreamAsAttribute
	boolean multiline;
    
    @XStreamAsAttribute
    LabelPosition labelPosition;

	@XStreamAsAttribute
	private boolean readOnly;

    Data defaultValue;

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

    public void setHeight(Double height) {
        this.height = height;
    }

    public Data getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Data defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Double getHeight() {
        return height;
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
}
