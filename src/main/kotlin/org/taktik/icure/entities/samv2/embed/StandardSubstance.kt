package org.taktik.icure.entities.samv2.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class StandardSubstance(
        val code: String? = null,
        val type: StandardSubstanceType? = null,
        val name: SamText? = null,
        val definition: SamText? = null,
        val url: String? = null
) : Serializable
