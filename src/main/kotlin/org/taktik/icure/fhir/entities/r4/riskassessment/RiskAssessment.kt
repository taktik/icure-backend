//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.riskassessment

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.annotation.Annotation
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Potential outcomes for a subject with likelihood
 *
 * An assessment of the likely outcome(s) for a patient or other subject as well as the likelihood
 * of each outcome.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class RiskAssessment(
  /**
   * Request fulfilled by this assessment
   */
  val basedOn: Reference? = null,
  val basis: List<Reference> = listOf(),
  /**
   * Type of assessment
   */
  val code: CodeableConcept? = null,
  /**
   * Condition assessed
   */
  val condition: Reference? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Where was assessment performed?
   */
  val encounter: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  /**
   * Evaluation mechanism
   */
  val method: CodeableConcept? = null,
  /**
   * How to reduce risk
   */
  val mitigation: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  /**
   * When was assessment made?
   */
  val occurrenceDateTime: String? = null,
  /**
   * When was assessment made?
   */
  val occurrencePeriod: Period? = null,
  /**
   * Part of this occurrence
   */
  val parent: Reference? = null,
  /**
   * Who did assessment?
   */
  val performer: Reference? = null,
  val prediction: List<RiskAssessmentPrediction> = listOf(),
  val reasonCode: List<CodeableConcept> = listOf(),
  val reasonReference: List<Reference> = listOf(),
  /**
   * registered | preliminary | final | amended +
   */
  val status: String? = null,
  /**
   * Who/what does assessment apply to?
   */
  val subject: Reference,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
