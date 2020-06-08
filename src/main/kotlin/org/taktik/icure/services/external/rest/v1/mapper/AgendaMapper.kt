package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Agenda
import org.taktik.icure.services.external.rest.v1.dto.AgendaDto
@Mapper(componentModel = "spring")
interface AgendaMapper {
	fun map(agendaDto: AgendaDto):Agenda
	fun map(agenda: Agenda):AgendaDto
}
