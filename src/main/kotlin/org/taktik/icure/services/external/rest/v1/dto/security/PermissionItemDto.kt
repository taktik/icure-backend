package org.taktik.icure.services.external.rest.v1.dto.security

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.taktik.icure.services.external.rest.v1.dto.filter.predicate.Predicate
import org.taktik.icure.services.external.rest.v1.handlers.JacksonPermissionItemDeserializer
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = JacksonPermissionItemDeserializer::class)
interface PermissionItemDto : Cloneable, Serializable {
    val type: PermissionTypeDto
    val predicate: Predicate
}
