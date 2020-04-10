package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.embed.Right
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
class Agenda(id: String,
             rev: String? = null,
             revisionsInfo: Array<RevisionInfo> = arrayOf(),
             conflicts: Array<String> = arrayOf(),
             revHistory: Map<String, String> = mapOf()) : StoredDocument(id, rev, revisionsInfo, conflicts, revHistory), Serializable {
    var name: String? = null
    var userId: String? = null
    var rights: List<Right>? = null

    fun copy(id: String? = null,
             rev: String? = null,
             revisionsInfo: Array<RevisionInfo>? = null,
             conflicts: Array<String>? = null,
             revHistory: Map<String, String>? = null,
             name: String? = null,
             userId: String? = null,
             rights: List<Right>? = null
    ) = Agenda(id ?: this.id, rev ?: this.rev, revisionsInfo ?: this.revisionsInfo, conflicts ?: this.conflicts, revHistory ?: this.revHistory). apply {
        name?.let{ this.name = it }
        userId?.let{ this.userId = it }
        rights?.let{ this.rights = it }
    }

}
