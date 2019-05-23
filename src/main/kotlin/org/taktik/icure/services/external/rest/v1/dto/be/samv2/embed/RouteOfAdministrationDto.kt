package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import org.taktik.icure.entities.base.Code

class RouteOfAdministrationDto(var name: SamTextDto? = null, var standardRoutes: List<Code> = listOf())
