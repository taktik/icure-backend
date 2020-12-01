//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.graphdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Links this graph makes rules about
 *
 * Links this graph makes rules about.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class GraphDefinitionLink(
  /**
   * Why this link is specified
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Maximum occurrences for this link
   */
  val max: String? = null,
  /**
   * Minimum occurrences for this link
   */
  val min: Int? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Path in the resource that contains the link
   */
  val path: String? = null,
  /**
   * Which slice (if profiled)
   */
  val sliceName: String? = null,
  val target: List<GraphDefinitionLinkTarget> = listOf()
) : BackboneElement
