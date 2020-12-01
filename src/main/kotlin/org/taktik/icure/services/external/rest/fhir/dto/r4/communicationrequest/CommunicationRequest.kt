//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.communicationrequest

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
 * A request for information to be sent to a receiver
 *
 * A request to convey information; e.g. the CDS system proposes that an alert be sent to a
 * responsible provider, the CDS system proposes that the public health agency be notified about a
 * reportable condition.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CommunicationRequest(
  val about: List<Reference> = listOf(),
  /**
   * When request transitioned to being actionable
   */
  val authoredOn: String? = null,
  val basedOn: List<Reference> = listOf(),
  val category: List<CodeableConcept> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * True if request is prohibiting action
   */
  val doNotPerform: Boolean? = null,
  /**
   * Encounter created as part of
   */
  val encounter: Reference? = null,
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
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  val medium: List<CodeableConcept> = listOf(),
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  /**
   * When scheduled
   */
  val occurrenceDateTime: String? = null,
  /**
   * When scheduled
   */
  val occurrencePeriod: Period? = null,
  val payload: List<CommunicationRequestPayload> = listOf(),
  /**
   * routine | urgent | asap | stat
   */
  val priority: String? = null,
  val reasonCode: List<CodeableConcept> = listOf(),
  val reasonReference: List<Reference> = listOf(),
  val recipient: List<Reference> = listOf(),
  val replaces: List<Reference> = listOf(),
  /**
   * Who/what is requesting service
   */
  val requester: Reference? = null,
  /**
   * Message sender
   */
  val sender: Reference? = null,
  /**
   * draft | active | on-hold | revoked | completed | entered-in-error | unknown
   */
  val status: String? = null,
  /**
   * Reason for current status
   */
  val statusReason: CodeableConcept? = null,
  /**
   * Focus of message
   */
  val subject: Reference? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
