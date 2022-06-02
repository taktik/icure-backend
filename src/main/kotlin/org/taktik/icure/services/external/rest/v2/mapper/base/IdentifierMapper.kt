package org.taktik.icure.services.external.rest.v2.mapper.base

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.services.external.rest.v2.dto.base.IdentifierDto

@Mapper(componentModel = "spring", uses = [CodeStubV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class IdentifierV2Mapper {
	abstract fun map(IdentifierDto: IdentifierDto): Identifier
	abstract fun map(codeStub: Identifier): IdentifierDto
}
