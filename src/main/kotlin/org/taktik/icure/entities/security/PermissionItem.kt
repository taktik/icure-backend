package org.taktik.icure.entities.security

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.predicate.Predicate
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
interface PermissionItem : Cloneable, Serializable {
    val type: PermissionType
    val predicate: Predicate

    fun merge(other: PermissionItem): PermissionItem
}
