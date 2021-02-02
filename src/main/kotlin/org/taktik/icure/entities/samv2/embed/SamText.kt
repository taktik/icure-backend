package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

class SamText(
        var fr: String? = null,
        var nl: String? = null,
        var de: String? = null,
        var en: String? = null
) : Serializable, Comparable<SamText> {
    override fun compareTo(other: SamText): Int {
        return if (this == other) {
            0
        } else compareValuesBy(this, other, { it.fr }, { it.nl }, { it.de }, { it.en }, { System.identityHashCode(it) }).also { if(it==0) throw IllegalStateException("Invalid compareTo implementation") }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SamText

        if (fr != other.fr) return false
        if (nl != other.nl) return false
        if (de != other.de) return false
        if (en != other.en) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fr?.hashCode() ?: 0
        result = 31 * result + (nl?.hashCode() ?: 0)
        result = 31 * result + (de?.hashCode() ?: 0)
        result = 31 * result + (en?.hashCode() ?: 0)
        return result
    }
}
