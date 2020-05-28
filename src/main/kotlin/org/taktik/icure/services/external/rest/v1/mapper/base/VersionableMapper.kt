package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.Versionable
import org.taktik.icure.services.external.rest.v1.dto.base.VersionableDto
@Mapper
interface VersionableMapper {
	fun map(versionableDto: VersionableDto):Versionable
	fun map(versionable: Versionable):VersionableDto
}
