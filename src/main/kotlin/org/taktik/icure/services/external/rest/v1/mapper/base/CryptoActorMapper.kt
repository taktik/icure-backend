package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.CryptoActor
import org.taktik.icure.services.external.rest.v1.dto.base.CryptoActorDto
@Mapper(componentModel = "spring")
interface CryptoActorMapper {
	fun map(cryptoActorDto: CryptoActorDto):CryptoActor
	fun map(cryptoActor: CryptoActor):CryptoActorDto
}
