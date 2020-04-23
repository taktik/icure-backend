package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

data class VtmDto(
        override val from: Long? = null,
        override val to: Long? = null,
        val code: String? = null,
        val name: SamTextDto? = null
) : DataPeriodDto
