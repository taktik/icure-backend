package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

class Vtm(
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var name: SamText? = null
) : DataPeriod(from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Vtm

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
