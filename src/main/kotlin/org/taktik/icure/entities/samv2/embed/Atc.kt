package org.taktik.icure.entities.samv2.embed

import com.fasterxml.jackson.annotation.JsonCreator



class Atc(var code: String? = null, var description: String? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Atc

        if (code != other.code) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code?.hashCode() ?: 0
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }
}
