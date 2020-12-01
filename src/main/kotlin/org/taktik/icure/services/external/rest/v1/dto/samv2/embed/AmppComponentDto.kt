package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AmppComponentDto(
        override val from: Long? = null,
        override val to: Long? = null,
        val contentType: ContentTypeDto? = null,
        val contentMultiplier: Int? = null,
        val packSpecification: String? = null,
        val deviceType: DeviceTypeDto? = null,
        val packagingType: PackagingTypeDto? = null
) : DataPeriodDto
