package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.FrontEndMigrationStatus
import org.taktik.icure.entities.embed.RevisionInfo
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
class FrontEndMigration(id: String,
                        rev: String? = null,
                        revisionsInfo: Array<RevisionInfo> = arrayOf(),
                        conflicts: Array<String> = arrayOf(),
                        revHistory: Map<String, String> = mapOf()) : StoredDocument(id, rev, revisionsInfo, conflicts, revHistory), Identifiable<String>, Cloneable, Serializable {
    var name: String? = null
    var startDate: Long? = null
    var endDate: Long? = null
    var status: FrontEndMigrationStatus? = null
    var logs: String? = null
    var userId: String? = null
    var startKey: String? = null
    var startKeyDocId: String? = null
    var processCount: Long? = null

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val frontEndMigration = other as FrontEndMigration
        return if (if (id != null) id != frontEndMigration.id else frontEndMigration.id != null) false else true
    }

    override fun hashCode(): Int {
        return if (id != null) id.hashCode() else 0
    }

    companion object {
        const val serialVersionUID = 1L
    }
}
