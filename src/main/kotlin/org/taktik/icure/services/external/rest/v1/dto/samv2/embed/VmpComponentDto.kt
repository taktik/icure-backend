package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import java.io.Serializable

import com.github.pozo.KotlinBuilder
@KotlinBuilder
import com.github.pozo.KotlinBuilder
@KotlinBuilder
data class VmpComponentDto(val code: String? = null,
                           val virtualForm: VirtualFormDto? = null,
                           val routeOfAdministrations: List<RouteOfAdministrationDto>? = null,
                           val name: SamTextDto? = null,
                           val phaseNumber: Short? = null,
                           val virtualIngredients: List<VirtualIngredientDto>? = null) : Serializable
