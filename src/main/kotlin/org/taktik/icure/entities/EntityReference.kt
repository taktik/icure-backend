package org.taktik.icure.entities

import org.taktik.icure.entities.base.StoredDocument
import java.util.Objects

class EntityReference : StoredDocument() {
    var docId: String? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as EntityReference
        return docId == that.docId
    }

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), docId)
    }
}
