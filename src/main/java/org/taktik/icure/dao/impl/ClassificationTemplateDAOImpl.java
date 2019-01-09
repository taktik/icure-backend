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
import org.taktik.icure.dao.ClassificationTemplateDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.ClassificationTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dlm on 16-07-18
 */
@Repository("classificationTemplateDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.ClassificationTemplate' && !doc.deleted) emit( doc.label, doc._id )}")
class ClassificationTemplateDAOImpl extends GenericIcureDAOImpl<ClassificationTemplate> implements ClassificationTemplateDAO {
    @Autowired
    public ClassificationTemplateDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbBase") CouchDbICureConnector couchdb, IDGenerator idGenerator) {
        super(ClassificationTemplate.class, couchdb, idGenerator);
        initStandardDesignDocument();
    }

	@Override
	public ClassificationTemplate getClassificationTemplate(String classificationTemplateId) {
		return get(classificationTemplateId);
	}

    @Override
    @View(name = "by_hcparty_patient", map = "classpath:js/classificationtemplate/By_hcparty_patient_map.js")
    public List<ClassificationTemplate> findByHCPartySecretPatientKeys(String hcPartyId, ArrayList<String> secretPatientKeys) {
        ComplexKey[] keys = secretPatientKeys.stream().map(fk -> ComplexKey.of(hcPartyId, fk)).collect(Collectors.toList()).toArray(new ComplexKey[secretPatientKeys.size()]);

        List<ClassificationTemplate> result = new ArrayList<>();
        queryView("by_hcparty_patient", keys).forEach((e)->{if (result.isEmpty() || !e.getId().equals(result.get(result.size()-1).getId())) {result.add(e); }});

        return result;
    }

	@Override
	public PaginatedList<ClassificationTemplate> listClassificationTemplates(PaginationOffset paginationOffset) {
		return pagedQueryView(
				"all",
				paginationOffset.getStartKey() != null ? paginationOffset.getStartKey().toString() : "\u0000",
				"\ufff0",
				paginationOffset, false
		);
	}


}
