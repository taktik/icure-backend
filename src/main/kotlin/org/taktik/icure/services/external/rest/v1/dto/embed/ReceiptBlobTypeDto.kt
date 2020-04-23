package org.taktik.icure.services.external.rest.v1.dto.embed

import org.taktik.icure.services.external.rest.v1.dto.base.EnumVersionDto

@EnumVersionDto(1L)
enum class ReceiptBlobTypeDto {
    xades, kmehrRequest, kmehrResponse, soapRequest, soapResponse, soapConversation, tack
}
