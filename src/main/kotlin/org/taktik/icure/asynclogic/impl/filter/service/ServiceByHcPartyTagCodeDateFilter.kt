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

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toSet
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.service.ServiceByHcPartyTagCodeDateFilter
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.utils.getLoggedHealthCarePartyId
import java.util.*
import javax.security.auth.login.LoginException

class ServiceByHcPartyTagCodeDateFilter(private val contactLogic: ContactLogic,
                                        private val sessionLogic: AsyncSessionLogic) : Filter<String, Service, ServiceByHcPartyTagCodeDateFilter> {
    override fun resolve(filter: ServiceByHcPartyTagCodeDateFilter, context: Filters) = flow {
        try {
            val hcPartyId = filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic)
            var ids: HashSet<String>? = null
            val patientSFK = filter.patientSecretForeignKey
            val patientSFKList = if (patientSFK != null) listOf(patientSFK) else null
            if (filter.tagType != null && filter.tagCode != null) {
                ids = HashSet(contactLogic.listServiceIdsByTag(
                        hcPartyId,
                        patientSFKList, filter.tagType!!,
                        filter.tagCode!!, filter.startValueDate, filter.endValueDate
                ).toSet())
            }
            if (filter.codeType != null && filter.codeCode != null) {
                val byCode = contactLogic.listServiceIdsByCode(
                        hcPartyId,
                        patientSFKList, filter.codeType!!,
                        filter.codeCode!!, filter.startValueDate, filter.endValueDate
                ).toSet()
                if (ids == null) {
                    ids = HashSet(byCode)
                } else {
                    ids.retainAll(byCode)
                }
            }
            emitAll((ids ?: HashSet()).asFlow())
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}
