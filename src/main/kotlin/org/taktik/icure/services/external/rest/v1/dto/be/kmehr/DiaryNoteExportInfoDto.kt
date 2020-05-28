package org.taktik.icure.services.external.rest.v1.dto.be.kmehr

import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto
import java.io.Serializable

class DiaryNoteExportInfoDto : Serializable {
    var secretForeignKeys: List<String> = listOf()
    var excludedIds: List<String> = listOf()
    var recipient: HealthcarePartyDto? = null
    var softwareName: String? = null
    var softwareVersion: String? = null
    var tags: List<String> = listOf()
    var contexts: List<String> = listOf()
    var psy: Boolean? = null
    var documentId: String? = null
    var attachmentId: String? = null
    var note: String? = null
}
