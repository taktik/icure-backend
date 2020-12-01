//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.immunization

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Immunization event information
 *
 * Describes the event of a patient being administered a vaccine or a record of an immunization as
 * reported by a patient, a clinician or another party.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Immunization(
  override val contained: List<Resource> = listOf(),
  /**
   * Amount of vaccine administered
   */
  val doseQuantity: Quantity? = null,
  val education: List<ImmunizationEducation> = listOf(),
  /**
   * Encounter immunization was part of
   */
  val encounter: Reference? = null,
  /**
   * Vaccine expiration date
   */
  val expirationDate: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Funding source for the vaccine
   */
  val fundingSource: CodeableConcept? = null,
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
   * Dose potency
   */
  val isSubpotent: Boolean? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Where immunization occurred
   */
  val location: Reference? = null,
  /**
   * Vaccine lot number
   */
  val lotNumber: String? = null,
  /**
   * Vaccine manufacturer
   */
  val manufacturer: Reference? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  /**
   * Vaccine administration date
   */
  val occurrenceDateTime: String? = null,
  /**
   * Vaccine administration date
   */
  val occurrenceString: String? = null,
  /**
   * Who was immunized
   */
  val patient: Reference,
  val performer: List<ImmunizationPerformer> = listOf(),
  /**
   * Indicates context the data was recorded in
   */
  val primarySource: Boolean? = null,
  val programEligibility: List<CodeableConcept> = listOf(),
  val protocolApplied: List<ImmunizationProtocolApplied> = listOf(),
  val reaction: List<ImmunizationReaction> = listOf(),
  val reasonCode: List<CodeableConcept> = listOf(),
  val reasonReference: List<Reference> = listOf(),
  /**
   * When the immunization was first captured in the subject's record
   */
  val recorded: String? = null,
  /**
   * Indicates the source of a secondarily reported record
   */
  val reportOrigin: CodeableConcept? = null,
  /**
   * How vaccine entered body
   */
  val route: CodeableConcept? = null,
  /**
   * Body site vaccine  was administered
   */
  val site: CodeableConcept? = null,
  /**
   * completed | entered-in-error | not-done
   */
  val status: String? = null,
  /**
   * Reason not done
   */
  val statusReason: CodeableConcept? = null,
  val subpotentReason: List<CodeableConcept> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Vaccine product administered
   */
  val vaccineCode: CodeableConcept
) : DomainResource
