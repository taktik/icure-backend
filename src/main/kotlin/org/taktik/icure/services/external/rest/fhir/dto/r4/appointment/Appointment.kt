/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.appointment

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * A booking of a healthcare event among patient(s), practitioner(s), related person(s) and/or
 * device(s) for a specific date/time. This may result in one or more Encounter(s)
 *
 * A booking of a healthcare event among patient(s), practitioner(s), related person(s) and/or
 * device(s) for a specific date/time. This may result in one or more Encounter(s).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Appointment(
  /**
   * The style of appointment or patient that has been booked in the slot (not service type)
   */
  val appointmentType: CodeableConcept? = null,
  val basedOn: List<Reference> = listOf(),
  /**
   * The coded reason for the appointment being cancelled
   */
  val cancelationReason: CodeableConcept? = null,
  /**
   * Additional comments
   */
  val comment: String? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * The date that this appointment was initially created
   */
  val created: String? = null,
  /**
   * Shown on a subject line in a meeting request, or appointment list
   */
  val description: String? = null,
  /**
   * When appointment is to conclude
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
  /**
   * Can be less than start/end (e.g. estimate)
   */
  val minutesDuration: Int? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val participant: List<AppointmentParticipant> = listOf(),
  /**
   * Detailed information and instructions for the patient
   */
  val patientInstruction: String? = null,
  /**
   * Used to make informed decisions if needing to re-prioritize
   */
  val priority: Int? = null,
  val reasonCode: List<CodeableConcept> = listOf(),
  val reasonReference: List<Reference> = listOf(),
  val requestedPeriod: List<Period> = listOf(),
  val serviceCategory: List<CodeableConcept> = listOf(),
  val serviceType: List<CodeableConcept> = listOf(),
  val slot: List<Reference> = listOf(),
  val specialty: List<CodeableConcept> = listOf(),
  /**
   * When appointment is to take place
   */
  val start: String? = null,
  /**
   * proposed | pending | booked | arrived | fulfilled | cancelled | noshow | entered-in-error |
   * checked-in | waitlist
   */
  val status: String? = null,
  val supportingInformation: List<Reference> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
