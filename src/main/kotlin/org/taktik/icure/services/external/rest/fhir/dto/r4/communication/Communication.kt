//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.communication

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
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * A record of information transmitted from a sender to a receiver
 *
 * An occurrence of information being transmitted; e.g. an alert that was sent to a responsible
 * provider, a public health agency that was notified about a reportable condition.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Communication(
  val about: List<Reference> = listOf(),
  val basedOn: List<Reference> = listOf(),
  val category: List<CodeableConcept> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Encounter created as part of
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
  val inResponseTo: List<Reference> = listOf(),
  val instantiatesCanonical: List<String> = listOf(),
  val instantiatesUri: List<String> = listOf(),
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
  val partOf: List<Reference> = listOf(),
  val payload: List<CommunicationPayload> = listOf(),
  /**
   * routine | urgent | asap | stat
   */
  val priority: String? = null,
  val reasonCode: List<CodeableConcept> = listOf(),
  val reasonReference: List<Reference> = listOf(),
  /**
   * When received
   */
  val received: String? = null,
  val recipient: List<Reference> = listOf(),
  /**
   * Message sender
   */
  val sender: Reference? = null,
  /**
   * When sent
   */
  val sent: String? = null,
  /**
   * preparation | in-progress | not-done | on-hold | stopped | completed | entered-in-error |
   * unknown
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
  override val text: Narrative? = null,
  /**
   * Description of the purpose/content
   */
  val topic: CodeableConcept? = null
) : DomainResource
