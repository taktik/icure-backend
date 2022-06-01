package org.taktik.icure.services.external.rest.v1.dto.security

import java.io.Serializable
import java.time.Instant
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AuthenticationTokenDto(
	@Schema(description = "Encrypted token") val token: String,
	@Schema(description = "Validity starting time of the token") val creationTime: Long = Instant.now().toEpochMilli(),
	@Schema(description = "Token validity in seconds") val validity: Long
) : Cloneable, Serializable
