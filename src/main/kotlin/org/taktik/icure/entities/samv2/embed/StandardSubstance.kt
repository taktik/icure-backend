package org.taktik.icure.entities.samv2.embed

class StandardSubstance(
        var code: String? = null,
        var type : StandardSubstanceType? = null,
        var name: SamText? = null,
        var definition: SamText? = null,
        var url: String? = null
) : Comparable<StandardSubstance> {
    override fun compareTo(other: StandardSubstance): Int {
        return if (this == other) {
            0
        } else compareValuesBy(this, other, { it.type }, { it.code })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StandardSubstance

        if (code != other.code) return false
        if (type != other.type) return false
        if (name != other.name) return false
        if (definition != other.definition) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code?.hashCode() ?: 0
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (definition?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: 0)
        return result
    }
}
