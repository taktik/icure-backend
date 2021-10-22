package org.taktik.icure.services.external.rest.v2.dto.security

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.services.external.rest.v2.dto.filter.predicate.Predicate
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
interface PermissionItemDto : Cloneable, Serializable {
    val type: PermissionTypeDto
    val predicate: Predicate
}
