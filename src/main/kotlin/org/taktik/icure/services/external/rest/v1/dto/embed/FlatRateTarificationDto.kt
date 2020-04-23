package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable


data class FlatRateTarificationDto(
        val code: String? = null,
        val flatRateType: FlatRateTypeDto? = null,
        val label: Map<String, String>? = null,
        val valorisations: List<ValorisationDto> = listOf(),
        override val encryptedSelf: String? = null
) : EncryptedDto, Serializable
