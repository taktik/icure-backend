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
import org.taktik.icure.dao.ClassificationDAO;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.entities.Classification;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.logic.ClassificationLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.validation.aspect.Check;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * Created by dlm on 16-07-18
 */
@org.springframework.stereotype.Service
public class ClassificationLogicImpl extends GenericLogicImpl<Classification, ClassificationDAO> implements ClassificationLogic {
	private static final Logger log = LoggerFactory.getLogger(ClassificationLogicImpl.class);


	private ClassificationDAO classificationDAO;
	private UUIDGenerator uuidGenerator;
	private ICureSessionLogic sessionLogic;

	@Autowired
	public void setClassificationDAO(ClassificationDAO classificationDAO) {
		this.classificationDAO = classificationDAO;
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
	protected ClassificationDAO getGenericDAO() {
		return classificationDAO;
	}

	@Override
	public Classification createClassification(@Check @NotNull Classification classification) {
		List<Classification> createdClassifications = new ArrayList<>(1);
		try {
			// Fetching the hcParty
			String healthcarePartyId = sessionLogic.getCurrentHealthcarePartyId();

			// Setting Classification attributes
			classification.setId(uuidGenerator.newGUID().toString());
			classification.setAuthor(healthcarePartyId);
			classification.setResponsible(healthcarePartyId);

			createEntities(Collections.singleton(classification), createdClassifications);
		} catch (Exception e) {
			log.error("createClassification: " + e.getMessage());
			throw new IllegalArgumentException("Invalid Classification", e);
		}
		return createdClassifications.size() == 0 ? null:createdClassifications.get(0);
	}

	@Override
	public Classification getClassification(String classificationId) {
		return classificationDAO.getClassification(classificationId);
	}

	@Override
	public List<Classification> findByHCPartySecretPatientKeys(String hcPartyId, List<String> secretPatientKeys) {
		return classificationDAO.findByHCPartySecretPatientKeys(hcPartyId, secretPatientKeys);
	}

	@Override
	public Set<String> deleteClassifications(Set<String> ids) {
		try {
			deleteEntities(ids);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
		return ids;
	}

	@Override
	public Classification modifyClassification(@Check @NotNull Classification classification) {
		try {
			Classification toEdit = getClassification(classification.getId());
			toEdit.setLabel(classification.getLabel());
			updateEntities(Collections.singleton(toEdit));
            return getClassification(classification.getId());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid Classification", e);
		}
	}

	@Override
	public Classification addDelegation(String classificationId, String healthcarePartyId, Delegation delegation) {
		Classification classification = getClassification(classificationId);
		classification.addDelegation(healthcarePartyId, delegation);

		return classificationDAO.save(classification);
	}

	@Override
	public Classification addDelegations(String classificationId, List<Delegation> delegations) {
		Classification classification = getClassification(classificationId);
		delegations.forEach(d->classification.addDelegation(d.getDelegatedTo(),d));

		return classificationDAO.save(classification);

	}

    @Override
    public List<Classification> getClassificationByIds(List<String> ids) {
        return classificationDAO.getList(ids);
    }

}
