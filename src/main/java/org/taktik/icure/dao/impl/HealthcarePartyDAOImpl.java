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

package org.taktik.icure.dao.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.ektorp.ComplexKey;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.HealthcarePartyDAO;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.db.StringUtils;
import org.taktik.icure.entities.HealthcareParty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created by aduchate on 18/07/13, 13:36 */
@Repository("healthcarePartyDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) emit( doc.lastName, doc._id )}")
class HealthcarePartyDAOImpl extends CachedDAOImpl<HealthcareParty> implements HealthcarePartyDAO {
	@Autowired
    public HealthcarePartyDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbBase") CouchDbICureConnector couchdb, IDGenerator idGenerator, @Qualifier("entitiesCacheManager") CacheManager cacheManager) {
        super(HealthcareParty.class, couchdb, idGenerator, cacheManager);
        initStandardDesignDocument();
    }

	@Override
	@View(name = "by_nihii", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) emit(doc.nihii.substr(0,8), doc._id )}")
	public List<HealthcareParty> findByNihii(String nihii) {
		if (nihii == null) { return new ArrayList<>(); }
		return queryView("by_nihii", nihii.length()>8 ? nihii.substring(0,8) : nihii);
    }

	@Override
	@View(name = "by_ssin", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) emit(doc.ssin, doc._id )}")
	public List<HealthcareParty> findBySsin(String ssin) {
		return queryView("by_ssin", ssin);
	}

	@Override
	@View(name = "by_speciality_postcode", map = "classpath:js/healthcareparty/By_speciality_postcode.js")
	public PaginatedList<HealthcareParty> findBySpecialityPostcode(String type, String spec, String firstCode, String lastCode) {
		return pagedQueryView("by_speciality_postcode", ComplexKey.of(type, spec, firstCode), ComplexKey.of(type, spec, lastCode), new PaginationOffset(10000), false);
	}


	@Override
	@View(name = "allForPagination", map = "classpath:js/healthcareparty/All_for_pagination.js")
	public PaginatedList<HealthcareParty> listHealthCareParties(PaginationOffset pagination, Boolean desc) {
		return pagedQueryView("allForPagination", pagination.getStartKey() != null ? pagination.getStartKey().toString() : (desc != null && desc ? "\ufff0" : "\u0000"), (desc != null && desc ? "\u0000" : "\ufff0"), pagination, desc==null?false:desc);
	}

	@Override
	@View(name = "by_name", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) emit(doc.name, doc._id )}")
	public List<HealthcareParty> findByName(String name) {
		return queryView("by_name", name);
	}

	@Override
	@View(name = "by_ssin_or_nihii", map = "classpath:js/healthcareparty/By_Ssin_or_Nihii.js")
	public PaginatedList<HealthcareParty> findBySsinOrNihii(String searchValue, PaginationOffset<String> offset, Boolean desc) {
		boolean isDesc = desc != null && desc;
		String from = (offset.getStartKey() == null) ? (isDesc ? searchValue + "\ufff0" : searchValue) : offset.getStartKey();
		String to = searchValue != null ? (isDesc ? searchValue : searchValue + "\ufff0") : (isDesc ? null : "\ufff0");

		return pagedQueryView("by_ssin_or_nihii", from, to, offset, isDesc);
	}

	@Override
	@View(name = "by_hcParty_name", map = "classpath:js/healthcareparty/By_hcparty_name_map.js")
	public PaginatedList<HealthcareParty> findByHcPartyNameContainsFuzzy(String searchString, PaginationOffset offset, Boolean desc) {
		String r = (searchString != null) ? StringUtils.sanitizeString(searchString) : null;
		boolean isDesc = desc != null && desc;
		String from = (offset.getStartKey() == null) ? (isDesc ? r + "\ufff0" : r) : (String) offset.getStartKey();
		String to = r != null ? (isDesc ? r : r + "\ufff0") : (isDesc ? null : "\ufff0");

		return pagedQueryView("by_hcParty_name", from, to, offset, isDesc);
	}

	@Override
	public List<HealthcareParty> findHealthcareParties(String searchString, int offset, int limit) {
		// TODO test
		String r = StringUtils.sanitizeString(searchString);
		ComplexKey from = ComplexKey.of(r);
		ComplexKey to = ComplexKey.of( r  + "\ufff0" );
		List<HealthcareParty> result = queryResults(createQuery("by_hcParty_name")
				.includeDocs(true)
				.startKey(from).endKey(to).limit(limit + offset));
		return result.subList(offset,result.size());
	}

    @Override
    @View(name = "by_hcparty_delegate_keys", map = "classpath:js/healthcareparty/By_hcparty_delegate_keys_map.js")
    public Map<String, String> getHcPartyKeysForDelegate(String healthcarePartyId) {
		//Not transactional aware
		ViewResult result = db.queryView(createQuery("by_hcparty_delegate_keys")
                .includeDocs(false)
                .key(healthcarePartyId));

        Map<String,String> resultMap = new HashMap<>();
        for (ViewResult.Row row : result.getRows()) {
            JsonNode valueNode = row.getValueAsNode();
            resultMap.put(valueNode.get(0).asText(), valueNode.get(1).asText());
        }

        return resultMap;
    }
}
