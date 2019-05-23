package org.taktik.icure.entities.samv2.embed

import org.taktik.icure.entities.base.StoredDocument

open class StoredDocumentWithPeriod(
        var from: Long? = null,
        var to: Long? = null
) : StoredDocument()
