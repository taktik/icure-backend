package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.IndexedIdentifier
import org.taktik.icure.services.external.rest.v1.dto.IndexedIdentifierDto
import org.taktik.icure.services.external.rest.v1.mapper.base.IdentifierMapper

@Mapper(componentModel = "spring", uses = [IdentifierMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class IndexedIdentifierMapper {
    abstract fun map(indexedIdentifierDto: IndexedIdentifierDto): IndexedIdentifier
    abstract fun map(indexedIdentifier: IndexedIdentifier): IndexedIdentifierDto
}
