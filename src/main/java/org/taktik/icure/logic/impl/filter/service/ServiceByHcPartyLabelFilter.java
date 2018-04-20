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

package org.taktik.icure.logic.impl.filter.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.entities.User;
import org.taktik.icure.entities.embed.Service;
import org.taktik.icure.logic.ContactLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.impl.filter.Filter;
import org.taktik.icure.logic.impl.filter.Filters;


public class ServiceByHcPartyLabelFilter implements Filter<String, Service, org.taktik.icure.dto.filter.service.ServiceByHcPartyLabelFilter> {
	ContactLogic contactLogic;
	ICureSessionLogic sessionLogic;

	@Autowired
	public void setContactLogic(ContactLogic contactLogic) {
		this.contactLogic = contactLogic;
	}
	@Autowired
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Override
	public Set<String> resolve(org.taktik.icure.dto.filter.service.ServiceByHcPartyLabelFilter filter, Filters context) {
		try {
            String hcPartyId = filter.getHealthcarePartyId() != null ? filter.getHealthcarePartyId() : getLoggedHealthCarePartyId();
            HashSet<String> ids = null;
            if (filter.getLabel() != null) {
                ids = new HashSet<>(contactLogic.findServicesByLabel(
                        hcPartyId,
                        filter.getPatientSecretForeignKey(), filter.getLabel(),
                        filter.getStartValueDate(), filter.getEndValueDate()
                ));
            }
			return ids != null ? ids : new HashSet<>();
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
