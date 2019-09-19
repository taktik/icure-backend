package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.io.Serializable

class AmppComponentDto(
        from: Long? = null,
        to: Long? = null,
        var contentType: ContentTypeDto? = null,
        var contentMultiplier: Int? = null,
        var packSpecification:String? = null,
        var deviceType: DeviceTypeDto? = null,
        var packagingType: PackagingTypeDto? = null
)  : DataPeriodDto(from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AmppComponentDto) return false
        if (!super.equals(other)) return false

        if (contentType != other.contentType) return false
        if (contentMultiplier != other.contentMultiplier) return false
        if (packSpecification != other.packSpecification) return false
        if (deviceType != other.deviceType) return false
        if (packagingType != other.packagingType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (contentType?.hashCode() ?: 0)
        result = 31 * result + (contentMultiplier ?: 0)
        result = 31 * result + (packSpecification?.hashCode() ?: 0)
        result = 31 * result + (deviceType?.hashCode() ?: 0)
        result = 31 * result + (packagingType?.hashCode() ?: 0)
        return result
    }
}
