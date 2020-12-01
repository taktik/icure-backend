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
 * Parameters to the transform
 *
 * Parameters to the transform.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class StructureMapGroupRuleTargetParameter(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Parameter value - variable or literal
   */
  val valueBoolean: Boolean? = null,
  /**
   * Parameter value - variable or literal
   */
  val valueDecimal: Float? = null,
  /**
   * Parameter value - variable or literal
   */
  val valueId: String? = null,
  /**
   * Parameter value - variable or literal
   */
  val valueInteger: Int? = null,
  /**
   * Parameter value - variable or literal
   */
  val valueString: String? = null
) : BackboneElement
