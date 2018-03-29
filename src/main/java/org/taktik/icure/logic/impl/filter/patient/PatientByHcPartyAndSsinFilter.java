/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.logic.impl.filter.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.User;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.logic.impl.filter.Filter;
import org.taktik.icure.logic.impl.filter.Filters;

import javax.security.auth.login.LoginException;
import java.util.HashSet;
import java.util.Set;

public class PatientByHcPartyAndSsinFilter implements Filter<String, Patient, org.taktik.icure.dto.filter.patient.PatientByHcPartyAndSsinFilter> {
    PatientLogic patientLogic;
    ICureSessionLogic sessionLogic;

    @Autowired
    public void setPatientLogic(PatientLogic patientLogic) {
        this.patientLogic = patientLogic;
    }
    @Autowired
    public void setSessionLogic(ICureSessionLogic sessionLogic) {
        this.sessionLogic = sessionLogic;
    }

    @Override
    public Set<String> resolve(org.taktik.icure.dto.filter.patient.PatientByHcPartyAndSsinFilter filter, Filters context) {
        try {
            return new HashSet<>(patientLogic.listByHcPartyAndSsinIdsOnly(filter.getSsin(), filter.getHealthcarePartyId() != null ? filter.getHealthcarePartyId() : getLoggedHealthCarePartyId()));
        } catch (LoginException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String getLoggedHealthCarePartyId() throws LoginException {
        User user = sessionLogic.getCurrentSessionContext().getUser();
        if (user == null || user.getHealthcarePartyId() == null) {
            throw new LoginException("You must be logged to perform this action. ");
        }
        return user.getHealthcarePartyId();
    }
}
