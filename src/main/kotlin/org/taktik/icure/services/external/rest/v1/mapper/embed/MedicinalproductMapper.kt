package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Medicinalproduct
import org.taktik.icure.services.external.rest.v1.dto.embed.MedicinalproductDto
@Mapper(componentModel = "spring")
interface MedicinalproductMapper {
	fun map(medicinalproductDto: MedicinalproductDto):Medicinalproduct
	fun map(medicinalproduct: Medicinalproduct):MedicinalproductDto
}
