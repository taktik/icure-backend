package org.taktik.icure.logic.impl.filter.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.dto.filter.patient.PatientByIdsFilter;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.User;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.logic.impl.filter.Filter;
import org.taktik.icure.logic.impl.filter.Filters;

import javax.security.auth.login.LoginException;
import java.util.HashSet;
import java.util.Set;

public class PatientByHcPartyDateOfBirthNameFilter implements Filter<String, Patient, org.taktik.icure.dto.filter.patient.PatientByHcPartyDateOfBirthNameFilter> {

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

    private String getLoggedHealthCarePartyId() throws LoginException {
        User user = sessionLogic.getCurrentSessionContext().getUser();
        if (user == null || user.getHealthcarePartyId() == null) {
            throw new LoginException("You must be logged to perform this action. ");
        }
        return user.getHealthcarePartyId();
    }

    @Override
    public Set<String> resolve(org.taktik.icure.dto.filter.patient.PatientByHcPartyDateOfBirthNameFilter filter, Filters context) {
        try {
            return new HashSet<>(patientLogic.listByHcPartyNameOrBirthday(filter.getName(), filter.getHealthcarePartyId() != null ? filter.getHealthcarePartyId() : getLoggedHealthCarePartyId(), filter.getDateOfBirth()));
        } catch (LoginException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
