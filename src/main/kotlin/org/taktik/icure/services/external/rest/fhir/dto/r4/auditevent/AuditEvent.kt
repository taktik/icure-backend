//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.auditevent

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.coding.Coding
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period

/**
 * Event record kept for security purposes
 *
 * A record of an event made for purposes of maintaining a security log. Typical uses include
 * detection of intrusion attempts and monitoring for inappropriate usage.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AuditEvent(
  /**
   * Type of action performed during the event
   */
  val action: String? = null,
  val agent: List<AuditEventAgent> = listOf(),
  override val contained: List<Resource> = listOf(),
  val entity: List<AuditEventEntity> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
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
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Whether the event succeeded or failed
   */
  val outcome: String? = null,
  /**
   * Description of the event outcome
   */
  val outcomeDesc: String? = null,
  /**
   * When the activity occurred
   */
  val period: Period? = null,
  val purposeOfEvent: List<CodeableConcept> = listOf(),
  /**
   * Time when the event was recorded
   */
  val recorded: String? = null,
  /**
   * Audit Event Reporter
   */
  val source: AuditEventSource,
  val subtype: List<Coding> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Type/identifier of event
   */
  val type: Coding
) : DomainResource
