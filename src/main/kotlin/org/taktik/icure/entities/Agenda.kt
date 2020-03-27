package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.StoredDocument
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
class Agenda : StoredDocument(), Serializable {
    var name: String? = null
    var userId: String? = null
    var rights: List<Right>? = null

}
