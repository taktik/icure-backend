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

import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.ClassificationTemplate;

import java.util.ArrayList;
import java.util.List;

public interface ClassificationTemplateDAO extends GenericDAO<ClassificationTemplate>  {
	ClassificationTemplate getClassificationTemplate(String classificationTemplateId);

    List<ClassificationTemplate> findByHCPartySecretPatientKeys(String hcPartyId, ArrayList<String> secretPatientKeys);

	PaginatedList<ClassificationTemplate> listClassificationTemplates(PaginationOffset paginationOffset);
}
