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

import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.ClassificationTemplate;
import org.taktik.icure.entities.User;
import org.taktik.icure.entities.embed.Delegation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by dlm on 16-07-18
 */
public interface ClassificationTemplateLogic extends EntityPersister<ClassificationTemplate, String> {

	ClassificationTemplate createClassificationTemplate(ClassificationTemplate classificationTemplate);

	ClassificationTemplate getClassificationTemplate(String classificationTemplateId);

	Set<String> deleteClassificationTemplates(Set<String> ids);

	ClassificationTemplate modifyClassificationTemplate(ClassificationTemplate classificationTemplate);

	ClassificationTemplate addDelegation(String classificationTemplateId, String healthcarePartyId, Delegation delegation);

	ClassificationTemplate addDelegations(String classificationTemplateId, List<Delegation> delegations);

    List<ClassificationTemplate> getClassificationTemplateByIds(List<String> asList);

    List<ClassificationTemplate> findByHCPartySecretPatientKeys(String hcPartyId, ArrayList<String> strings);

	PaginatedList<ClassificationTemplate> listClassificationTemplates(PaginationOffset paginationOffset);
}
