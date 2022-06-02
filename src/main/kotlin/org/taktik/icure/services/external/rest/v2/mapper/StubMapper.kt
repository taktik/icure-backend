/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v2.mapper

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
import org.taktik.icure.services.external.rest.v2.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v2.mapper.embed.DelegationV2Mapper

@Mapper(componentModel = "spring", uses = [DelegationV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface StubV2Mapper {
	fun mapToStub(contact: Contact): IcureStubDto
	fun mapToStub(calendarItem: CalendarItem): IcureStubDto
	fun mapToStub(message: Message): IcureStubDto
	fun mapToStub(healthElement: HealthElement): IcureStubDto
	fun mapToStub(form: Form): IcureStubDto
	fun mapToStub(document: Document): IcureStubDto
	fun mapToStub(classification: Classification): IcureStubDto
	fun mapToStub(invoice: Invoice): IcureStubDto
}
