package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.Encryptable
import org.taktik.icure.services.external.rest.v1.dto.base.EncryptableDto
@Mapper
interface EncryptableMapper {
	fun map(encryptableDto: EncryptableDto):Encryptable
	fun map(encryptable: Encryptable):EncryptableDto
}
