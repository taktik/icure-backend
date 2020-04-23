package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable


data class AgreementAppendixDto(
        val docSeq: Int? = null,
        val verseSeq: Int? = null,
        val documentId: String? = null,
        val path: String? = null
) : Serializable
