package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.embed.ServiceLink
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceLinkDto
@Mapper(componentModel = "spring", uses = [DelegationMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ServiceLinkMapper {
    @Mappings(
            Mapping(target = "service", ignore = true)
    )
    fun map(serviceLinkDto: ServiceLinkDto):ServiceLink
    @Mappings(
            Mapping(target = "service", ignore = true)
    )
	fun map(serviceLink: ServiceLink):ServiceLinkDto
}
