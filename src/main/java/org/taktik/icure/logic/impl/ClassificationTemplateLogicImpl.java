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

package org.taktik.icure.logic.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.dao.ClassificationTemplateDAO;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.ClassificationTemplate;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.logic.ClassificationTemplateLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.validation.aspect.Check;

import javax.validation.constraints.NotNull;
import java.util.*;


/**
 * Created by dlm on 16-07-18
 */
@org.springframework.stereotype.Service
public class ClassificationTemplateLogicImpl extends GenericLogicImpl<ClassificationTemplate, ClassificationTemplateDAO> implements ClassificationTemplateLogic {
	private static final Logger log = LoggerFactory.getLogger(ClassificationTemplateLogicImpl.class);


	private ClassificationTemplateDAO classificationTemplateDAO;
	private UUIDGenerator uuidGenerator;
	private ICureSessionLogic sessionLogic;

	@Autowired
	public void setClassificationTemplateDAO(ClassificationTemplateDAO classificationTemplateDAO) {
		this.classificationTemplateDAO = classificationTemplateDAO;
	}
	@Autowired
	public void setUuidGenerator(UUIDGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}
	@Autowired
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}
	@Override
	protected ClassificationTemplateDAO getGenericDAO() {
		return classificationTemplateDAO;
	}

	@Override
	public ClassificationTemplate createClassificationTemplate(@Check @NotNull ClassificationTemplate classificationTemplate) {
		List<ClassificationTemplate> createdClassificationTemplates = new ArrayList<>(1);
		try {
			// Fetching the hcParty
			String healthcarePartyId = sessionLogic.getCurrentHealthcarePartyId();

			// Setting Classification Template attributes
			classificationTemplate.setId(uuidGenerator.newGUID().toString());
			classificationTemplate.setAuthor(healthcarePartyId);
			classificationTemplate.setResponsible(healthcarePartyId);

			createEntities(Collections.singleton(classificationTemplate), createdClassificationTemplates);
		} catch (Exception e) {
			log.error("createClassificationTemplate: " + e.getMessage());
			throw new IllegalArgumentException("Invalid Classification Template", e);
		}
		return createdClassificationTemplates.size() == 0 ? null:createdClassificationTemplates.get(0);
	}

	@Override
	public ClassificationTemplate getClassificationTemplate(String classificationTemplateId) {
		return classificationTemplateDAO.getClassificationTemplate(classificationTemplateId);
	}


	@Override
	public Set<String> deleteClassificationTemplates(Set<String> ids) {
		try {
			deleteEntities(ids);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
		return ids;
	}

	@Override
	public ClassificationTemplate modifyClassificationTemplate(@Check @NotNull ClassificationTemplate classificationTemplate) {
		try {
			ClassificationTemplate toEdit = getClassificationTemplate(classificationTemplate.getId());
			toEdit.setLabel(classificationTemplate.getLabel());
			updateEntities(Collections.singleton(toEdit));
			return getClassificationTemplate(classificationTemplate.getId());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid Classification Template", e);
		}
	}

	@Override
	public ClassificationTemplate addDelegation(String classificationTemplateId, String healthcarePartyId, Delegation delegation) {
		ClassificationTemplate classificationTemplate = getClassificationTemplate(classificationTemplateId);
		classificationTemplate.addDelegation(healthcarePartyId, delegation);

		return classificationTemplateDAO.save(classificationTemplate);
	}

	@Override
	public ClassificationTemplate addDelegations(String classificationTemplateId, List<Delegation> delegations) {
		ClassificationTemplate classificationTemplate = getClassificationTemplate(classificationTemplateId);
		delegations.forEach(d->classificationTemplate.addDelegation(d.getDelegatedTo(),d));

		return classificationTemplateDAO.save(classificationTemplate);

	}

    @Override
    public List<ClassificationTemplate> getClassificationTemplateByIds(List<String> ids) {
        return classificationTemplateDAO.getList(ids);
    }

    @Override
    public List<ClassificationTemplate> findByHCPartySecretPatientKeys(String hcPartyId, ArrayList<String> secretPatientKeys) {
        return classificationTemplateDAO.findByHCPartySecretPatientKeys(hcPartyId, secretPatientKeys);
    }

	@Override
	public PaginatedList<ClassificationTemplate> listClassificationTemplates(PaginationOffset paginationOffset) {
		return classificationTemplateDAO.listClassificationTemplates(paginationOffset);
	}

}
