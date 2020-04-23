package org.taktik.icure.entities.samv2.embed

data class AmppComponent(
        override val from: Long? = null,
        override val to: Long? = null,
        val contentType: ContentType? = null,
        val contentMultiplier: Int? = null,
        val packSpecification: String? = null,
        val deviceType: DeviceType? = null,
        val packagingType: PackagingType? = null
) : DataPeriod
