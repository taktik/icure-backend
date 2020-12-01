//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.slot

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * A slot of time on a schedule that may be available for booking appointments
 *
 * A slot of time on a schedule that may be available for booking appointments.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Slot(
  /**
   * The style of appointment or patient that may be booked in the slot (not service type)
   */
  val appointmentType: CodeableConcept? = null,
  /**
   * Comments on the slot to describe any extended information. Such as custom constraints on the
   * slot
   */
  val comment: String? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Date/Time that the slot is to conclude
   */
  val end: String? = null,
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
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * This slot has already been overbooked, appointments are unlikely to be accepted for this time
   */
  val overbooked: Boolean? = null,
  /**
   * The schedule resource that this slot defines an interval of status information
   */
  val schedule: Reference,
  val serviceCategory: List<CodeableConcept> = listOf(),
  val serviceType: List<CodeableConcept> = listOf(),
  val specialty: List<CodeableConcept> = listOf(),
  /**
   * Date/Time that the slot is to begin
   */
  val start: String? = null,
  /**
   * busy | free | busy-unavailable | busy-tentative | entered-in-error
   */
  val status: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
