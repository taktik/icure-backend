package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import java.io.Serializable

data class VmpComponentDto(val code: String? = null,
                           val virtualForm: VirtualFormDto? = null,
                           val routeOfAdministrations: List<RouteOfAdministrationDto>? = null,
                           val name: SamTextDto? = null,
                           val phaseNumber: Short? = null,
                           val virtualIngredients: List<VirtualIngredientDto>? = null) : Serializable
