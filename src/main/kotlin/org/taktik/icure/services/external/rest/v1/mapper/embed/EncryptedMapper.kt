package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Encrypted
import org.taktik.icure.services.external.rest.v1.dto.embed.EncryptedDto
@Mapper(componentModel = "spring")
interface EncryptedMapper {
	fun map(encryptedDto: EncryptedDto):Encrypted
	fun map(encrypted: Encrypted):EncryptedDto
}
