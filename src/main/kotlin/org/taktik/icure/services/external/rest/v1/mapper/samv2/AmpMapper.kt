package org.taktik.icure.services.external.rest.v1.mapper.samv2

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.services.external.rest.v1.dto.samv2.AmpDto
@Mapper
interface AmpMapper {
	fun map(ampDto: AmpDto):Amp
	fun map(amp: Amp):AmpDto
}
