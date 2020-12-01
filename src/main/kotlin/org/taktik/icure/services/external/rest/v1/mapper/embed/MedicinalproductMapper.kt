package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Medicinalproduct
import org.taktik.icure.services.external.rest.v1.dto.embed.MedicinalproductDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class, MediumTypeMapper::class, MembershipTypeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MedicinalproductMapper {
	fun map(medicinalproductDto: MedicinalproductDto):Medicinalproduct
	fun map(medicinalproduct: Medicinalproduct):MedicinalproductDto
}
