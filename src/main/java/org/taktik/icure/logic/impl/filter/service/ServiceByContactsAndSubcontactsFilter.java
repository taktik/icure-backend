/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.entities.embed.Service;
import org.taktik.icure.entities.embed.ServiceLink;
import org.taktik.icure.entities.embed.SubContact;
import org.taktik.icure.logic.ContactLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.logic.impl.filter.Filter;
import org.taktik.icure.logic.impl.filter.Filters;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@org.springframework.stereotype.Service
public class ServiceByContactsAndSubcontactsFilter implements Filter<String, Service, org.taktik.icure.dto.filter.service.ServiceByContactsAndSubcontactsFilter> {
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
	public Set<String> resolve(org.taktik.icure.dto.filter.service.ServiceByContactsAndSubcontactsFilter filter, Filters context) {
        Set<String> ids = new HashSet<>();
        for (Contact c : contactLogic.getContacts(filter.getContacts())) {
            if (filter.getSubContacts()!=null) {
                for (SubContact sc: c.getSubContacts()) {
                    if (filter.getSubContacts().contains(sc.getId()) && sc.getServices() != null) {
                        ids.addAll(sc.getServices().stream().map(ServiceLink::getServiceId).collect(Collectors.toList()));
                    }
                }
            } else {
                ids.addAll(c.getServices().stream().map(Service::getId).collect(Collectors.toList()));
            }
         }

        return ids;
    }
}
