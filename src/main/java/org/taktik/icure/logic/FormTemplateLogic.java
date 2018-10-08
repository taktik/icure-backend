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

package org.taktik.icure.logic;

import org.taktik.icure.dto.gui.layout.FormLayout;
import org.taktik.icure.entities.FormTemplate;

import java.util.List;

public interface FormTemplateLogic extends EntityPersister<FormTemplate, String> {
    FormTemplate get(String formId);
    List<FormTemplate> getFormTemplatesBySpecialty(String specialityCode, boolean loadLayout);
    List<FormTemplate> getFormTemplatesByUser(String id, boolean loadLayout);
    FormTemplate modifyFormTemplate(FormTemplate formTemplate);

	List<FormTemplate> getFormTemplatesByGuid(String userId, String specialityCode, String formTemplateGuid);

	FormLayout extractLayout(FormTemplate formTemplate);
    FormLayout build(byte[] data);
	List<String> getFieldsNames(FormLayout formTemplateDto);
	FormTemplate createFormTemplate(FormTemplate entity);
	FormTemplate getFormTemplateById(String formTemplateId);
}
