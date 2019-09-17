package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

class StandardSubstanceDto(
        var code: String? = null,
        var type : StandardSubstanceTypeDto? = null,
        var name: SamTextDto? = null,
        var definition: SamTextDto? = null,
        var url: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StandardSubstanceDto

        if (code != other.code) return false
        if (type != other.type) return false
        if (name != other.name) return false
        if (definition != other.definition) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code?.hashCode() ?: 0
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (definition?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: 0)
        return result
    }
}
