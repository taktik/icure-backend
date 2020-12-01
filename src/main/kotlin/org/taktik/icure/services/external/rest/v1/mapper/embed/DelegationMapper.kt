package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
@Mapper(componentModel = "spring", uses = [DelegationTagMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface DelegationMapper {
	fun map(delegationDto: DelegationDto):Delegation
	fun map(delegation: Delegation):DelegationDto

    fun mapDelegationsMaps(delegations:Map<String, Set<Delegation>>): Map<String, Set<DelegationDto>> {
        return delegations.mapValues { it.value.map { map(it) }.toSet() }
    }

    fun mapDelegationDtosMaps(delegations:Map<String, Set<DelegationDto>>): Map<String, Set<Delegation>> {
        return delegations.mapValues { it.value.map { map(it) }.toSet() }
    }

    fun mapDelegations(delegations:Set<Delegation>): Set<DelegationDto> {
        return delegations.map { map(it) }.toSet()
    }

    fun mapDelegationDtos(delegations:Set<DelegationDto>): Set<Delegation> {
        return delegations.map { map(it) }.toSet()
    }

}
