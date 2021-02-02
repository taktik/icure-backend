package org.taktik.icure.entities.samv2.embed

import org.taktik.icure.entities.base.StoredDocument
import java.util.*

class Substance(
        id: String? = null,
        var code: String? = null,
        var chemicalForm: String? = null,
        var name: SamText? = null,
        var note: SamText? = null,
        var standardSubstances: Set<StandardSubstance>? = null
) : StoredDocument(id), Comparable<Substance> {
    override fun compareTo(other: Substance): Int {
        return if (this == other) {
            0
        } else compareValuesBy(this, other, { it.id }, { it.code }, { System.identityHashCode(it) }).also { if(it==0) throw IllegalStateException("Invalid compareTo implementation") }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Substance

        if (code != other.code) return false
        if (chemicalForm != other.chemicalForm) return false
        if (name != other.name) return false
        if (note != other.note) return false
        if (standardSubstances != other.standardSubstances) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (chemicalForm?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (note?.hashCode() ?: 0)
        result = 31 * result + (standardSubstances?.hashCode() ?: 0)
        return result
    }

}
