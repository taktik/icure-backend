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
package org.taktik.icure.fhir.entities.r4.medicationdispense

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.annotation.Annotation
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.dosage.Dosage
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Dispensing a medication to a named patient
 *
 * Indicates that a medication product is to be or has been dispensed for a named person/patient.
 * This includes a description of the medication product (supply) provided and the instructions for
 * administering the medication.  The medication dispense is the result of a pharmacy system responding
 * to a medication order.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationDispense(
  val authorizingPrescription: List<Reference> = listOf(),
  /**
   * Type of medication dispense
   */
  val category: CodeableConcept? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Encounter / Episode associated with event
   */
  val context: Reference? = null,
  /**
   * Amount of medication expressed as a timing amount
   */
  val daysSupply: Quantity? = null,
  /**
   * Where the medication was sent
   */
  val destination: Reference? = null,
  val detectedIssue: List<Reference> = listOf(),
  val dosageInstruction: List<Dosage> = listOf(),
  val eventHistory: List<Reference> = listOf(),
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
   * Where the dispense occurred
   */
  val location: Reference? = null,
  /**
   * What medication was supplied
   */
  val medicationCodeableConcept: CodeableConcept,
  /**
   * What medication was supplied
   */
  val medicationReference: Reference,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  val partOf: List<Reference> = listOf(),
  val performer: List<MedicationDispensePerformer> = listOf(),
  /**
   * Amount dispensed
   */
  val quantity: Quantity? = null,
  val receiver: List<Reference> = listOf(),
  /**
   * preparation | in-progress | cancelled | on-hold | completed | entered-in-error | stopped |
   * declined | unknown
   */
  val status: String? = null,
  /**
   * Why a dispense was not performed
   */
  val statusReasonCodeableConcept: CodeableConcept? = null,
  /**
   * Why a dispense was not performed
   */
  val statusReasonReference: Reference? = null,
  /**
   * Who the dispense is for
   */
  val subject: Reference? = null,
  /**
   * Whether a substitution was performed on the dispense
   */
  val substitution: MedicationDispenseSubstitution? = null,
  val supportingInformation: List<Reference> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Trial fill, partial fill, emergency fill, etc.
   */
  val type: CodeableConcept? = null,
  /**
   * When product was given out
   */
  val whenHandedOver: String? = null,
  /**
   * When product was packaged and reviewed
   */
  val whenPrepared: String? = null
) : DomainResource
