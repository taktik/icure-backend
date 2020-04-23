package org.taktik.icure.services.external.rest.v1.dto

import org.taktik.icure.dto.message.Attachment
import java.util.*

class ImportResultDto {
    var patient: PatientDto? = null
    var hes: List<HealthElementDto>? = null
    var ctcs: List<ContactDto>? = null
    var warnings: List<String>? = null
    var errors: List<String>? = null
    var forms: List<FormDto>? = null
    var hcps: List<HealthcarePartyDto>? = null
    var documents: List<DocumentDto>? = null

    var attachments: HashMap<String, Attachment>? = null

}
