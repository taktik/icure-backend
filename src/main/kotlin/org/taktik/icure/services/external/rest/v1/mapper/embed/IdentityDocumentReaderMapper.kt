package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.IdentityDocumentReader
import org.taktik.icure.services.external.rest.v1.dto.embed.IdentityDocumentReaderDto
@Mapper(componentModel = "spring")
interface IdentityDocumentReaderMapper {
	fun map(identityDocumentReaderDto: IdentityDocumentReaderDto):IdentityDocumentReader
	fun map(identityDocumentReader: IdentityDocumentReader):IdentityDocumentReaderDto
}
