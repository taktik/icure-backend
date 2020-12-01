package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.CalendarItem
import org.taktik.icure.services.external.rest.v1.dto.CalendarItemDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.AddressMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.CalendarItemTagMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.FlowItemMapper

@Mapper(componentModel = "spring", uses = [CalendarItemTagMapper::class, CodeStubMapper::class, DelegationMapper::class, AddressMapper::class, FlowItemMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface CalendarItemMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(calendarItemDto: CalendarItemDto):CalendarItem
	fun map(calendarItem: CalendarItem):CalendarItemDto
}
