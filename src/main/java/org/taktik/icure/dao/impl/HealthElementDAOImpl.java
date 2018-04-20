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
import org.taktik.icure.dao.HealthElementDAO;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.entities.Document;
import org.taktik.icure.entities.HealthElement;
import org.taktik.icure.entities.base.Code;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by aduchate on 18/07/13, 13:36
 */
@Repository("healthElementDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthElement' && !doc.deleted) emit( doc.patientId, doc._id )}")
class HealthElementDAOImpl extends GenericIcureDAOImpl<HealthElement> implements HealthElementDAO {
    @Autowired
    public HealthElementDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector couchdb, IDGenerator idGenerator) {
        super(HealthElement.class, couchdb, idGenerator);
        initStandardDesignDocument();
    }

    @Override
    public List<HealthElement> findByPatient(String patientId) {
        return queryView("all", patientId);
    }

    @Override
    @View(name = "by_patient_and_codes", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthElement' && !doc.deleted) {\n" +
            "  for (var i=0;i<doc.codes.length;i++) {\n" +
            "  emit( [doc.patientId, doc.codes[i].type+':'+doc.codes[i].code], doc._id );\n" +
            "  }}}")
    public List<HealthElement> findByPatientAndCodes(String patientId, Set<Code> codes) {
        ComplexKey[] keys = codes.stream().map(c -> ComplexKey.of(patientId, c.toString())).collect(Collectors.toList()).toArray(new ComplexKey[codes.size()]);
        return queryView("by_patient_and_codes", keys);
    }

    @Override
    @View(name = "by_planOfActionId", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthElement' && !doc.deleted) {\n" +
            "            for(var i= 0;i<doc.plansOfAction.length;i++) {\n" +
            "        emit([doc.plansOfAction[i].id], doc._id);\n" +
            "    }\n" +
            "}}")
    public HealthElement findHealthElementByPlanOfActionId(String planOfActionId) {
        List<HealthElement> result = queryView("by_planOfActionId", planOfActionId);
        return result.size()>0?result.get(0):null;
    }

	@Override
	public HealthElement getHealthElement(String healthElementId) {
		return get(healthElementId);
	}

	@Override
	@View(name = "by_hcparty_patient", map = "classpath:js/healthelement/By_hcparty_patient_map.js")
	public List<HealthElement> findByHCPartySecretPatientKeys(String hcPartyId, List<String> secretPatientKeys) {
		ComplexKey[] keys = secretPatientKeys.stream().map(fk -> ComplexKey.of(hcPartyId, fk)).collect(Collectors.toList()).toArray(new ComplexKey[secretPatientKeys.size()]);

        List<HealthElement> result = new ArrayList<>();
        queryView("by_hcparty_patient", keys).forEach((e)->{if (result.isEmpty() || !e.getId().equals(result.get(result.size()-1).getId())) {result.add(e); }});

        return result;
	}

	@Override
	@View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.HealthElement' && !doc.deleted && doc._conflicts) emit(doc._id )}")
	public List<HealthElement> listConflicts() {
		return queryView("conflicts");
	}
}
