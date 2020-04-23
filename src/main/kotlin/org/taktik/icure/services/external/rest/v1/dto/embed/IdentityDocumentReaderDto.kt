package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable


data class IdentityDocumentReaderDto(
        val justificatifDocumentNumber: String? = null,
        val supportSerialNumber: String? = null,
        val timeReadingEIdDocument: Long? = null,
        val eidDocumentSupportType: Int = 0,
        val reasonManualEncoding: Int = 0,
        val reasonUsingVignette: Int = 0
) : Serializable
