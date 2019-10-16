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

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.dao.HealthcarePartyDAO;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.validation.aspect.Check;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class HealthcarePartyLogicImpl extends GenericLogicImpl<HealthcareParty, HealthcarePartyDAO>  implements HealthcarePartyLogic {
	private static final Logger log = LoggerFactory.getLogger(HealthcarePartyLogicImpl.class);

	private HealthcarePartyDAO healthcarePartyDAO;
	private UUIDGenerator uuidGenerator;

	@Autowired
	public void setUuidGenerator(UUIDGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}

	@Override
	protected HealthcarePartyDAO getGenericDAO() {
		return healthcarePartyDAO;
	}

	@Autowired
	public void setHealthcarePartyDAO(HealthcarePartyDAO healthcarePartyDAO) {
		this.healthcarePartyDAO = healthcarePartyDAO;
	}

	@Override
	public HealthcareParty 	getHealthcareParty(String id) {
		return healthcarePartyDAO.get(id);
	}

	@Override
	public List<HealthcareParty> findHealthcareParties(String searchString, int offset, int limit) {
		return healthcarePartyDAO.findHealthcareParties(searchString, offset, limit);
	}

    @Override
    public Map<String, String> getHcPartyKeysForDelegate(String healthcarePartyId) {
        return healthcarePartyDAO.getHcPartyKeysForDelegate(healthcarePartyId);
    }

	@Override
	public HealthcareParty modifyHealthcareParty(@Check @NotNull HealthcareParty healthcareParty) throws MissingRequirementsException {
		// checking requirements
		if (healthcareParty.getNihii() == null && healthcareParty.getSsin() == null && healthcareParty.getName() == null && healthcareParty.getLastName() == null) {
			throw new MissingRequirementsException("modifyHealthcareParty: one of Name or Last name, Nihii or  Ssin are required.");
		}

		try {
			updateEntities(Collections.singleton(healthcareParty));
            return getHealthcareParty(healthcareParty.getId());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid healthcare party", e);
		}
	}

	@Override
	public List<String> deleteHealthcareParties(List<String> healthcarePartyIds) throws DeletionException {
		try {
			deleteEntities(healthcarePartyIds);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DeletionException("The healthcare party (" + healthcarePartyIds + ") not found or " + e.getMessage() , e);
		}
		return healthcarePartyIds;
	}

	@Override
	public HealthcareParty createHealthcareParty(@Check @NotNull HealthcareParty healthcareParty) throws MissingRequirementsException {
		// checking requirements
		if (healthcareParty.getNihii() == null && healthcareParty.getSsin() == null && healthcareParty.getName() == null && healthcareParty.getLastName() == null) {
			throw new MissingRequirementsException("createHealthcareParty: one of Name or Last name, Nihii, and Public key are required.");
		}

		List<HealthcareParty> createdHealthcareParties = new ArrayList<>(1);
		try {
			if (healthcareParty.getId()==null) {
				String newId = uuidGenerator.newGUID().toString();
				healthcareParty.setId(newId);
			}

			createEntities(Collections.singleton(healthcareParty), createdHealthcareParties);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid healthcare party", e);
		}
		return createdHealthcareParties.size() == 0 ? null:createdHealthcareParties.get(0);
	}

	@Override
	public Map<String, String[]> updateHcPartyKeys(String healthcarePartyId, Map<String, String[]> newHcPartyKeys) throws Exception {
		Preconditions.checkArgument(healthcarePartyId != null);
		Preconditions.checkArgument(newHcPartyKeys.size() != 0);

		// Fetching existing HcPartyKeys
		HealthcareParty healthcareParty = getHealthcareParty(healthcarePartyId);
		Map<String, String[]> existingHcPartyKeys = healthcareParty.getHcPartyKeys();

		// Updating with new HcPartyKeys
		existingHcPartyKeys.putAll(newHcPartyKeys);
		healthcareParty.setHcPartyKeys(existingHcPartyKeys);
		updateEntities(Collections.singleton(healthcareParty));

		return existingHcPartyKeys;
	}

	@Override
	public PaginatedList<HealthcareParty> listHealthcareParties(PaginationOffset offset, Boolean desc) {
		PaginatedList<HealthcareParty> healthcareParties;
		healthcareParties = healthcarePartyDAO.listHealthCareParties(offset,desc);
		return healthcareParties;
	}

    @Override
    public PaginatedList<HealthcareParty> findHealthcareParties(String fuzzyName, PaginationOffset offset, Boolean desc) {
        PaginatedList<HealthcareParty> healthcareParties;
        healthcareParties = healthcarePartyDAO.findByHcPartyNameContainsFuzzy(fuzzyName, offset, desc);
        return healthcareParties;
    }

	@Override
	public List<HealthcareParty> listByNihii(String nihii) {
		return healthcarePartyDAO.findByNihii(nihii);
	}

	@Override
	public List<HealthcareParty> listBySsin(String ssin) {
		return healthcarePartyDAO.findBySsin(ssin);
	}

	@Override
	public List<HealthcareParty> listByName(String name) { return healthcarePartyDAO.findByName(name); }

	@Override
	public String getPublicKey(String healthcarePartyId) throws org.taktik.icure.exceptions.DocumentNotFoundException {
		HealthcareParty hcParty = healthcarePartyDAO.get(healthcarePartyId);

		if (hcParty==null) {
			throw new org.taktik.icure.exceptions.DocumentNotFoundException("Healthcare party (" + healthcarePartyId + ") not found in the database.");
		}

		return hcParty.getPublicKey();
	}

	@Override
	public PaginatedList<HealthcareParty> findHealthcareParties(String type, String spec, String firstCode, String lastCode) {
		return healthcarePartyDAO.findBySpecialityPostcode(type, spec, firstCode, lastCode);
	}

	@Override
	public List<HealthcareParty> getHealthcareParties(List<String> ids) {
		return healthcarePartyDAO.getList(ids);
	}

	@Override
	public PaginatedList<HealthcareParty> findHealthcarePartiesBySsinOrNihii(String searchValue, PaginationOffset<String> paginationOffset, Boolean desc) {
		return healthcarePartyDAO.findBySsinOrNihii(searchValue, paginationOffset, desc);
	}

	@Override
	public  List<HealthcareParty> getHealthcarePartiesByParentId(String parentId){ return healthcarePartyDAO.findByParentId(parentId); }

}
