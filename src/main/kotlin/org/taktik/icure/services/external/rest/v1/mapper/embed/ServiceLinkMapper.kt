package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.ServiceLink
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceLinkDto
@Mapper(componentModel = "spring", uses = [DelegationMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ServiceLinkMapper {
	fun map(serviceLinkDto: ServiceLinkDto):ServiceLink
	fun map(serviceLink: ServiceLink):ServiceLinkDto
}
