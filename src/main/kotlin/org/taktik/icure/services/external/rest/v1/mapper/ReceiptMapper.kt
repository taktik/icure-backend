package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Receipt
import org.taktik.icure.services.external.rest.v1.dto.ReceiptDto
@Mapper
interface ReceiptMapper {
	fun map(receiptDto: ReceiptDto):Receipt
	fun map(receipt: Receipt):ReceiptDto
}
