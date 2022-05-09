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

import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.entities.embed.Service

@org.springframework.stereotype.Service
class ServiceByHcPartyFilter(
	private val contactLogic: ContactLogic,
	private val sessionLogic: AsyncSessionLogic
) : Filter<String, Service, org.taktik.icure.domain.filter.Filters.ByHcpartyFilter<String, Service>> {
	override fun resolve(filter: org.taktik.icure.domain.filter.Filters.ByHcpartyFilter<String, Service>, context: Filters) = flow {
		emitAll(contactLogic.listServiceIdsByHcParty(filter.hcpId))
	}
}
