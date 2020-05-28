package org.taktik.icure.services.external.rest.v1.mapper.utils

import org.mapstruct.Mapper

@Mapper
interface PaginatedDocumentKeyIdPairMapper {
    fun map(paginatedDocumentKeyIdPair: org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair<*>): org.taktik.icure.db.PaginatedDocumentKeyIdPair
    fun map(applicationSettings: org.taktik.icure.db.PaginatedDocumentKeyIdPair): org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair<*>
}
