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
 * Structure Definition used by this map
 *
 * A structure definition used by this map. The structure definition may describe instances that are
 * converted, or the instances that are produced.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class StructureMapStructure(
  /**
   * Name for type in this map
   */
  val alias: String? = null,
  /**
   * Documentation on use of structure
   */
  val documentation: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * source | queried | target | produced
   */
  val mode: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Canonical reference to structure definition
   */
  val url: String? = null
) : BackboneElement
