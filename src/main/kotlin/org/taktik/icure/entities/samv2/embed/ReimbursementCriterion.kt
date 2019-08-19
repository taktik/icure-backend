package org.taktik.icure.entities.samv2.embed

class ReimbursementCriterion(var category: String? = null, var code:String? = null, var description: SamText? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReimbursementCriterion

        if (category != other.category) return false
        if (code != other.code) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = category?.hashCode() ?: 0
        result = 31 * result + code.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }
}
