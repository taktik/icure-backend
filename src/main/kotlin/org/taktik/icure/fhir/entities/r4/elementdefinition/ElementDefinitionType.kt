//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.elementdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Element
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Data type and Profile for this element
 *
 * The data type or resource that the value of this element is permitted to be.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ElementDefinitionType(
  val aggregation: List<String> = listOf(),
  /**
   * Data type or Resource (reference to definition)
   */
  val code: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val profile: List<String> = listOf(),
  val targetProfile: List<String> = listOf(),
  /**
   * either | independent | specific
   */
  val versioning: String? = null
) : Element
