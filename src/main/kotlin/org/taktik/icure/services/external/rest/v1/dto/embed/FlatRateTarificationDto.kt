package org.taktik.icure.services.external.rest.v1.dto.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class FlatRateTarificationDto(
        val code: String? = null,
        val flatRateType: FlatRateTypeDto? = null,
        val label: Map<String, String>? = null,
        val valorisations: List<ValorisationDto> = listOf(),
        override val encryptedSelf: String? = null
) : EncryptedDto, Serializable
