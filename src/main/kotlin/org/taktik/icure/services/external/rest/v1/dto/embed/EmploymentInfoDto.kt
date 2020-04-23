package org.taktik.icure.services.external.rest.v1.dto.embed

import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import java.io.Serializable

class EmploymentInfoDto(
        val startDate: Long? = null,
        val endDate: Long? = null,
        val professionType: CodeStubDto? = null,
        val employer: EmployerDto? = null
) : Serializable
