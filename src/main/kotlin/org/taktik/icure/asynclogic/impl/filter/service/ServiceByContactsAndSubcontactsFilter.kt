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
package org.taktik.icure.asynclogic.impl.filter.service

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.dto.filter.service.ServiceByContactsAndSubcontactsFilter

@FlowPreview
@Service
class ServiceByContactsAndSubcontactsFilter(private val contactLogic: ContactLogic) : Filter<String, org.taktik.icure.entities.embed.Service, ServiceByContactsAndSubcontactsFilter> {

    override fun resolve(filter: ServiceByContactsAndSubcontactsFilter, context: Filters): Flow<String> {
        val contacts = contactLogic.getContacts(filter.contacts)
        return if (filter.subContacts != null) {
            contacts.flatMapConcat { c -> c.subContacts?.flatMap { sc -> if (filter.subContacts.contains(sc.id) && sc.services != null) sc.services.mapNotNull { it?.serviceId } else listOf() }!!.asFlow() }
        } else {
            contacts.flatMapConcat { c -> c.services?.mapNotNull { it.id }!!.asFlow() }
        }
    }
}
