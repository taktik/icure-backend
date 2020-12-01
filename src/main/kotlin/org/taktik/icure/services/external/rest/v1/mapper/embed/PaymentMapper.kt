package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Payment
import org.taktik.icure.services.external.rest.v1.dto.embed.PaymentDto
@Mapper(componentModel = "spring", uses = [PaymentTypeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PaymentMapper {
	fun map(paymentDto: PaymentDto):Payment
	fun map(payment: Payment):PaymentDto
}
