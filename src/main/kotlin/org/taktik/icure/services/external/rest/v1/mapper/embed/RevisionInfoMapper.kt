package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.services.external.rest.v1.dto.embed.RevisionInfoDto
@Mapper(componentModel = "spring")
interface RevisionInfoMapper {
	fun map(revisionInfoDto: RevisionInfoDto):RevisionInfo
	fun map(revisionInfo: RevisionInfo):RevisionInfoDto
}
