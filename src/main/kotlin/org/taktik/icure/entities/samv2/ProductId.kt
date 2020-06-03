package org.taktik.icure.entities.samv2

import org.taktik.icure.entities.base.StoredDocument

class ProductId(
        id: String? = null,
        var productId: String? = null
) : StoredDocument(id) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductId) return false
        if (!super.equals(other)) return false

        if (productId != other.productId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (productId?.hashCode() ?: 0)
        return result
    }
}
