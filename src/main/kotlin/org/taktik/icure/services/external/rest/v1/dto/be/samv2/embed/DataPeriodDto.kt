package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.io.Serializable

open class DataPeriodDto(
        var from: Long? = null,
        var to: Long? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataPeriodDto

        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int {
        var result = from?.hashCode() ?: 0
        result = 31 * result + (to?.hashCode() ?: 0)
        return result
    }
}
