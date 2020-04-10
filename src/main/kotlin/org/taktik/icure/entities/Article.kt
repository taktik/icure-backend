package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.RevisionInfo

@JsonInclude(JsonInclude.Include.NON_NULL)
class Article(id: String,
              rev: String? = null,
              revisionsInfo: Array<RevisionInfo> = arrayOf(),
              conflicts: Array<String> = arrayOf(),
              revHistory: Map<String, String> = mapOf()) : StoredICureDocument(id, rev, revisionsInfo, conflicts, revHistory) {
    var name: String? = null
    var content: List<Content>? = null
    var classification: String? = null

}
