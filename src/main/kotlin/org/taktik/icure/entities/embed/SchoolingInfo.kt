package org.taktik.icure.entities.embed

import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.base.CodeStub
import java.io.Serializable

@KotlinBuilder
data class SchoolingInfo(
        val startDate: Long? = null,
        val endDate: Long? = null,
        val school: String? = null,
        val typeOfEducation: CodeStub? = null
) : Serializable
