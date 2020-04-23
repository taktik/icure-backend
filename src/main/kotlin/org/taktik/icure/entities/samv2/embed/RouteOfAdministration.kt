package org.taktik.icure.entities.samv2.embed

import org.taktik.icure.entities.base.Code
import java.io.Serializable

data class RouteOfAdministration(val name: SamText? = null, val standardRoutes: List<Code> = listOf()) : Serializable
