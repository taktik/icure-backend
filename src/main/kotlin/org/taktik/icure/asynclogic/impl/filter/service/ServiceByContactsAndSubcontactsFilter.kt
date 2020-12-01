/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */
package org.taktik.icure.asynclogic.impl.filter.service

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.service.ServiceByContactsAndSubcontactsFilter

@FlowPreview
@Service
class ServiceByContactsAndSubcontactsFilter(private val contactLogic: ContactLogic) : Filter<String, org.taktik.icure.entities.embed.Service, ServiceByContactsAndSubcontactsFilter> {

    override fun resolve(filter: ServiceByContactsAndSubcontactsFilter, context: Filters): Flow<String> {
        val contacts = contactLogic.getContacts(filter.contacts)
        return if (filter.subContacts != null) {
            contacts.flatMapConcat { c -> c.subContacts.flatMap { sc -> if (filter.subContacts!!.contains(sc.id)) sc.services.mapNotNull { it.serviceId } else listOf() }.asFlow() }
        } else {
            contacts.flatMapConcat { c -> c.services.map { it.id }.asFlow() }
        }
    }
}
