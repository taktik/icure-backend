package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.CalendarItem
import org.taktik.icure.entities.Classification
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.Message
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper

@Mapper(componentModel = "spring", uses = [DelegationMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface StubMapper {
    fun mapToStub(contact: Contact): IcureStubDto
    fun mapToStub(calendarItem: CalendarItem): IcureStubDto
    fun mapToStub(message: Message): IcureStubDto
    fun mapToStub(healthElement: HealthElement): IcureStubDto
    fun mapToStub(form: Form): IcureStubDto
    fun mapToStub(document: Document): IcureStubDto
    fun mapToStub(classification: Classification): IcureStubDto
    fun mapToStub(invoice: Invoice): IcureStubDto
}
