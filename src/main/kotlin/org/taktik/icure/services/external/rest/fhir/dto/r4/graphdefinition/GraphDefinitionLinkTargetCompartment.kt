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
 * Compartment Consistency Rules
 *
 * Compartment Consistency Rules.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class GraphDefinitionLinkTargetCompartment(
  /**
   * Patient | Encounter | RelatedPerson | Practitioner | Device
   */
  val code: String? = null,
  /**
   * Documentation for FHIRPath expression
   */
  val description: String? = null,
  /**
   * Custom rule, as a FHIRPath expression
   */
  val expression: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * identical | matching | different | custom
   */
  val rule: String? = null,
  /**
   * condition | requirement
   */
  val use: String? = null
) : BackboneElement
