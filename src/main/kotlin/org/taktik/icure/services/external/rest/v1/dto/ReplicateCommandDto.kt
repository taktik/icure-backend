package org.taktik.icure.services.external.rest.v1.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ReplicateCommandDto(
        @JsonProperty("source_url") val sourceUrl: String,
        @JsonProperty("source_username") val sourceUsername: String,
        @JsonProperty("source_password") val sourcePassword: String,
        @JsonProperty("target_url") val targetUrl: String,
        @JsonProperty("target_username") val targetUsername: String,
        @JsonProperty("target_password") val targetPassword: String,
        val id: String? = null
)
