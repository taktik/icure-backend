package org.taktik.icure.services.external.rest.v1.mapper.samv2

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.services.external.rest.v1.dto.samv2.AmpDto
@Mapper(componentModel = "spring")
interface AmpMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true),
            Mapping(target = "set_type", ignore = true)
            )
	fun map(ampDto: AmpDto):Amp
	fun map(amp: Amp):AmpDto
}
