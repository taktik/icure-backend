package org.taktik.icure.services.external.rest.v1.dto.be.mikrono

import com.github.pozo.KotlinBuilder
import java.io.Serializable

@KotlinBuilder
data class AttachmentDto(val data: ByteArray? = null, val fileName: String? = null, val mimeType: String? = null) : Serializable
