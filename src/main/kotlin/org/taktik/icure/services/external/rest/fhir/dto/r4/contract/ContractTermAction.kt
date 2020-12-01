//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.contract

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference
import org.taktik.icure.services.external.rest.fhir.dto.r4.timing.Timing

/**
 * Entity being ascribed responsibility
 *
 * An actor taking a role in an activity for which it can be assigned some degree of responsibility
 * for the activity taking place.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ContractTermAction(
  /**
   * Episode associated with action
   */
  val context: Reference? = null,
  val contextLinkId: List<String> = listOf(),
  /**
   * True if the term prohibits the  action
   */
  val doNotPerform: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Purpose for the Contract Term Action
   */
  val intent: CodeableConcept,
  val linkId: List<String> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  /**
   * When action happens
   */
  val occurrenceDateTime: String? = null,
  /**
   * When action happens
   */
  val occurrencePeriod: Period? = null,
  /**
   * When action happens
   */
  val occurrenceTiming: Timing? = null,
  /**
   * Actor that wil execute (or not) the action
   */
  val performer: Reference? = null,
  val performerLinkId: List<String> = listOf(),
  /**
   * Competency of the performer
   */
  val performerRole: CodeableConcept? = null,
  val performerType: List<CodeableConcept> = listOf(),
  val reason: List<String> = listOf(),
  val reasonCode: List<CodeableConcept> = listOf(),
  val reasonLinkId: List<String> = listOf(),
  val reasonReference: List<Reference> = listOf(),
  val requester: List<Reference> = listOf(),
  val requesterLinkId: List<String> = listOf(),
  val securityLabelNumber: List<Int> = listOf(),
  /**
   * State of the action
   */
  val status: CodeableConcept,
  val subject: List<ContractTermActionSubject> = listOf(),
  /**
   * Type or form of the action
   */
  val type: CodeableConcept
) : BackboneElement
