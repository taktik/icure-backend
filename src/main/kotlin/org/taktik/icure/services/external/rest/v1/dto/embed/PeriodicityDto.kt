package org.taktik.icure.services.external.rest.v1.dto.embed

import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import java.io.Serializable

data class PeriodicityDto(
        val relatedCode: CodeStubDto? = null,
        val relatedPeriodicity: CodeStubDto? = null
) : Serializable
