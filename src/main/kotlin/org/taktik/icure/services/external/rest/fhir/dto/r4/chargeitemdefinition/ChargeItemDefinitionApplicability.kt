//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.chargeitemdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Whether or not the billing code is applicable
 *
 * Expressions that describe applicability criteria for the billing code.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ChargeItemDefinitionApplicability(
  /**
   * Natural language description of the condition
   */
  val description: String? = null,
  /**
   * Boolean-valued expression
   */
  val expression: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Language of the expression
   */
  val language: String? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement
