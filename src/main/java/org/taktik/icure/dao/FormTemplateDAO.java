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

package org.taktik.icure.dao;

import org.ektorp.support.View;
import org.taktik.icure.entities.FormTemplate;

import java.util.List;

public interface FormTemplateDAO  extends GenericDAO<FormTemplate> {
	FormTemplate createFormTemplate(FormTemplate entity);
	List<FormTemplate> findByUserGuid(String userId, String guid, boolean loadLayout);

	@View(name = "by_guid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.FormTemplate' && !doc.deleted) emit(doc.guid, null )}")
	List<FormTemplate> findByGuid(String guid, boolean loadLayout);

	List<FormTemplate> findBySpecialtyGuid(String specialityCode, String guid, boolean loadLayout);
}
