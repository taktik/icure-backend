package org.taktik.icure.services.external.rest.v1.dto.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ParagraphAgreementDto(
        val timestamp: Long? = null,
        val paragraph: String? = null,
        val accepted: Boolean? = null,
        val inTreatment: Boolean? = null,
        val canceled: Boolean? = null,
        val careProviderReference: String? = null,
        val decisionReference: String? = null,
        val start: Long? = null,
        val end: Long? = null,
        val cancelationDate: Long? = null,
        val quantityValue: Double? = null,
        val quantityUnit: String? = null,
        val ioRequestReference: String? = null,
        val responseType: String? = null,
        val refusalJustification: Map<String, String>? = null,
        val verses: Set<Long>? = null,
        val coverageType: String? = null,
        val unitNumber: Double? = null,
        val strength: Double? = null,
        val strengthUnit: String? = null,
        val agreementAppendices: List<AgreementAppendixDto>? = null,
        val documentId: String? = null
) : Serializable
