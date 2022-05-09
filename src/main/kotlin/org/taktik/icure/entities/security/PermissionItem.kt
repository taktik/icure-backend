package org.taktik.icure.entities.security

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.taktik.icure.domain.filter.predicate.Predicate

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "java_type")
interface PermissionItem : Cloneable, Serializable {
	val type: PermissionType
	val predicate: Predicate

	fun merge(other: PermissionItem): PermissionItem
}
