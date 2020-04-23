package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

data class CommercializationDto(
        override val from: Long? = null,
        override val to: Long? = null
) : DataPeriodDto
