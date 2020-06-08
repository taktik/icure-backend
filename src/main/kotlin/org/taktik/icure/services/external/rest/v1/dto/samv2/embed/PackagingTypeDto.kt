package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import java.io.Serializable

import com.github.pozo.KotlinBuilder
@KotlinBuilder


data class PackagingTypeDto(
        val code: String? = null,
        val name: SamTextDto? = null,
        val edqmCode: String? = null,
        val edqmDefinition: String? = null
) : Serializable
