package org.taktik.icure.services.external.rest.v2.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.IndexedIdentifier
import org.taktik.icure.services.external.rest.v2.dto.IndexedIdentifierDto
import org.taktik.icure.services.external.rest.v2.mapper.base.IdentifierV2Mapper

@Mapper(componentModel = "spring", uses = [IdentifierV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class IndexedIdentifierV2Mapper {
    abstract fun map(indexedIdentifierDto: IndexedIdentifierDto): IndexedIdentifier
    abstract fun map(indexedIdentifier: IndexedIdentifier): IndexedIdentifierDto
}
