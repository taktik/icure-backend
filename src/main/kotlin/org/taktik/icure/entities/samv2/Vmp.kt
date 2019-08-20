package org.taktik.icure.entities.samv2

import org.taktik.icure.entities.samv2.embed.*
import java.io.Serializable

class Vmp(
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var name: SamText? = null,
        var abbreviation: SamText? = null,
        var vmpGroupId: String? = null,
        var vtm: Vtm? = null,
        var wadas: List<Wada>? = null,
        components: List<VmpComponent>? = null,
        commentedClassifications: List<CommentedClassification>? = null
) : StoredDocumentWithPeriod(from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Vmp

        if (code != other.code) return false
        if (name != other.name) return false
        if (abbreviation != other.abbreviation) return false
        if (vmpGroupId != other.vmpGroupId) return false
        if (vtm != other.vtm) return false
        if (wadas != other.wadas) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (abbreviation?.hashCode() ?: 0)
        result = 31 * result + (vmpGroupId?.hashCode() ?: 0)
        result = 31 * result + (vtm?.hashCode() ?: 0)
        result = 31 * result + (wadas?.hashCode() ?: 0)
        return result
    }
}
