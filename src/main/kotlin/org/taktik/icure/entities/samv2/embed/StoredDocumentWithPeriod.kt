package org.taktik.icure.entities.samv2.embed

import org.taktik.icure.entities.base.StoredDocument

open class StoredDocumentWithPeriod(
        var from: Long? = null,
        var to: Long? = null
) : StoredDocument() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as StoredDocumentWithPeriod

        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (from?.hashCode() ?: 0)
        result = 31 * result + (to?.hashCode() ?: 0)
        return result
    }
}
