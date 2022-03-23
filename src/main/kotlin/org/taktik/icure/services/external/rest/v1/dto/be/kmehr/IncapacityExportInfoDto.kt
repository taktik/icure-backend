package org.taktik.icure.services.external.rest.v1.dto.be.kmehr

import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto
import java.io.Serializable

class IncapacityExportInfoDto : Serializable {
    var secretForeignKeys: List<String> = emptyList()
    var services: List<ServiceDto> = emptyList()
    var serviceAuthors: List<HealthcarePartyDto>? = null
    var recipient: HealthcarePartyDto? = null
    var comment: String? = null
    var incapacityId: String = ""
    var retraction: Boolean = false
    var dataset: String = ""

}
