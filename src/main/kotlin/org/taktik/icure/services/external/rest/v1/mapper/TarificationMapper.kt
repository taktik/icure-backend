package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Tarification
import org.taktik.icure.services.external.rest.v1.dto.TarificationDto
@Mapper
interface TarificationMapper {
	fun map(tarificationDto: TarificationDto):Tarification
	fun map(tarification: Tarification):TarificationDto
}
