package org.taktik.icure.entities

import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo
import java.util.Objects

class EntityReference(id: String,
                      rev: String? = null,
                      revisionsInfo: Array<RevisionInfo> = arrayOf(),
                      conflicts: Array<String> = arrayOf(),
                      revHistory: Map<String, String> = mapOf()) : StoredDocument(id, rev, revisionsInfo, conflicts, revHistory) {
    var docId: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false
        val that = other as EntityReference
        return docId == that.docId
    }

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), docId)
    }
}
