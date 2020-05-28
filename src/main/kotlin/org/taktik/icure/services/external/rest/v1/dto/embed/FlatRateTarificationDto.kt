package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable
import com.github.pozo.KotlinBuilder
import com.github.pozo.KotlinBuilder
@KotlinBuilder
data class FlatRateTarificationDto(
        val code: String? = null,
        val flatRateType: FlatRateTypeDto? = null,
        val label: Map<String, String>? = null,
        val valorisations: List<ValorisationDto> = listOf(),
        override val encryptedSelf: String? = null
) : EncryptedDto, Serializable
