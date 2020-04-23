package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

data class VmpComponent(val code: String? = null,
                        val virtualForm: VirtualForm? = null,
                        val routeOfAdministrations: List<RouteOfAdministration>? = null,
                        val name: SamText? = null,
                        val phaseNumber: Short? = null,
                        val virtualIngredients: List<VirtualIngredient>? = null) : Serializable
