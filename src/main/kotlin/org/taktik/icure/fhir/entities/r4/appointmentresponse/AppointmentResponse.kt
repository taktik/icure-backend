//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.appointmentresponse

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
 * A reply to an appointment request for a patient and/or practitioner(s), such as a confirmation or
 * rejection
 *
 * A reply to an appointment request for a patient and/or practitioner(s), such as a confirmation or
 * rejection.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AppointmentResponse(
  /**
   * Person, Location, HealthcareService, or Device
   */
  val actor: Reference? = null,
  /**
   * Appointment this response relates to
   */
  val appointment: Reference,
  /**
   * Additional comments
   */
  val comment: String? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Time from appointment, or requested new end time
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
   * accepted | declined | tentative | needs-action
   */
  val participantStatus: String? = null,
  val participantType: List<CodeableConcept> = listOf(),
  /**
   * Time from appointment, or requested new start time
   */
  val start: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
