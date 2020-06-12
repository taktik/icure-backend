package org.taktik.icure.entities.samv2

import org.taktik.icure.entities.samv2.embed.NoGenericPrescriptionReason
import org.taktik.icure.entities.samv2.embed.NoSwitchReason
import org.taktik.icure.entities.samv2.embed.SamText
import org.taktik.icure.entities.samv2.embed.StoredDocumentWithPeriod
import java.io.Serializable

class VmpGroup(
        id: String? = null,
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var name: SamText? = null,
        var noGenericPrescriptionReason: NoGenericPrescriptionReason? = null,
        var noSwitchReason: NoSwitchReason? = null
) : StoredDocumentWithPeriod(id, from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VmpGroup) return false
        if (!super.equals(other)) return false

        if (code != other.code) return false
        if (name != other.name) return false
        if (noGenericPrescriptionReason != other.noGenericPrescriptionReason) return false
        if (noSwitchReason != other.noSwitchReason) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (noGenericPrescriptionReason?.hashCode() ?: 0)
        result = 31 * result + (noSwitchReason?.hashCode() ?: 0)
        return result
    }
}
