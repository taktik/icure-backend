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

package org.taktik.icure.services.external.rest.v1.dto.gui.editor;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.gui.Editor;
import org.taktik.icure.services.external.rest.v1.dto.gui.layout.FormLayoutData;

/**
 * Created by aduchate on 03/12/13, 22:23
 */
@XStreamAlias("Label")
public class Label extends Editor {
	
	@XStreamAsAttribute
	FormLayoutData formData;

	public Label() {
		super();
	}

	public FormLayoutData getFormData() {
		return formData;
	}

	public void setFormData(FormLayoutData formData) {
		this.formData = formData;
	}
}
