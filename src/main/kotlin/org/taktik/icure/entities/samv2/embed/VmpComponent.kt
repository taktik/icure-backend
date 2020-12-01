package org.taktik.icure.entities.samv2.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class VmpComponent(val code: String? = null,
                        val virtualForm: VirtualForm? = null,
                        val routeOfAdministrations: List<RouteOfAdministration>? = null,
                        val name: SamText? = null,
                        val phaseNumber: Short? = null,
                        val virtualIngredients: List<VirtualIngredient>? = null) : Serializable
