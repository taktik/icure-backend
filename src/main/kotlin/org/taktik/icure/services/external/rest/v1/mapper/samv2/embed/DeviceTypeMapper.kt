package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.DeviceType
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.DeviceTypeDto
@Mapper(componentModel = "spring", uses = [SamTextMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface DeviceTypeMapper {
	fun map(deviceTypeDto: DeviceTypeDto):DeviceType
	fun map(deviceType: DeviceType):DeviceTypeDto
}
