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
 * This element is sliced - slices follow
 *
 * Indicates that the element is sliced into a set of alternative definitions (i.e. in a structure
 * definition, there are multiple different constraints on a single element in the base resource).
 * Slicing can be used in any resource that has cardinality ..* on the base resource, or any resource
 * with a choice of types. The set of slices is any elements that come after this in the element
 * sequence that have the same path, until a shorter path occurs (the shorter path terminates the set).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ElementDefinitionSlicing(
  /**
   * Text description of how slicing works (or not)
   */
  val description: String? = null,
  val discriminator: List<ElementDefinitionSlicingDiscriminator> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * If elements must be in same order as slices
   */
  val ordered: Boolean? = null,
  /**
   * closed | open | openAtEnd
   */
  val rules: String? = null
) : Element
