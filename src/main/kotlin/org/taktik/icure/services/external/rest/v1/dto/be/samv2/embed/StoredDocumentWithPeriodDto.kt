package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import org.taktik.icure.entities.base.StoredDocument

open class StoredDocumentWithPeriodDto(
        id: String,
        var from: Long? = null,
        var to: Long? = null
) : StoredDocument(id, "", _type = "", revHistory = null)
