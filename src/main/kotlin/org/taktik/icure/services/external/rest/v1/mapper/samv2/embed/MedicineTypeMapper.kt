package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.MedicineType
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.MedicineTypeDto
@Mapper(componentModel = "spring")
interface MedicineTypeMapper {
	fun map(medicineTypeDto: MedicineTypeDto):MedicineType
	fun map(medicineType: MedicineType):MedicineTypeDto
}
