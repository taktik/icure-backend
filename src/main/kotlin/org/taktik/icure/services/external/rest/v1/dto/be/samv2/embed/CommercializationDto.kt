package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.io.Serializable

class CommercializationDto(
        from: Long? = null,
        to: Long? = null
) : DataPeriodDto(from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false
        return true
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
