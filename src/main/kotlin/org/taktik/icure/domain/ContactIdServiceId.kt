package org.taktik.icure.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.handlers.JacksonContactIdServiceIdDeserializer

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = JacksonContactIdServiceIdDeserializer::class)
@KotlinBuilder
data class ContactIdServiceId(val contactId: String, val serviceId: String? = null, val modified: Long? = null)
