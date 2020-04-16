package org.taktik.icure.entities.samv2.embed

import org.taktik.icure.entities.base.StoredDocument

interface StoredDocumentWithPeriod : StoredDocument {
    var from: Long?
    var to: Long?
}
