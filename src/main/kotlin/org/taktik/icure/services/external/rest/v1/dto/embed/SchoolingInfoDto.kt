package org.taktik.icure.services.external.rest.v1.dto.embed

import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import java.io.Serializable
import com.github.pozo.KotlinBuilder
import com.github.pozo.KotlinBuilder
@KotlinBuilder
data class SchoolingInfoDto(
        val startDate: Long? = null,
        val endDate: Long? = null,
        val school: String? = null,
        val typeOfEducation: CodeStubDto? = null
) : Serializable
