package org.taktik.icure.entities.embed

import org.taktik.icure.entities.base.EnumVersion

@EnumVersion(1L)
enum class ReceiptBlobType {
    xades, kmehrRequest, kmehrResponse, soapRequest, soapResponse, soapConversation, tack
}
