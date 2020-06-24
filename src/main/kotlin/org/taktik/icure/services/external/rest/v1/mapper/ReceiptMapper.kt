package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Receipt
import org.taktik.icure.services.external.rest.v1.dto.ReceiptDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.ReceiptBlobTypeMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class, DelegationMapper::class, ReceiptBlobTypeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ReceiptMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(receiptDto: ReceiptDto):Receipt
	fun map(receipt: Receipt):ReceiptDto
}
