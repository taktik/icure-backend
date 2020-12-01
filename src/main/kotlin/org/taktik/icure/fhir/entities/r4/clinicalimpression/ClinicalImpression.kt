//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.clinicalimpression

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
 * A clinical assessment performed when planning treatments and management strategies for a patient
 *
 * A record of a clinical assessment performed to determine what problem(s) may affect the patient
 * and before planning the treatments or management strategies that are best to manage a patient's
 * condition. Assessments are often 1:1 with a clinical consultation / encounter,  but this varies
 * greatly depending on the clinical workflow. This resource is called "ClinicalImpression" rather than
 * "ClinicalAssessment" to avoid confusion with the recording of assessment tools such as Apgar score.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ClinicalImpression(
  /**
   * The clinician performing the assessment
   */
  val assessor: Reference? = null,
  /**
   * Kind of assessment performed
   */
  val code: CodeableConcept? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * When the assessment was documented
   */
  val date: String? = null,
  /**
   * Why/how the assessment was performed
   */
  val description: String? = null,
  /**
   * Time of assessment
   */
  val effectiveDateTime: String? = null,
  /**
   * Time of assessment
   */
  val effectivePeriod: Period? = null,
  /**
   * Encounter created as part of
   */
  val encounter: Reference? = null,
  override val extension: List<Extension> = listOf(),
  val finding: List<ClinicalImpressionFinding> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val investigation: List<ClinicalImpressionInvestigation> = listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  /**
   * Reference to last assessment
   */
  val previous: Reference? = null,
  val problem: List<Reference> = listOf(),
  val prognosisCodeableConcept: List<CodeableConcept> = listOf(),
  val prognosisReference: List<Reference> = listOf(),
  val protocol: List<String> = listOf(),
  /**
   * in-progress | completed | entered-in-error
   */
  val status: String? = null,
  /**
   * Reason for current status
   */
  val statusReason: CodeableConcept? = null,
  /**
   * Patient or group assessed
   */
  val subject: Reference,
  /**
   * Summary of the assessment
   */
  val summary: String? = null,
  val supportingInfo: List<Reference> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
