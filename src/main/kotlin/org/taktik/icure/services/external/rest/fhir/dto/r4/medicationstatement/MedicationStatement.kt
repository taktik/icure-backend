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
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicationstatement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.dosage.Dosage
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Record of medication being taken by a patient
 *
 * A record of a medication that is being consumed by a patient.   A MedicationStatement may
 * indicate that the patient may be taking the medication now or has taken the medication in the past
 * or will be taking the medication in the future.  The source of this information can be the patient,
 * significant other (such as a family member or spouse), or a clinician.  A common scenario where this
 * information is captured is during the history taking process during a patient visit or stay.   The
 * medication information may come from sources such as the patient's memory, from a prescription
 * bottle,  or from a list of medications the patient, clinician or other party maintains.
 *
 * The primary difference between a medication statement and a medication administration is that the
 * medication administration has complete administration information and is based on actual
 * administration information from the person who administered the medication.  A medication statement
 * is often, if not always, less specific.  There is no required date/time when the medication was
 * administered, in fact we only know that a source has reported the patient is taking this medication,
 * where details such as time, quantity, or rate or even medication product may be incomplete or
 * missing or less precise.  As stated earlier, the medication statement information may come from the
 * patient's memory, from a prescription bottle or from a list of medications the patient, clinician or
 * other party maintains.  Medication administration is more formal and is not missing detailed
 * information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationStatement(
  val basedOn: List<Reference> = listOf(),
  /**
   * Type of medication usage
   */
  val category: CodeableConcept? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Encounter / Episode associated with MedicationStatement
   */
  val context: Reference? = null,
  /**
   * When the statement was asserted?
   */
  val dateAsserted: String? = null,
  val derivedFrom: List<Reference> = listOf(),
  val dosage: List<Dosage> = listOf(),
  /**
   * The date/time or interval when the medication is/was/will be taken
   */
  val effectiveDateTime: String? = null,
  /**
   * The date/time or interval when the medication is/was/will be taken
   */
  val effectivePeriod: Period? = null,
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
   * Person or organization that provided the information about the taking of this medication
   */
  val informationSource: Reference? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * What medication was taken
   */
  val medicationCodeableConcept: CodeableConcept,
  /**
   * What medication was taken
   */
  val medicationReference: Reference,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  val partOf: List<Reference> = listOf(),
  val reasonCode: List<CodeableConcept> = listOf(),
  val reasonReference: List<Reference> = listOf(),
  /**
   * active | completed | entered-in-error | intended | stopped | on-hold | unknown | not-taken
   */
  val status: String? = null,
  val statusReason: List<CodeableConcept> = listOf(),
  /**
   * Who is/was taking  the medication
   */
  val subject: Reference,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
