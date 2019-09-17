package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.io.Serializable

class VmpComponentDto(var code: String? = null,
                      var virtualForm: VirtualFormDto? = null,
                      var routeOfAdministrations: List<RouteOfAdministrationDto>? = null,
                      var name: SamTextDto? = null,
                      var phaseNumber: Short? = null,
                      var virtualIngredients: List<VirtualIngredientDto>? = null) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VmpComponentDto

        if (code != other.code) return false
        if (virtualForm != other.virtualForm) return false
        if (routeOfAdministrations != other.routeOfAdministrations) return false
        if (name != other.name) return false
        if (phaseNumber != other.phaseNumber) return false
        if (virtualIngredients != other.virtualIngredients) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code?.hashCode() ?: 0
        result = 31 * result + (virtualForm?.hashCode() ?: 0)
        result = 31 * result + (routeOfAdministrations?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (phaseNumber ?: 0)
        result = 31 * result + (virtualIngredients?.hashCode() ?: 0)
        return result
    }

}
