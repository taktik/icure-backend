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

package org.taktik.icure.services.external.rest.v1.dto;

import org.taktik.icure.services.external.rest.v1.dto.FormTemplateDto;
import org.taktik.icure.services.external.rest.v1.dto.data.FormContent;

import java.io.Serializable;
import java.util.List;

public class GetFormLayoutAndContentReply implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	FormContent formContent;
	List<FormTemplateDto> templates;
	public GetFormLayoutAndContentReply(FormContent formContent,
			List<FormTemplateDto> templates) {
		super();
		this.formContent = formContent;
		this.templates = templates;
	}
	
	public GetFormLayoutAndContentReply(){}
	
	
	public FormContent getFormContent() {
		return formContent;
	}
	public void setFormContent(FormContent formContent) {
		this.formContent = formContent;
	}

	public List<FormTemplateDto> getTemplates() {
		return templates;
	}

	public void setTemplates(List<FormTemplateDto> templates) {
		this.templates = templates;
	}

	
}
