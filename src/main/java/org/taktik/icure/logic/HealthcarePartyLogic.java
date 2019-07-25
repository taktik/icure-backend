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
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.exceptions.DocumentNotFoundException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.db.PaginationOffset;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Map;

public interface HealthcarePartyLogic extends EntityPersister<HealthcareParty, String> {

	HealthcareParty getHealthcareParty(String id);


	List<HealthcareParty> findHealthcareParties(String searchString, int offset, int limit);

    Map<String,String> getHcPartyKeysForDelegate(String healthcarePartyId);

	HealthcareParty modifyHealthcareParty(HealthcareParty healthcareParty) throws MissingRequirementsException;

	List<String> deleteHealthcareParties(List<String> healthcarePartyIds) throws DeletionException;

	HealthcareParty createHealthcareParty(HealthcareParty healthcareParty) throws MissingRequirementsException;

	Map<String,String[]> updateHcPartyKeys(String healthcarePartyId, Map<String, String[]> hcPartyKeys) throws Exception;

	PaginatedList<HealthcareParty> listHealthcareParties(PaginationOffset offset, Boolean desc);

    PaginatedList<HealthcareParty> findHealthcareParties(String fuzzyName, PaginationOffset offset, Boolean desc);

	List<HealthcareParty> listByNihii(String nihii);

	List<HealthcareParty> listBySsin(String ssin);

	List<HealthcareParty> listByName(String name);

	String getPublicKey(String healthcarePartyId) throws DocumentNotFoundException;

	PaginatedList<HealthcareParty> findHealthcareParties(String type, String spec, String firstCode, String lastCode);

	List<HealthcareParty> getHealthcareParties(List<String> ids);

	List<HealthcareParty> getHealthcarePartiesByParentId(String parentId);

	PaginatedList<HealthcareParty> findHealthcarePartiesBySsinOrNihii(String searchValue, PaginationOffset<String> paginationOffset, Boolean desc);

}
