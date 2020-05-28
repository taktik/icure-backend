package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PersonalStatus
import org.taktik.icure.services.external.rest.v1.dto.embed.PersonalStatusDto
@Mapper
interface PersonalStatusMapper {
	fun map(personalStatusDto: PersonalStatusDto):PersonalStatus
	fun map(personalStatus: PersonalStatus):PersonalStatusDto
}
