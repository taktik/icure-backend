package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Agenda
import org.taktik.icure.services.external.rest.v1.dto.AgendaDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.RightMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class, RightMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AgendaMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(agendaDto: AgendaDto):Agenda
	fun map(agenda: Agenda):AgendaDto
}
