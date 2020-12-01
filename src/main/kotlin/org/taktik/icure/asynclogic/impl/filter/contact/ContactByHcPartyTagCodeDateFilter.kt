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
package org.taktik.icure.asynclogic.impl.filter.contact

import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.contact.ContactByHcPartyTagCodeDateFilter
import org.taktik.icure.entities.Contact
import org.taktik.icure.utils.getLoggedHealthCarePartyId
import java.util.*
import javax.security.auth.login.LoginException

@Service
class ContactByHcPartyTagCodeDateFilter(private val contactLogic: ContactLogic,
                                        private val sessionLogic: AsyncSessionLogic) : Filter<String, Contact, ContactByHcPartyTagCodeDateFilter> {

    override fun resolve(filter: ContactByHcPartyTagCodeDateFilter, context: Filters) = flow {
        try {
            val hcPartyId: String = filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic)
            var ids: HashSet<String>? = null
            if (filter.tagType != null && filter.tagCode != null) {
                ids = HashSet(contactLogic.listServiceIdsByTag(
                        hcPartyId,
                        null,
                        filter.tagType!!,
                        filter.tagCode!!,
                        filter.startOfContactOpeningDate, filter.endOfContactOpeningDate).toList())
            }
            if (filter.codeType != null && filter.codeCode != null) {
                val byCode = contactLogic.listServiceIdsByCode(
                        hcPartyId,
                        null,
                        filter.tagType!!,
                        filter.tagCode!!,
                        filter.startOfContactOpeningDate, filter.endOfContactOpeningDate).toList()
                if (ids == null) {
                    ids = HashSet(byCode)
                } else {
                    ids.retainAll(byCode)
                }
            }
            emitAll(if (ids == null) contactLogic.listContactIds(hcPartyId) else contactLogic.listIdsByServices(ids))
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}
