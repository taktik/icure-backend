package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

class VmpComponent(var virtualForm: VirtualForm? = null,
                   var routeOfAdministrations: List<RouteOfAdministration>? = null,
                   var name: SamText? = null,
                   var phaseNumber: Short? = null) : DataPeriod(), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as VmpComponent

        if (virtualForm != other.virtualForm) return false
        if (routeOfAdministrations != other.routeOfAdministrations) return false
        if (name != other.name) return false
        if (phaseNumber != other.phaseNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (virtualForm?.hashCode() ?: 0)
        result = 31 * result + (routeOfAdministrations?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (phaseNumber ?: 0)
        return result
    }
}
