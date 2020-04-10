package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class CalendarItemTag : Serializable {
    var code: String? = null
    var date: Long? = null
    var userId: String? = null
    var userName: String? = null

}
