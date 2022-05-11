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

package org.taktik.icure.services.external.rest.v2.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.services.external.rest.v2.dto.embed.DelegationDto

@Mapper(componentModel = "spring", uses = [], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface DelegationV2Mapper {
	fun map(delegationDto: DelegationDto): Delegation
	fun map(delegation: Delegation): DelegationDto

	fun mapDelegationsMaps(delegations: Map<String, Set<Delegation>>): Map<String, Set<DelegationDto>> {
		return delegations.mapValues { it.value.map { map(it) }.toSet() }
	}

	fun mapDelegationDtosMaps(delegations: Map<String, Set<DelegationDto>>): Map<String, Set<Delegation>> {
		return delegations.mapValues { it.value.map { map(it) }.toSet() }
	}

	fun mapDelegations(delegations: Set<Delegation>): Set<DelegationDto> {
		return delegations.map { map(it) }.toSet()
	}

	fun mapDelegationDtos(delegations: Set<DelegationDto>): Set<Delegation> {
		return delegations.map { map(it) }.toSet()
	}
}
