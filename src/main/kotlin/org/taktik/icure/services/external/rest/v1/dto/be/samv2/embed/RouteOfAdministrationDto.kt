package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import org.taktik.icure.entities.base.Code

class RouteOfAdministrationDto(var name: SamTextDto? = null, var standardRoutes: List<Code> = listOf()) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RouteOfAdministrationDto) return false

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
