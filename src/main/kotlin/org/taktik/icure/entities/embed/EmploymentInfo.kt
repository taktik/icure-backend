package org.taktik.icure.entities.embed

import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.base.CodeStub
import java.io.Serializable

@KotlinBuilder
data class EmploymentInfo(
        val startDate: Long? = null,
        val endDate: Long? = null,
        val professionType: CodeStub? = null,
        val employer: Employer? = null
) : Serializable
