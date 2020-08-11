package org.taktik.icure.entities.samv2.embed

import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.entities.base.StoredDocument

class PharmaceuticalForm(id: String? = null, var code: String? = null, var name: SamText? = null, var standardForms: List<Code> = listOf()) : StoredDocument(id) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PharmaceuticalForm) return false
        if (!super.equals(other)) return false

        if (code != other.code) return false
        if (name != other.name) return false
        if (standardForms != other.standardForms) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + standardForms.hashCode()
        return result
    }
}
