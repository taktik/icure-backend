package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.NoGenericPrescriptionReason
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.NoGenericPrescriptionReasonDto
@Mapper(componentModel = "spring", uses = [SamTextMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface NoGenericPrescriptionReasonMapper {
	fun map(noGenericPrescriptionReasonDto: NoGenericPrescriptionReasonDto):NoGenericPrescriptionReason
	fun map(noGenericPrescriptionReason: NoGenericPrescriptionReason):NoGenericPrescriptionReasonDto
}
