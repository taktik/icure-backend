//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.medicationrequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.annotation.Annotation
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.dosage.Dosage
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Ordering of medication for patient or group
 *
 * An order or request for both supply of the medication and the instructions for administration of
 * the medication to a patient. The resource is called "MedicationRequest" rather than
 * "MedicationPrescription" or "MedicationOrder" to generalize the use across inpatient and outpatient
 * settings, including care plans, etc., and to harmonize with workflow patterns.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationRequest(
  /**
   * When request was initially authored
   */
  val authoredOn: String? = null,
  val basedOn: List<Reference> = listOf(),
  val category: List<CodeableConcept> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Overall pattern of medication administration
   */
  val courseOfTherapyType: CodeableConcept? = null,
  val detectedIssue: List<Reference> = listOf(),
  /**
   * Medication supply authorization
   */
  val dispenseRequest: MedicationRequestDispenseRequest? = null,
  /**
   * True if request is prohibiting action
   */
  val doNotPerform: Boolean? = null,
  val dosageInstruction: List<Dosage> = listOf(),
  /**
   * Encounter created as part of encounter/admission/stay
   */
  val encounter: Reference? = null,
  val eventHistory: List<Reference> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Composite request this is part of
   */
  val groupIdentifier: Identifier? = null,
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val instantiatesCanonical: List<String> = listOf(),
  val instantiatesUri: List<String> = listOf(),
  val insurance: List<Reference> = listOf(),
  /**
   * proposal | plan | order | original-order | reflex-order | filler-order | instance-order |
   * option
   */
  val intent: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Medication to be taken
   */
  val medicationCodeableConcept: CodeableConcept,
  /**
   * Medication to be taken
   */
  val medicationReference: Reference,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  /**
   * Intended performer of administration
   */
  val performer: Reference? = null,
  /**
   * Desired kind of performer of the medication administration
   */
  val performerType: CodeableConcept? = null,
  /**
   * An order/prescription that is being replaced
   */
  val priorPrescription: Reference? = null,
  /**
   * routine | urgent | asap | stat
   */
  val priority: String? = null,
  val reasonCode: List<CodeableConcept> = listOf(),
  val reasonReference: List<Reference> = listOf(),
  /**
   * Person who entered the request
   */
  val recorder: Reference? = null,
  /**
   * Reported rather than primary record
   */
  val reportedBoolean: Boolean? = null,
  /**
   * Reported rather than primary record
   */
  val reportedReference: Reference? = null,
  /**
   * Who/What requested the Request
   */
  val requester: Reference? = null,
  /**
   * active | on-hold | cancelled | completed | entered-in-error | stopped | draft | unknown
   */
  val status: String? = null,
  /**
   * Reason for current status
   */
  val statusReason: CodeableConcept? = null,
  /**
   * Who or group medication request is for
   */
  val subject: Reference,
  /**
   * Any restrictions on medication substitution
   */
  val substitution: MedicationRequestSubstitution? = null,
  val supportingInformation: List<Reference> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
