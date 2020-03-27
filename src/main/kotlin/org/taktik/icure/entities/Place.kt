package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.Address
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
class Place : StoredDocument(), Serializable {
    var name: String? = null
    var address: Address? = null

}
