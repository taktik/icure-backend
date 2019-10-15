package org.taktik.icure.entities.samv2.embed

import org.taktik.icure.entities.base.Code

class RouteOfAdministration(var name: SamText? = null, var standardRoutes: List<Code> = listOf()) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RouteOfAdministration

        if (name != other.name) return false
        if (standardRoutes != other.standardRoutes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + standardRoutes.hashCode()
        return result
    }
}
