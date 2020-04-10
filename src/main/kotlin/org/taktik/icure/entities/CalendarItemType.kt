package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo
import java.io.Serializable
import java.util.HashMap

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class CalendarItemType(id: String,
                       rev: String? = null,
                       revisionsInfo: Array<RevisionInfo> = arrayOf(),
                       conflicts: Array<String> = arrayOf(),
                       revHistory: Map<String, String> = mapOf()) : StoredDocument(id, rev, revisionsInfo, conflicts, revHistory), Serializable {
    //mikrono API: Put /rest/appointmentTypeResource
    var name: String? = null
    var color //"#123456"
            : String? = null
    var duration = 0 // mikrono: int durationInMinutes; = 0
    var externalRef // same as topaz Id, to be used by mikrono
            : String? = null
    var mikronoId: String? = null
    var docIds: List<String>? = null
    var otherInfos = HashMap<String, String>()
    var subjectByLanguage = HashMap<String, String>()

}
