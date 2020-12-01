//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.graphdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Potential target for the link
 *
 * Potential target for the link.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class GraphDefinitionLinkTarget(
  val compartment: List<GraphDefinitionLinkTargetCompartment> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val link: List<GraphDefinitionLink> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Criteria for reverse lookup
   */
  val params: String? = null,
  /**
   * Profile for the target resource
   */
  val profile: String? = null,
  /**
   * Type of resource this link refers to
   */
  val type: String? = null
) : BackboneElement
