package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import java.io.Serializable

data class VirtualFormDto(val name: SamTextDto? = null, val standardForms: List<CodeStubDto> = listOf()) : Serializable
