package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.DeliveryEnvironment
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.DeliveryEnvironmentDto
@Mapper(componentModel = "spring")
interface DeliveryEnvironmentMapper {
	fun map(deliveryEnvironmentDto: DeliveryEnvironmentDto):DeliveryEnvironment
	fun map(deliveryEnvironment: DeliveryEnvironment):DeliveryEnvironmentDto
}
