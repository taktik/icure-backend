//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.effectevidencesynthesis

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * What was the estimated effect
 *
 * The estimated effect of the exposure variant.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class EffectEvidenceSynthesisEffectEstimate(
  /**
   * Description of effect estimate
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val precisionEstimate: List<EffectEvidenceSynthesisEffectEstimatePrecisionEstimate> = listOf(),
  /**
   * Type of efffect estimate
   */
  val type: CodeableConcept? = null,
  /**
   * What unit is the outcome described in?
   */
  val unitOfMeasure: CodeableConcept? = null,
  /**
   * Point estimate
   */
  val value: Float? = null,
  /**
   * Variant exposure states
   */
  val variantState: CodeableConcept? = null
) : BackboneElement
