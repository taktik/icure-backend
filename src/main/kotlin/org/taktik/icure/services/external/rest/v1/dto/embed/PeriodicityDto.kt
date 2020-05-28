package org.taktik.icure.services.external.rest.v1.dto.embed

import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import java.io.Serializable

@KotlinBuilder
data class PeriodicityDto(
        val relatedCode: CodeStubDto? = null,
        val relatedPeriodicity: CodeStubDto? = null
) : Serializable
