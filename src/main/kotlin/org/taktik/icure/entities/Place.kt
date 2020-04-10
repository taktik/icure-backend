package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.RevisionInfo
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
class Place(id: String,
            rev: String? = null,
            revisionsInfo: Array<RevisionInfo> = arrayOf(),
            conflicts: Array<String> = arrayOf(),
            revHistory: Map<String, String> = mapOf()) : StoredDocument(id, rev, revisionsInfo, conflicts, revHistory), Serializable {
    var name: String? = null
    var address: Address? = null

}
