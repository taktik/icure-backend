package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.validation.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
class Classification(id: String,
                     val templateId: String,
                     rev: String? = null,
                     revisionsInfo: Array<RevisionInfo> = arrayOf(),
                     conflicts: Array<String> = arrayOf(),
                     revHistory: Map<String, String> = mapOf()) : ClassificationTemplate(id, rev, revisionsInfo, conflicts, revHistory) {
}
