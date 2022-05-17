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
package org.taktik.icure.asynclogic.impl.filter.hcparty

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toSet
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.entities.HealthcareParty

@Service
class HealthcarePartyByTagCodeFilter(
	private val healthcarePartyLogic: HealthcarePartyLogic,
) : Filter<String, HealthcareParty, org.taktik.icure.domain.filter.hcparty.HealthcarePartyByTagCodeFilter> {

	override fun resolve(filter: org.taktik.icure.domain.filter.hcparty.HealthcarePartyByTagCodeFilter, context: Filters): Flow<String> = flow {
		try {
			val idsByTag = if (filter.tagType != null) {
				healthcarePartyLogic.listHealthcarePartyIdsByTag(
					filter.tagType!!,
					filter.tagCode
				).toSet()
			}
			else {
				null
			}

			val idsByCode = if (filter.codeType != null) {
				healthcarePartyLogic.listHealthcarePartyIdsByCode(
					filter.codeType!!,
					filter.codeCode
				).toSet()
			}
			else {
				null
			}

			val ids = when {
				idsByTag != null && idsByCode != null -> idsByTag.intersect(idsByCode).asFlow()
				idsByTag != null -> idsByTag.asFlow()
				idsByCode != null -> idsByCode.asFlow()
				else -> healthcarePartyLogic.getEntityIds()
			}

			emitAll(ids)
		} catch (e: LoginException) {
			throw IllegalArgumentException(e)
		}
	}
}
