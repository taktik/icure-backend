package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Medication
import org.taktik.icure.services.external.rest.v1.dto.embed.MedicationDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [RenewalMapper::class, MedicinalproductMapper::class, CodeStubMapper::class, RegimenItemMapper::class, SuspensionMapper::class, ParagraphAgreementMapper::class, SubstanceproductMapper::class, DurationMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MedicationMapper {
	fun map(medicationDto: MedicationDto):Medication
	fun map(medication: Medication):MedicationDto
}
