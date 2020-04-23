package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

data class AmppComponentDto(
        override val from: Long? = null,
        override val to: Long? = null,
        val contentType: ContentTypeDto? = null,
        val contentMultiplier: Int? = null,
        val packSpecification: String? = null,
        val deviceType: DeviceTypeDto? = null,
        val packagingType: PackagingTypeDto? = null
) : DataPeriodDto
