package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable
import com.github.pozo.KotlinBuilder
import com.github.pozo.KotlinBuilder
@KotlinBuilder
data class AgreementAppendixDto(
        val docSeq: Int? = null,
        val verseSeq: Int? = null,
        val documentId: String? = null,
        val path: String? = null
) : Serializable
