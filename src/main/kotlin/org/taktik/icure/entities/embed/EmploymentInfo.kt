package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.base.CodeStub
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class EmploymentInfo(
        val startDate: Long? = null,
        val endDate: Long? = null,
        val professionType: CodeStub? = null,
        val employer: Employer? = null
) : Serializable
