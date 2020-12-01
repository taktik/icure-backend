//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.capabilitystatement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Search parameters supported by implementation
 *
 * Search parameters for implementations to support and/or make use of - either references to ones
 * defined in the specification, or additional ones defined for/by the implementation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CapabilityStatementRestResourceSearchParam(
  /**
   * Source of definition for parameter
   */
  val definition: String? = null,
  /**
   * Server-specific usage
   */
  val documentation: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name of search parameter
   */
  val name: String? = null,
  /**
   * number | date | string | token | reference | composite | quantity | uri | special
   */
  val type: String? = null
) : BackboneElement
