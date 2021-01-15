package org.taktik.icure.entities.samv2.embed

import com.fasterxml.jackson.annotation.JsonCreator



class Atc(var code: String? = null, var description: String? = null) : Comparable<Atc> {
    override fun compareTo(other: Atc): Int {
        return if (this == other) {
            0
        } else compareValuesBy(this, other, { it.code }, { it.description }, { System.identityHashCode(it) }).also { if(it==0) throw IllegalStateException("Invalid compareTo implementation") }
    }

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
