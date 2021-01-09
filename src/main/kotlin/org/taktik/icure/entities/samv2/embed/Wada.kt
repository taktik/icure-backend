package org.taktik.icure.entities.samv2.embed

class Wada(var code: String? = null, var name: SamText? = null, var description: SamText? = null) : Comparable<Wada> {
    override fun compareTo(other: Wada): Int {
        return if (this == other) {
            0
        } else compareValuesBy(this, other, { it.code }, { it.name })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Wada) return false

        if (code != other.code) return false
        if (name != other.name) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }
}
