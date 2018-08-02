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
import org.taktik.icure.dao.HealthElementDAO;
import org.taktik.icure.dao.Option;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.entities.HealthElement;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.logic.HealthElementLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.utils.FuzzyValues;
import org.taktik.icure.validation.aspect.Check;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;


/**
 * Created by emad7105 on 24/06/2014.
 */
@org.springframework.stereotype.Service
public class HealthElementLogicImpl extends GenericLogicImpl<HealthElement, HealthElementDAO> implements HealthElementLogic {
	private static final Logger log = LoggerFactory.getLogger(HealthElementLogicImpl.class);


	private HealthElementDAO healthElementDAO;
	private UUIDGenerator uuidGenerator;
	private ICureSessionLogic sessionLogic;

	@Autowired
	public void setHealthElementDAO(HealthElementDAO healthElementDAO) {
		this.healthElementDAO = healthElementDAO;
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
	protected HealthElementDAO getGenericDAO() {
		return healthElementDAO;
	}

	@Override
	public HealthElement createHealthElement(@Check @NotNull HealthElement healthElement) {
		List<HealthElement> createdHealthElements = new ArrayList<>(1);
		try {
			// Fetching the hcParty
			String healthcarePartyId = sessionLogic.getCurrentHealthcarePartyId();

			// Setting Healthcare problem attributes
			healthElement.setId(uuidGenerator.newGUID().toString());
			if (healthElement.getOpeningDate() == null) { healthElement.setOpeningDate(FuzzyValues.getFuzzyDateTime(LocalDateTime.now(), ChronoUnit.SECONDS)); }
			healthElement.setAuthor(healthcarePartyId);
			healthElement.setResponsible(healthcarePartyId);

			// TODO should we check that opening contacts or closing contacts are valid?

			createEntities(Collections.singleton(healthElement), createdHealthElements);
		} catch (Exception e) {
			log.error("createHealthElement: " + e.getMessage());
			throw new IllegalArgumentException("Invalid Healthcare problem", e);
		}
		return createdHealthElements.size() == 0 ? null:createdHealthElements.get(0);
	}

	@Override
	public HealthElement getHealthElement(String healthElementId) {
		return healthElementDAO.getHealthElement(healthElementId);
	}

	@Override
	public List<HealthElement> getHealthElements(List<String> healthElementIds) {
		return healthElementDAO.getList(healthElementIds);
	}

	@Override
	public List<HealthElement> findByHCPartySecretPatientKeys(String hcPartyId, List<String> secretPatientKeys) {
		return healthElementDAO.findByHCPartySecretPatientKeys(hcPartyId, secretPatientKeys);
	}

	@Override
	public Set<String> deleteHealthElements(Set<String> ids) {
		try {
			deleteEntities(ids);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
		return ids;
	}

	@Override
	public HealthElement modifyHealthElement(@Check @NotNull HealthElement healthElement) {
		try {
			updateEntities(Collections.singleton(healthElement));
            return getHealthElement(healthElement.getId());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid Health problem", e);
		}
	}

	@Override
	public HealthElement addDelegation(String healthElementId, String healthcarePartyId, Delegation delegation) {
		HealthElement healthElement = getHealthElement(healthElementId);
		healthElement.addDelegation(healthcarePartyId, delegation);

		return healthElementDAO.save(healthElement);
	}

	@Override
	public HealthElement addDelegations(String healthElementId, List<Delegation> delegations) {
		HealthElement healthElement = getHealthElement(healthElementId);
		delegations.forEach(d->healthElement.addDelegation(d.getDelegatedTo(),d));

		return healthElementDAO.save(healthElement);

	}

	@Override
	public void solveConflicts() {
		List<HealthElement> healthElementsInConflict = healthElementDAO.listConflicts().stream().map(it -> healthElementDAO.get(it.getId(), Option.CONFLICTS)).collect(Collectors.toList());
		healthElementsInConflict.forEach(p-> {
			Arrays.stream(p.getConflicts()).map(c->healthElementDAO.get(p.getId(),c)).forEach(cp -> {
				p.solveConflictWith(cp);
				healthElementDAO.purge(cp);
			});
			healthElementDAO.save(p);
		});

	}


}
