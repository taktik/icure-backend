package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.TimeTable
import org.taktik.icure.services.external.rest.v1.dto.TimeTableDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.TimeTableItemMapper

@Mapper(componentModel = "spring", uses = [TimeTableItemMapper::class, CodeStubMapper::class, DelegationMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface TimeTableMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(timeTableDto: TimeTableDto):TimeTable
	fun map(timeTable: TimeTable):TimeTableDto
}
