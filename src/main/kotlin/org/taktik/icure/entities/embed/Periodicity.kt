package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.ValidCode
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Periodicity(
        @field:ValidCode(autoFix = AutoFix.NORMALIZECODE) val relatedCode: CodeStub? = null,
        @field:ValidCode(autoFix = AutoFix.NORMALIZECODE) val relatedPeriodicity: CodeStub? = null
) : Serializable
