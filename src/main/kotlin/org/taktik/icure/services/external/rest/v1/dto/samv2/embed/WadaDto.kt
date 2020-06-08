package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import java.io.Serializable

import com.github.pozo.KotlinBuilder
@KotlinBuilder


data class WadaDto(val code: String? = null, val name: SamTextDto? = null, val description: SamTextDto? = null) : Serializable
