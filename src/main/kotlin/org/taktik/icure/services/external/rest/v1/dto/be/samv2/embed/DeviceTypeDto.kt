package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.io.Serializable

class DeviceTypeDto(
        var code: String? = null,
        var name: SamTextDto? = null,
        var edqmCode: String? = null,
        var edqmDefinition: String? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceTypeDto

        if (code != other.code) return false
        if (name != other.name) return false
        if (edqmCode != other.edqmCode) return false
        if (edqmDefinition != other.edqmDefinition) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (edqmCode?.hashCode() ?: 0)
        result = 31 * result + (edqmDefinition?.hashCode() ?: 0)
        return result
    }
}
