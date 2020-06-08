package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.CalendarItem
import org.taktik.icure.entities.Contact
import org.taktik.icure.services.external.rest.v1.dto.CalendarItemDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto

@Mapper(componentModel = "spring")
interface CalendarItemMapper {
	fun map(calendarItemDto: CalendarItemDto):CalendarItem
	fun map(calendarItem: CalendarItem):CalendarItemDto

    fun mapToStub(invoice: CalendarItem): IcureStubDto
    fun mapFromStub(invoice: IcureStubDto): CalendarItem

}
