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
import org.taktik.icure.dao.ClassificationDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.Classification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dlm on 16-07-18
 */
@Repository("classificationDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Classification' && !doc.deleted) emit( doc.patientId, doc._id )}")
class ClassificationDAOImpl extends GenericIcureDAOImpl<Classification> implements ClassificationDAO {
    @Autowired
    public ClassificationDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector couchdb, IDGenerator idGenerator) {
        super(Classification.class, couchdb, idGenerator);
        initStandardDesignDocument();
    }

    @Override
    public List<Classification> findByPatient(String patientId) {
        return queryView("all", patientId);
    }

	@Override
	public Classification getClassification(String classificationId) {
		return get(classificationId);
	}

	@Override
	@View(name = "by_hcparty_patient", map = "classpath:js/classification/By_hcparty_patient_map.js")
	public List<Classification> findByHCPartySecretPatientKeys(String hcPartyId, List<String> secretPatientKeys) {
		ComplexKey[] keys = secretPatientKeys.stream().map(fk -> ComplexKey.of(hcPartyId, fk)).collect(Collectors.toList()).toArray(new ComplexKey[secretPatientKeys.size()]);

        List<Classification> result = new ArrayList<>();
        queryView("by_hcparty_patient", keys).forEach((e)->{if (result.isEmpty() || !e.getId().equals(result.get(result.size()-1).getId())) {result.add(e); }});

        return result;
	}
}
