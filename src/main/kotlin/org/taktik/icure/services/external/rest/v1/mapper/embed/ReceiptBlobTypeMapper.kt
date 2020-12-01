package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.ReceiptBlobType
import org.taktik.icure.services.external.rest.v1.dto.embed.ReceiptBlobTypeDto
@Mapper(componentModel = "spring")
interface ReceiptBlobTypeMapper {
	fun map(receiptBlobTypeDto: ReceiptBlobTypeDto):ReceiptBlobType
	fun map(receiptBlobType: ReceiptBlobType):ReceiptBlobTypeDto
}
