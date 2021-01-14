package org.taktik.icure.entities.samv2.embed

import org.taktik.icure.entities.base.Code
import java.util.*

class RouteOfAdministration(var name: SamText? = null, var standardRoutes: SortedSet<Code> = sortedSetOf()) : Comparable<RouteOfAdministration> {
    override fun compareTo(other: RouteOfAdministration): Int {
        return if (this == other) {
            0
        } else compareValuesBy(this, other, { it.name }, { it.hashCode() }).also { if(it==0) throw IllegalStateException("Invalid compareTo implementation") }
    }

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
