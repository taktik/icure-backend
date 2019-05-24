package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.io.Serializable

class AmpComponentDto : DataPeriodDto(), Serializable {
    var pharmaceuticalForms: List<PharmaceuticalFormDto>? = null
    var routeOfAdministrations: List<RouteOfAdministrationDto>? = null
    var dividable: String? = null
    var scored: String? = null
    var crushable: CrushableDto? = null
    var containsAlcohol: ContainsAlcoholDto? = null
    var isSugarFree: Boolean? = null
    var modifiedReleaseType: Int? = null
    var specificDrugDevice: Int? = null
    var dimensions: String? = null
    var name: SamTextDto? = null
    var note: SamTextDto? = null
}
