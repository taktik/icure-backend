package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class, DelegationMapper::class, MedicationMapper::class, MeasureMapper::class, ContractChangeTypeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ServiceMapper {
	fun map(serviceDto: ServiceDto):Service
	fun map(service: Service):ServiceDto
    fun map(contentDto: ContentDto): Content
    fun map(content: Content): ContentDto
}
