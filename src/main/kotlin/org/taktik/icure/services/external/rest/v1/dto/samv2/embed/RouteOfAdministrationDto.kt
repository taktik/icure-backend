package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import java.io.Serializable

data class RouteOfAdministrationDto(val name: SamTextDto? = null, val standardRoutes: List<CodeStubDto> = listOf()) : Serializable
