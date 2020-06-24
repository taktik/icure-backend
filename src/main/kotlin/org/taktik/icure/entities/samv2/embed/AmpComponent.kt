package org.taktik.icure.entities.samv2.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AmpComponent(
        override val from: Long? = null,
        override val to: Long? = null,
        val ingredients: List<Ingredient>? = null,
        val pharmaceuticalForms: List<PharmaceuticalForm>? = null,
        val routeOfAdministrations: List<RouteOfAdministration>? = null,
        val dividable: String? = null,
        val scored: String? = null,
        val crushable: Crushable? = null,
        val containsAlcohol: ContainsAlcohol? = null,
        val sugarFree: Boolean? = null,
        val modifiedReleaseType: Int? = null,
        val specificDrugDevice: Int? = null,
        val dimensions: String? = null,
        val name: SamText? = null,
        val note: SamText? = null
) : DataPeriod
