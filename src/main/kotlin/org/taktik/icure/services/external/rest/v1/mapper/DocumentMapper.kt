package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.services.external.rest.v1.dto.DocumentDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto

@Mapper(componentModel = "spring")
interface DocumentMapper {
	fun map(documentDto: DocumentDto):Document
	fun map(document: Document):DocumentDto

    fun mapToStub(invoice: Document): IcureStubDto
    fun mapFromStub(invoice: IcureStubDto): Document
}
