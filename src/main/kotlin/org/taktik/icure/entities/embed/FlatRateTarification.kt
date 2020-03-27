package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class FlatRateTarification : Serializable {
    var code: String? = null
    var flatRateType: FlatRateType? = null
    var label: Map<String, String>? = null
    var valorisations: Set<Valorisation>? = null

}
