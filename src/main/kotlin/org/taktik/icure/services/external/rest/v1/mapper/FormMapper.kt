package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Form
import org.taktik.icure.services.external.rest.v1.dto.FormDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto

@Mapper(componentModel = "spring")
interface FormMapper {
	fun map(formDto: FormDto):Form
	fun map(form: Form):FormDto

    fun mapToStub(invoice: Form): IcureStubDto
    fun mapFromStub(invoice: IcureStubDto): Form

}
