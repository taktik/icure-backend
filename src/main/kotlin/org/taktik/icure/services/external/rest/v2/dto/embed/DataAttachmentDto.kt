package org.taktik.icure.services.external.rest.v2.dto.embed

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DataAttachmentDto(
	@Schema(description = "Id of the attachment, if stored as a couchdb attachment") val couchDbAttachmentId: String? = null,
	@Schema(description = "Id of the attachment, if stored using the object storage service") val objectStoreAttachmentId: String? = null,
	@Schema(description = "The Uniform Type Identifiers (https://developer.apple.com/library/archive/documentation/FileManagement/Conceptual/understanding_utis/understand_utis_conc/understand_utis_conc.html#//apple_ref/doc/uid/TP40001319-CH202-CHDHIJDE) of the attachment. This is a list to allow representing a priority, but each UTI must be unique.") val utis: List<String> = emptyList()
) : Serializable
