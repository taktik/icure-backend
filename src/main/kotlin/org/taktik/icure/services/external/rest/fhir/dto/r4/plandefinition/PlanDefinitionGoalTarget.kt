//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.plandefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.duration.Duration
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range

/**
 * Target outcome for the goal
 *
 * Indicates what should be done and within what timeframe.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PlanDefinitionGoalTarget(
  /**
   * The target value to be achieved
   */
  val detailCodeableConcept: CodeableConcept? = null,
  /**
   * The target value to be achieved
   */
  val detailQuantity: Quantity? = null,
  /**
   * The target value to be achieved
   */
  val detailRange: Range? = null,
  /**
   * Reach goal within
   */
  val due: Duration? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * The parameter whose value is to be tracked
   */
  val measure: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement
