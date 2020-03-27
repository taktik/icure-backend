package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.StoredDocument
import java.io.Serializable
import java.util.HashMap

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class CalendarItemType : StoredDocument(), Serializable {
    //mikrono API: Put /rest/appointmentTypeResource
    var name: String? = null
    var color //"#123456"
            : String? = null
    var duration // mikrono: int durationInMinutes; = 0
    var externalRef // same as topaz Id, to be used by mikrono
            : String? = null
    var mikronoId: String? = null
    var docIds: List<String>? = null
    var otherInfos = HashMap<String, String>()
    var subjectByLanguage = HashMap<String, String>()

}
