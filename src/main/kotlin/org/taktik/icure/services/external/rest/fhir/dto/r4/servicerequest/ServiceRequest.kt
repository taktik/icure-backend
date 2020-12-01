//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.servicerequest

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
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range
import org.taktik.icure.services.external.rest.fhir.dto.r4.ratio.Ratio
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference
import org.taktik.icure.services.external.rest.fhir.dto.r4.timing.Timing

/**
 * A request for a service to be performed
 *
 * A record of a request for service such as diagnostic investigations, treatments, or operations to
 * be performed.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ServiceRequest(
  /**
   * Preconditions for service
   */
  val asNeededBoolean: Boolean? = null,
  /**
   * Preconditions for service
   */
  val asNeededCodeableConcept: CodeableConcept? = null,
  /**
   * Date request signed
   */
  val authoredOn: String? = null,
  val basedOn: List<Reference> = listOf(),
  val bodySite: List<CodeableConcept> = listOf(),
  val category: List<CodeableConcept> = listOf(),
  /**
   * What is being requested/ordered
   */
  val code: CodeableConcept? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * True if service/procedure should not be performed
   */
  val doNotPerform: Boolean? = null,
  /**
   * Encounter in which the request was created
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
  val instantiatesCanonical: List<String> = listOf(),
  val instantiatesUri: List<String> = listOf(),
  val insurance: List<Reference> = listOf(),
  /**
   * proposal | plan | directive | order | original-order | reflex-order | filler-order |
   * instance-order | option
   */
  val intent: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  val locationCode: List<CodeableConcept> = listOf(),
  val locationReference: List<Reference> = listOf(),
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  /**
   * When service should occur
   */
  val occurrenceDateTime: String? = null,
  /**
   * When service should occur
   */
  val occurrencePeriod: Period? = null,
  /**
   * When service should occur
   */
  val occurrenceTiming: Timing? = null,
  val orderDetail: List<CodeableConcept> = listOf(),
  /**
   * Patient or consumer-oriented instructions
   */
  val patientInstruction: String? = null,
  val performer: List<Reference> = listOf(),
  /**
   * Performer role
   */
  val performerType: CodeableConcept? = null,
  /**
   * routine | urgent | asap | stat
   */
  val priority: String? = null,
  /**
   * Service amount
   */
  val quantityQuantity: Quantity? = null,
  /**
   * Service amount
   */
  val quantityRange: Range? = null,
  /**
   * Service amount
   */
  val quantityRatio: Ratio? = null,
  val reasonCode: List<CodeableConcept> = listOf(),
  val reasonReference: List<Reference> = listOf(),
  val relevantHistory: List<Reference> = listOf(),
  val replaces: List<Reference> = listOf(),
  /**
   * Who/what is requesting service
   */
  val requester: Reference? = null,
  /**
   * Composite Request ID
   */
  val requisition: Identifier? = null,
  val specimen: List<Reference> = listOf(),
  /**
   * draft | active | on-hold | revoked | completed | entered-in-error | unknown
   */
  val status: String? = null,
  /**
   * Individual or Entity the service is ordered for
   */
  val subject: Reference,
  val supportingInfo: List<Reference> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
