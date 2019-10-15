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
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.db.PaginationOffset;

import java.util.List;
import java.util.Map;

public interface HealthcarePartyDAO extends GenericDAO<HealthcareParty>{

	PaginatedList<HealthcareParty> findByHcPartyNameContainsFuzzy(String searchString, PaginationOffset offset, Boolean desc);

	List<HealthcareParty> findHealthcareParties(String searchString, int offset, int limit);

    /**
     * Returns a map indexed by owners of all HcPartyKeys encrypted using healthcarePartyId
     * private key.
     *
     * @param healthcarePartyId
     * @return the map of keys indexed by owner
     */
	Map<String,String> getHcPartyKeysForDelegate(String healthcarePartyId);

	List<HealthcareParty> findByNihii(String nihii);

	List<HealthcareParty> findBySsin(String ssin);

	PaginatedList<HealthcareParty> findBySpecialityPostcode(String type, String spec, String startCode, String endCode);

	PaginatedList<HealthcareParty> listHealthCareParties(PaginationOffset pagination, Boolean desc);

	List<HealthcareParty> findByName(String name);

	PaginatedList<HealthcareParty> findBySsinOrNihii(String searchValue, PaginationOffset<String> paginationOffset, Boolean desc);

	List<HealthcareParty> findByParentId(String parentId);
}
