package org.taktik.icure.entities.samv2.stub

import org.taktik.icure.entities.samv2.VmpGroup
import org.taktik.icure.entities.samv2.embed.*
import java.io.Serializable

class VmpStub(
        var id: String? = null,
        var code: String? = null,
        var vmpGroup: VmpGroupStub? = null,
        var name: SamText? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VmpStub) return false

        if (id != other.id) return false
        if (code != other.code) return false
        if (vmpGroup != other.vmpGroup) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (vmpGroup?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }

}
