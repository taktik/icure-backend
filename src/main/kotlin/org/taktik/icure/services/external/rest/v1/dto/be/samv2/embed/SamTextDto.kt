package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.io.Serializable

class SamTextDto(
        var fr: String? = null,
        var nl: String? = null,
        var de: String? = null,
        var en: String? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SamTextDto) return false

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
