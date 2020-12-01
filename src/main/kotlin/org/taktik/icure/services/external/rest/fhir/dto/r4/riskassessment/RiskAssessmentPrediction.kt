//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.riskassessment

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range

/**
 * Outcome predicted
 *
 * Describes the expected outcome for the subject.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class RiskAssessmentPrediction(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Possible outcome for the subject
   */
  val outcome: CodeableConcept? = null,
  /**
   * Likelihood of specified outcome
   */
  val probabilityDecimal: Float? = null,
  /**
   * Likelihood of specified outcome
   */
  val probabilityRange: Range? = null,
  /**
   * Likelihood of specified outcome as a qualitative value
   */
  val qualitativeRisk: CodeableConcept? = null,
  /**
   * Explanation of prediction
   */
  val rationale: String? = null,
  /**
   * Relative likelihood
   */
  val relativeRisk: Float? = null,
  /**
   * Timeframe or age range
   */
  val whenPeriod: Period? = null,
  /**
   * Timeframe or age range
   */
  val whenRange: Range? = null
) : BackboneElement
