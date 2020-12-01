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
package org.taktik.icure.fhir.entities.r4.careplan

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference
import org.taktik.icure.fhir.entities.r4.timing.Timing

/**
 * In-line definition of activity
 *
 * A simple summary of a planned activity suitable for a general care plan system (e.g. form driven)
 * that doesn't know about specific resources such as procedure etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CarePlanActivityDetail(
  /**
   * Detail type of activity
   */
  val code: CodeableConcept? = null,
  /**
   * How to consume/day?
   */
  val dailyAmount: Quantity? = null,
  /**
   * Extra info describing activity to perform
   */
  val description: String? = null,
  /**
   * If true, activity is prohibiting action
   */
  val doNotPerform: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  val goal: List<Reference> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val instantiatesCanonical: List<String> = listOf(),
  val instantiatesUri: List<String> = listOf(),
  /**
   * Appointment | CommunicationRequest | DeviceRequest | MedicationRequest | NutritionOrder | Task
   * | ServiceRequest | VisionPrescription
   */
  val kind: String? = null,
  /**
   * Where it should happen
   */
  val location: Reference? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val performer: List<Reference> = listOf(),
  /**
   * What is to be administered/supplied
   */
  val productCodeableConcept: CodeableConcept? = null,
  /**
   * What is to be administered/supplied
   */
  val productReference: Reference? = null,
  /**
   * How much to administer/supply/consume
   */
  val quantity: Quantity? = null,
  val reasonCode: List<CodeableConcept> = listOf(),
  val reasonReference: List<Reference> = listOf(),
  /**
   * When activity is to occur
   */
  val scheduledPeriod: Period? = null,
  /**
   * When activity is to occur
   */
  val scheduledString: String? = null,
  /**
   * When activity is to occur
   */
  val scheduledTiming: Timing? = null,
  /**
   * not-started | scheduled | in-progress | on-hold | completed | cancelled | stopped | unknown |
   * entered-in-error
   */
  val status: String? = null,
  /**
   * Reason for current status
   */
  val statusReason: CodeableConcept? = null
) : BackboneElement
