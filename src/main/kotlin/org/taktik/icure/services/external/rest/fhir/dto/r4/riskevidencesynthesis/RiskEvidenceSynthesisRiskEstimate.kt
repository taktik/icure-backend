//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.riskevidencesynthesis

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * What was the estimated risk
 *
 * The estimated risk of the outcome.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class RiskEvidenceSynthesisRiskEstimate(
  /**
   * Sample size for group measured
   */
  val denominatorCount: Int? = null,
  /**
   * Description of risk estimate
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Number with the outcome
   */
  val numeratorCount: Int? = null,
  val precisionEstimate: List<RiskEvidenceSynthesisRiskEstimatePrecisionEstimate> = listOf(),
  /**
   * Type of risk estimate
   */
  val type: CodeableConcept? = null,
  /**
   * What unit is the outcome described in?
   */
  val unitOfMeasure: CodeableConcept? = null,
  /**
   * Point estimate
   */
  val value: Float? = null
) : BackboneElement
