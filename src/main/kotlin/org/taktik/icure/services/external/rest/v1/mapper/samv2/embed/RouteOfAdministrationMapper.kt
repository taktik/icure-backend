package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.RouteOfAdministration
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.RouteOfAdministrationDto
@Mapper
interface RouteOfAdministrationMapper {
	fun map(routeOfAdministrationDto: RouteOfAdministrationDto):RouteOfAdministration
	fun map(routeOfAdministration: RouteOfAdministration):RouteOfAdministrationDto
}
