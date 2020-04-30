package org.taktik.icure.entities.samv2.embed

import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.CodeStub
import java.io.Serializable

data class RouteOfAdministration(val name: SamText? = null, val standardRoutes: List<CodeStub> = listOf()) : Serializable
