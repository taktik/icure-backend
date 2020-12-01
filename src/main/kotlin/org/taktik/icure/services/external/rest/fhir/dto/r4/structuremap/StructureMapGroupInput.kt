//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.structuremap

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Named instance provided when invoking the map
 *
 * A name assigned to an instance of data. The instance must be provided when the mapping is
 * invoked.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class StructureMapGroupInput(
  /**
   * Documentation for this instance of data
   */
  val documentation: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * source | target
   */
  val mode: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name for this instance of data
   */
  val name: String? = null,
  /**
   * Type for this instance of data
   */
  val type: String? = null
) : BackboneElement
