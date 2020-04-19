package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class AgreementAppendix(
        val docSeq: Int? = null,
        val verseSeq: Int? = null,
        val documentId: String? = null,
        val path: String? = null
) : Serializable
