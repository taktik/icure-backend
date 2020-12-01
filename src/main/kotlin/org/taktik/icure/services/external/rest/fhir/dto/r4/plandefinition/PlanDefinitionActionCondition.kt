//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.plandefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.expression.Expression
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Whether or not the action is applicable
 *
 * An expression that describes applicability criteria or start/stop conditions for the action.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PlanDefinitionActionCondition(
  /**
   * Boolean-valued expression
   */
  val expression: Expression? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * applicability | start | stop
   */
  val kind: String? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement
