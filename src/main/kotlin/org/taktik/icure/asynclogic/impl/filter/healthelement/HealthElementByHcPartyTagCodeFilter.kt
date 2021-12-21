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

package org.taktik.icure.asynclogic.impl.filter.healthelement

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toSet
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.healthelement.HealthElementByHcPartyTagCodeFilter
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.utils.getLoggedHealthCarePartyId

@Service
class HealthElementByHcPartyTagCodeFilter(private val healthElementLogic: HealthElementLogic,
                                          private val sessionLogic: AsyncSessionLogic) : Filter<String, HealthElement, HealthElementByHcPartyTagCodeFilter> {

    override fun resolve(filter: HealthElementByHcPartyTagCodeFilter, context: Filters) = flow<String> {
        try {
            val hcPartyId: String = filter.healthCarePartyId ?: getLoggedHealthCarePartyId(sessionLogic)
            var ids: HashSet<String>? = null
            if (filter.tagType != null && filter.tagCode != null) {
                ids = HashSet(healthElementLogic.listHealthElementIdsByHcPartyAndTags(hcPartyId, filter.tagType!!, filter.tagCode!!).toSet())
            }
            if (filter.codeType != null && filter.codeNumber != null) {
                val byCode = HashSet(healthElementLogic.listHealthElementIdsByHcPartyAndCodes(hcPartyId, filter.codeType!!, filter.codeNumber!!).toSet())
                if (ids == null) {
                    ids = byCode
                } else {
                    ids.retainAll(byCode)
                }
            }
            if (filter.status != null) {
                val byStatus = HashSet(healthElementLogic.listHealthElementIdsByHcPartyAndStatus(hcPartyId, filter.status!!).toSet())
                if (ids == null) {
                    ids = byStatus
                } else {
                    ids.retainAll(byStatus)
                }
            }
            ids?.forEach { emit(it) }
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}
