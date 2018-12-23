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

import org.ektorp.ComplexKey;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.FormDAO;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Document;
import org.taktik.icure.entities.Form;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by aduchate on 02/02/13, 15:24
 */
@Repository("formDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Form' && !doc.deleted) emit(null, doc._id )}")
class FormDAOImpl extends GenericIcureDAOImpl<Form> implements FormDAO {
    @Autowired
    public FormDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector couchdb, IDGenerator idGenerator) {
        super(Form.class, couchdb, idGenerator);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_hcparty_patientfk", map = "classpath:js/form/By_hcparty_patientfk_map.js")
    public List<Form> findByHcPartyPatient(String hcPartyId, List<String> secretPatientKeys) {
        ComplexKey[] keys = secretPatientKeys.stream().map(
                fk -> ComplexKey.of(hcPartyId, fk)
        ).collect(Collectors.toList()).toArray(new ComplexKey[secretPatientKeys.size()]);
        List<Form> result = new ArrayList<>();
        queryView("by_hcparty_patientfk", keys).forEach((e)->{if (result.isEmpty() || !e.getId().equals(result.get(result.size()-1).getId())) {result.add(e); }});

        return result;
    }

    @Override
    @View(name = "by_hcparty_parentId", map = "classpath:js/form/By_hcparty_parent_id.js")
    public List<Form> findByHcPartyParentId(String hcPartyId, String formId) {
        return queryView("by_hcparty_parentId", ComplexKey.of(hcPartyId, formId));
    }

	@Override
	public PaginatedList<Form> findAll(PaginationOffset<String> pagination) {
		return pagedQueryView("all", pagination.getStartKey(), null, pagination, false);
	}

	@Override
	@View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Form' && !doc.deleted && doc._conflicts) emit(doc._id )}")
	public List<Form> listConflicts() {
		return queryView("conflicts");
	}

}
