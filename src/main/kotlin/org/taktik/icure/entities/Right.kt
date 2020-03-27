package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
class Right : Serializable {
    var userId: String? = null
    var isRead = false
    var isWrite = false
    var isAdministration = false

}
