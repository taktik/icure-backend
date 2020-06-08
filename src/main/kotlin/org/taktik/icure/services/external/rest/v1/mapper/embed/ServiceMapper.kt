package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto
@Mapper(componentModel = "spring")
interface ServiceMapper {
	fun map(serviceDto: ServiceDto):Service
	fun map(service: Service):ServiceDto
}
