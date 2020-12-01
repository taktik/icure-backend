//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicationadministration

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Administration of medication to a patient
 *
 * Describes the event of a patient consuming or otherwise being administered a medication.  This
 * may be as simple as swallowing a tablet or it may be a long running infusion.  Related resources tie
 * this event to the authorizing prescription, and the specific encounter between patient and health
 * care practitioner.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationAdministration(
  /**
   * Type of medication usage
   */
  val category: CodeableConcept? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Encounter or Episode of Care administered as part of
   */
  val context: Reference? = null,
  val device: List<Reference> = listOf(),
  /**
   * Details of how medication was taken
   */
  val dosage: MedicationAdministrationDosage? = null,
  /**
   * Start and end time of administration
   */
  val effectiveDateTime: String? = null,
  /**
   * Start and end time of administration
   */
  val effectivePeriod: Period,
  val eventHistory: List<Reference> = listOf(),
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
  val instantiates: List<String> = listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * What was administered
   */
  val medicationCodeableConcept: CodeableConcept,
  /**
   * What was administered
   */
  val medicationReference: Reference,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  val partOf: List<Reference> = listOf(),
  val performer: List<MedicationAdministrationPerformer> = listOf(),
  val reasonCode: List<CodeableConcept> = listOf(),
  val reasonReference: List<Reference> = listOf(),
  /**
   * Request administration performed against
   */
  val request: Reference? = null,
  /**
   * in-progress | not-done | on-hold | completed | entered-in-error | stopped | unknown
   */
  val status: String? = null,
  val statusReason: List<CodeableConcept> = listOf(),
  /**
   * Who received medication
   */
  val subject: Reference,
  val supportingInformation: List<Reference> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
