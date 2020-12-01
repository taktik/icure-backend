package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.RouteOfAdministration
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.RouteOfAdministrationDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [SamTextMapper::class, CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface RouteOfAdministrationMapper {
	fun map(routeOfAdministrationDto: RouteOfAdministrationDto):RouteOfAdministration
	fun map(routeOfAdministration: RouteOfAdministration):RouteOfAdministrationDto
}
