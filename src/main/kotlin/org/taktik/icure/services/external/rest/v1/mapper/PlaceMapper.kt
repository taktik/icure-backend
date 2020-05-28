package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Place
import org.taktik.icure.services.external.rest.v1.dto.PlaceDto
@Mapper
interface PlaceMapper {
	fun map(placeDto: PlaceDto):Place
	fun map(place: Place):PlaceDto
}
