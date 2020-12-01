package org.taktik.icure.services.external.rest.v1.dto.base

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MimeAttachmentDto(val data: ByteArray? = null, val fileName: String? = null, val mimeType: String? = null) : Serializable
