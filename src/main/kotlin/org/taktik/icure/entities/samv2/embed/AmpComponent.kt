package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

class AmpComponent(var pharmaceuticalForms: List<PharmaceuticalForm>? = null,
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
                   var note: SamText? = null) : DataPeriod(), Serializable
