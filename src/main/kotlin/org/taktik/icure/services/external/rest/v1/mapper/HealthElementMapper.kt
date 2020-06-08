package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.services.external.rest.v1.dto.HealthElementDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto

@Mapper(componentModel = "spring")
interface HealthElementMapper {
	fun map(healthElementDto: HealthElementDto):HealthElement
	fun map(healthElement: HealthElement):HealthElementDto

    fun mapToStub(invoice: HealthElement): IcureStubDto
    fun mapFromStub(invoice: IcureStubDto): HealthElement
}
