package org.taktik.icure.services.external.rest.v1.dto.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class IdentityDocumentReaderDto(
        val justificatifDocumentNumber: String? = null,
        val supportSerialNumber: String? = null,
        val timeReadingEIdDocument: Long? = null,
        val eidDocumentSupportType: Int = 0,
        val reasonManualEncoding: Int = 0,
        val reasonUsingVignette: Int = 0
) : Serializable
