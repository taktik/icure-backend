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
 * Named sections for reader convenience
 *
 * Organizes the mapping into manageable chunks for human review/ease of maintenance.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class StructureMapGroup(
  /**
   * Additional description/explanation for group
   */
  val documentation: String? = null,
  /**
   * Another group that this group adds rules to
   */
  val extends: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val input: List<StructureMapGroupInput> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Human-readable label
   */
  val name: String? = null,
  val rule: List<StructureMapGroupRule> = listOf(),
  /**
   * none | types | type-and-types
   */
  val typeMode: String? = null
) : BackboneElement
