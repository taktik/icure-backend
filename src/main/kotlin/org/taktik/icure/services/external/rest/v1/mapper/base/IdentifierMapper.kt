package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.services.external.rest.v1.dto.base.IdentifierDto

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class IdentifierMapper {
	abstract fun map(IdentifierDto: IdentifierDto): Identifier
	abstract fun map(codeStub: Identifier): IdentifierDto
}
