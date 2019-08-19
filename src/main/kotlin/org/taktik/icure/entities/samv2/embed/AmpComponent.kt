package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

class AmpComponent(from: Long? = null,
                   to: Long? = null,
                   var ingredients: List<Ingredient>? = null,
                   var pharmaceuticalForms: List<PharmaceuticalForm>? = null,
                   var routeOfAdministrations: List<RouteOfAdministration>? = null,
                   var dividable: String? = null,
                   var scored: String? = null,
                   var crushable: Crushable? = null,
                   var containsAlcohol: ContainsAlcohol? = null,
                   var isSugarFree: Boolean? = null,
                   var modifiedReleaseType: Int? = null,
                   var specificDrugDevice: Int? = null,
                   var dimensions: String? = null,
                   var name: SamText? = null,
                   var note: SamText? = null) : DataPeriod(from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as AmpComponent

        if (ingredients != other.ingredients) return false
        if (pharmaceuticalForms != other.pharmaceuticalForms) return false
        if (routeOfAdministrations != other.routeOfAdministrations) return false
        if (dividable != other.dividable) return false
        if (scored != other.scored) return false
        if (crushable != other.crushable) return false
        if (containsAlcohol != other.containsAlcohol) return false
        if (isSugarFree != other.isSugarFree) return false
        if (modifiedReleaseType != other.modifiedReleaseType) return false
        if (specificDrugDevice != other.specificDrugDevice) return false
        if (dimensions != other.dimensions) return false
        if (name != other.name) return false
        if (note != other.note) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (ingredients?.hashCode() ?: 0)
        result = 31 * result + (pharmaceuticalForms?.hashCode() ?: 0)
        result = 31 * result + (routeOfAdministrations?.hashCode() ?: 0)
        result = 31 * result + (dividable?.hashCode() ?: 0)
        result = 31 * result + (scored?.hashCode() ?: 0)
        result = 31 * result + (crushable?.hashCode() ?: 0)
        result = 31 * result + (containsAlcohol?.hashCode() ?: 0)
        result = 31 * result + (isSugarFree?.hashCode() ?: 0)
        result = 31 * result + (modifiedReleaseType ?: 0)
        result = 31 * result + (specificDrugDevice ?: 0)
        result = 31 * result + (dimensions?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (note?.hashCode() ?: 0)
        return result
    }
}
