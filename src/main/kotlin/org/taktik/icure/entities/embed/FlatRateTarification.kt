package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class FlatRateTarification(
        val code: String? = null,
        val flatRateType: FlatRateType? = null,
        val label: Map<String, String>? = null,
        val valorisations: List<Valorisation> = listOf(),
        override val encryptedSelf: String? = null
) : Encrypted, Serializable
