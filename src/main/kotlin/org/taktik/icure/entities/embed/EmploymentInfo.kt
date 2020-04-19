package org.taktik.icure.entities.embed

import org.taktik.icure.entities.base.CodeStub
import java.io.Serializable

class EmploymentInfo(
        val startDate: Long? = null,
        val endDate: Long? = null,
        val professionType: CodeStub? = null,
        val employer: Employer? = null
) : Serializable
