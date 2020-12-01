//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.riskevidencesynthesis

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * How precise the estimate is
 *
 * A description of the precision of the estimate for the effect.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class RiskEvidenceSynthesisRiskEstimatePrecisionEstimate(
  override val extension: List<Extension> = listOf(),
  /**
   * Lower bound
   */
  @JsonProperty("from")
  val from_fhir: Float? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Level of confidence interval
   */
  val level: Float? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Upper bound
   */
  val to: Float? = null,
  /**
   * Type of precision estimate
   */
  val type: CodeableConcept? = null
) : BackboneElement
