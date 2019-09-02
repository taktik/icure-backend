package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

class Commercialization(
        from: Long? = null,
        to: Long? = null
) : DataPeriod(from, to), Serializable {
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
