//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.elementdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Element values that are used to distinguish the slices
 *
 * Designates which child elements are used to discriminate between the slices when processing an
 * instance. If one or more discriminators are provided, the value of the child elements in the
 * instance data SHALL completely distinguish which slice the element in the resource matches based on
 * the allowed values for those elements in each of the slices.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ElementDefinitionSlicingDiscriminator(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Path to element value
   */
  val path: String? = null,
  /**
   * value | exists | pattern | type | profile
   */
  val type: String? = null
) : Element
