//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.structuremap

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Content to create because of this mapping rule
 *
 * Content to create because of this mapping rule.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class StructureMapGroupRuleTarget(
  /**
   * Type or variable this rule applies to
   */
  val context: String? = null,
  /**
   * type | variable
   */
  val contextType: String? = null,
  /**
   * Field to create in the context
   */
  val element: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val listMode: List<String> = listOf(),
  /**
   * Internal rule reference for shared list items
   */
  val listRuleId: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val parameter: List<StructureMapGroupRuleTargetParameter> = listOf(),
  /**
   * create | copy +
   */
  val transform: String? = null,
  /**
   * Named context for field, if desired, and a field is specified
   */
  val variable: String? = null
) : BackboneElement
