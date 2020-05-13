package org.taktik.icure.entities.samv2.stub

import org.taktik.icure.entities.samv2.embed.NoGenericPrescriptionReason
import org.taktik.icure.entities.samv2.embed.NoSwitchReason
import org.taktik.icure.entities.samv2.embed.SamText
import org.taktik.icure.entities.samv2.embed.StoredDocumentWithPeriod
import java.io.Serializable

class VmpGroupStub(
        var productId: String? = null,
        var id: String? = null,
        var code: String? = null,
        var name: SamText? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VmpGroupStub) return false

        if (id != other.id) return false
        if (code != other.code) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }

}
