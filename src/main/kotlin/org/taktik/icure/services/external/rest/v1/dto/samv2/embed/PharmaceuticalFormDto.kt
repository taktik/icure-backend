package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import java.io.Serializable

import com.github.pozo.KotlinBuilder
@KotlinBuilder


data class PharmaceuticalFormDto(val code: String? = null, val name: SamTextDto? = null, val standardForms: List<CodeStubDto> = listOf()) : Serializable
