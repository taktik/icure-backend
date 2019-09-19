package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

class WadaDto(var code: String? = null, var name: SamTextDto? = null, var description: SamTextDto? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WadaDto) return false

        if (code != other.code) return false
        if (name != other.name) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }
}
