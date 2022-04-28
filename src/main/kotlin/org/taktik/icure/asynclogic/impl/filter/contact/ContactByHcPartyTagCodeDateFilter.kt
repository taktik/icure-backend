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
package org.taktik.icure.asynclogic.impl.filter.contact

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.asFlow
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

@Service
class ContactByHcPartyTagCodeDateFilter(private val contactLogic: ContactLogic,
                                        private val sessionLogic: AsyncSessionLogic) : Filter<String, Contact, ContactByHcPartyTagCodeDateFilter> {

    override fun resolve(filter: ContactByHcPartyTagCodeDateFilter, context: Filters) = flow {
        try {
            val hcPartyId: String = filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic)
            var ids: HashSet<String>? = null
            if (filter.tagType != null && filter.tagCode != null) {
                ids = HashSet(contactLogic.listContactIdsByTag(
                        hcPartyId,
                        filter.tagType!!,
                        filter.tagCode!!,
                        filter.startOfContactOpeningDate, filter.endOfContactOpeningDate).toList())
            }
            if (filter.codeType != null && filter.codeCode != null) {
                val byCode = contactLogic.listContactIdsByCode(
                        hcPartyId,
                        filter.codeType!!,
                        filter.codeCode!!,
                        filter.startOfContactOpeningDate, filter.endOfContactOpeningDate).toList()
                if (ids == null) {
                    ids = HashSet(byCode)
                } else {
                    ids.retainAll(byCode)
                }
            }
            emitAll(ids?.asFlow() ?: contactLogic.listContactIds(hcPartyId))
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}
