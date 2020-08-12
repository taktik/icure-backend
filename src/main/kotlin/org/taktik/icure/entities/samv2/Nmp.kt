package org.taktik.icure.entities.samv2

import org.taktik.icure.entities.samv2.embed.*

class Nmp(
        id: String? = null,
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var category: String? = null,
        var commercialStatus: String? = null,
        var name: SamText? = null,
        var producer: SamText? = null,
        var distributor: SamText? = null
) : StoredDocumentWithPeriod(id, from, to) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Nmp) return false
        if (!super.equals(other)) return false

        if (code != other.code) return false
        if (category != other.category) return false
        if (commercialStatus != other.commercialStatus) return false
        if (name != other.name) return false
        if (producer != other.producer) return false
        if (distributor != other.distributor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (category?.hashCode() ?: 0)
        result = 31 * result + (commercialStatus?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (producer?.hashCode() ?: 0)
        result = 31 * result + (distributor?.hashCode() ?: 0)
        return result
    }
}

