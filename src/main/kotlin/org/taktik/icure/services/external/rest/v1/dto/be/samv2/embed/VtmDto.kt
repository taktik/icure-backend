package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.io.Serializable

class VtmDto(
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var name: SamTextDto? = null
) : DataPeriodDto(from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VtmDto) return false
        if (!super.equals(other)) return false

        if (code != other.code) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }
}
