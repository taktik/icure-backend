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
 * Transform Rule from source to target
 *
 * Transform Rule from source to target.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class StructureMapGroupRule(
  val dependent: List<StructureMapGroupRuleDependent> = listOf(),
  /**
   * Documentation for this instance of data
   */
  val documentation: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name of the rule for internal references
   */
  val name: String? = null,
  val rule: List<StructureMapGroupRule> = listOf(),
  val source: List<StructureMapGroupRuleSource> = listOf(),
  val target: List<StructureMapGroupRuleTarget> = listOf()
) : BackboneElement
