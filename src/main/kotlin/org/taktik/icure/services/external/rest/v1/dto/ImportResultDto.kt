package org.taktik.icure.services.external.rest.v1.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.base.MimeAttachmentDto
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ImportResultDto(
        val patient: PatientDto? = null,
        val hes: List<HealthElementDto>? = null,
        val ctcs: List<ContactDto>? = null,
        val warnings: List<String>? = null,
        val errors: List<String>? = null,
        val forms: List<FormDto>? = null,
        val hcps: List<HealthcarePartyDto>? = null,
        val documents: List<DocumentDto>? = null,
        val attachments: HashMap<String, MimeAttachmentDto>? = null
)
