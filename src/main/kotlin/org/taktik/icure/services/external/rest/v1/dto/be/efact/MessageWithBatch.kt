package org.taktik.icure.services.external.rest.v1.dto.be.efact

import org.taktik.icure.services.external.rest.v1.dto.MessageDto

class MessageWithBatch {
    var invoicesBatch: InvoicesBatch? = null
    var message: MessageDto? = null
}
