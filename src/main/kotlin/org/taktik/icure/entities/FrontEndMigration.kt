package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.FrontEndMigrationStatus
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
class FrontEndMigration : StoredDocument, Identifiable<String?>, Cloneable, Serializable {
    var name: String? = null
    var startDate: Long? = null
    var endDate: Long? = null
    var status: FrontEndMigrationStatus? = null
    var logs: String? = null
    var userId: String? = null
    var startKey: String? = null
    var startKeyDocId: String? = null
    var processCount: Long? = null

    constructor() {}
    constructor(name: String?, userId: String?, startDate: Long?, endDate: Long?, status: FrontEndMigrationStatus?, logs: String?) {
        this.name = name
        this.startDate = startDate
        this.endDate = endDate
        this.status = status
        this.logs = logs
        this.userId = userId
    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val frontEndMigration = o as FrontEndMigration
        return if (if (id != null) id != frontEndMigration.id else frontEndMigration.id != null) false else true
    }

    override fun hashCode(): Int {
        return if (id != null) id.hashCode() else 0
    }

    companion object {
        const val serialVersionUID = 1L
    }
}
