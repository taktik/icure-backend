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
package org.taktik.icure.fhir.entities.r4.observation

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.annotation.Annotation
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.range.Range
import org.taktik.icure.fhir.entities.r4.ratio.Ratio
import org.taktik.icure.fhir.entities.r4.reference.Reference
import org.taktik.icure.fhir.entities.r4.sampleddata.SampledData
import org.taktik.icure.fhir.entities.r4.timing.Timing

/**
 * Measurements and simple assertions
 *
 * Measurements and simple assertions made about a patient, device or other subject.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Observation(
  val basedOn: List<Reference> = listOf(),
  /**
   * Observed body part
   */
  val bodySite: CodeableConcept? = null,
  val category: List<CodeableConcept> = listOf(),
  /**
   * Type of observation (code / type)
   */
  val code: CodeableConcept,
  val component: List<ObservationComponent> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Why the result is missing
   */
  val dataAbsentReason: CodeableConcept? = null,
  val derivedFrom: List<Reference> = listOf(),
  /**
   * (Measurement) Device
   */
  val device: Reference? = null,
  /**
   * Clinically relevant time/time-period for observation
   */
  val effectiveDateTime: String? = null,
  /**
   * Clinically relevant time/time-period for observation
   */
  val effectiveInstant: String? = null,
  /**
   * Clinically relevant time/time-period for observation
   */
  val effectivePeriod: Period? = null,
  /**
   * Clinically relevant time/time-period for observation
   */
  val effectiveTiming: Timing? = null,
  /**
   * Healthcare event during which this observation is made
   */
  val encounter: Reference? = null,
  override val extension: List<Extension> = listOf(),
  val focus: List<Reference> = listOf(),
  val hasMember: List<Reference> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val interpretation: List<CodeableConcept> = listOf(),
  /**
   * Date/Time this version was made available
   */
  val issued: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  /**
   * How it was done
   */
  val method: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  val partOf: List<Reference> = listOf(),
  val performer: List<Reference> = listOf(),
  val referenceRange: List<ObservationReferenceRange> = listOf(),
  /**
   * Specimen used for this observation
   */
  val specimen: Reference? = null,
  /**
   * registered | preliminary | final | amended +
   */
  val status: String? = null,
  /**
   * Who and/or what the observation is about
   */
  val subject: Reference? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Actual result
   */
  val valueBoolean: Boolean? = null,
  /**
   * Actual result
   */
  val valueCodeableConcept: CodeableConcept? = null,
  /**
   * Actual result
   */
  val valueDateTime: String? = null,
  /**
   * Actual result
   */
  val valueInteger: Int? = null,
  /**
   * Actual result
   */
  val valuePeriod: Period? = null,
  /**
   * Actual result
   */
  val valueQuantity: Quantity? = null,
  /**
   * Actual result
   */
  val valueRange: Range? = null,
  /**
   * Actual result
   */
  val valueRatio: Ratio? = null,
  /**
   * Actual result
   */
  val valueSampledData: SampledData? = null,
  /**
   * Actual result
   */
  val valueString: String? = null,
  /**
   * Actual result
   */
  val valueTime: String? = null
) : DomainResource
