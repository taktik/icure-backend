//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.goal

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.duration.Duration
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.range.Range
import org.taktik.icure.fhir.entities.r4.ratio.Ratio

/**
 * Target outcome for the goal
 *
 * Indicates what should be done by when.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class GoalTarget(
  /**
   * The target value to be achieved
   */
  val detailBoolean: Boolean? = null,
  /**
   * The target value to be achieved
   */
  val detailCodeableConcept: CodeableConcept? = null,
  /**
   * The target value to be achieved
   */
  val detailInteger: Int? = null,
  /**
   * The target value to be achieved
   */
  val detailQuantity: Quantity? = null,
  /**
   * The target value to be achieved
   */
  val detailRange: Range? = null,
  /**
   * The target value to be achieved
   */
  val detailRatio: Ratio? = null,
  /**
   * The target value to be achieved
   */
  val detailString: String? = null,
  /**
   * Reach goal on or before
   */
  val dueDate: String? = null,
  /**
   * Reach goal on or before
   */
  val dueDuration: Duration? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * The parameter whose value is being tracked
   */
  val measure: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement
