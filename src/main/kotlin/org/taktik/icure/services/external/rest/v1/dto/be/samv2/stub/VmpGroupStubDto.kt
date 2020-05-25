package org.taktik.icure.services.external.rest.v1.dto.be.samv2.stub

import org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed.SamTextDto
import java.io.Serializable

class VmpGroupStubDto(
        var productId: String? = null,
        var id: String? = null,
        var code: String? = null,
        var name: SamTextDto? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VmpGroupStubDto) return false

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
