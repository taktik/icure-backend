package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PaymentType
import org.taktik.icure.services.external.rest.v1.dto.embed.PaymentTypeDto
@Mapper(componentModel = "spring")
interface PaymentTypeMapper {
	fun map(paymentTypeDto: PaymentTypeDto):PaymentType
	fun map(paymentType: PaymentType):PaymentTypeDto
}
