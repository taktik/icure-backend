package org.taktik.icure.services.external.rest.v1.dto.security

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.utils.InstantDeserializer
import java.io.Serializable
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AuthenticationTokenDto(
        @Schema(description = "Encrypted token") val token: String,
        @JsonDeserialize(using = InstantDeserializer::class)
        @Schema(description = "Validity starting time of the token") val creationTime: Instant = Instant.now(),
        @Schema(description = "Token validity in seconds") val validity: Long
) : Cloneable, Serializable
