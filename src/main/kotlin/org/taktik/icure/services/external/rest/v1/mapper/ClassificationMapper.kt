package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Classification
import org.taktik.icure.entities.Contact
import org.taktik.icure.services.external.rest.v1.dto.ClassificationDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto

@Mapper(componentModel = "spring")
interface ClassificationMapper {
	fun map(classificationDto: ClassificationDto):Classification
	fun map(classification: Classification):ClassificationDto

    fun mapToStub(classification: Classification): IcureStubDto
    fun mapFromStub(classification: IcureStubDto): Classification

}
