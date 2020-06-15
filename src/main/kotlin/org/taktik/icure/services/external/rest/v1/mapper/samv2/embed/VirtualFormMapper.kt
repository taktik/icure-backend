package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.VirtualForm
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.VirtualFormDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [SamTextMapper::class, CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface VirtualFormMapper {
	fun map(virtualFormDto: VirtualFormDto):VirtualForm
	fun map(virtualForm: VirtualForm):VirtualFormDto
}
