package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.Content

@JsonInclude(JsonInclude.Include.NON_NULL)
class Article : StoredICureDocument() {
    var name: String? = null
    var content: List<Content>? = null
    var classification: String? = null

}
