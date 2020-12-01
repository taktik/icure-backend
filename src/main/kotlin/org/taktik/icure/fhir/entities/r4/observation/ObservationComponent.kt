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
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.range.Range
import org.taktik.icure.fhir.entities.r4.ratio.Ratio
import org.taktik.icure.fhir.entities.r4.sampleddata.SampledData

/**
 * Component results
 *
 * Some observations have multiple component observations.  These component observations are
 * expressed as separate code value pairs that share the same attributes.  Examples include systolic
 * and diastolic component observations for blood pressure measurement and multiple component
 * observations for genetics observations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ObservationComponent(
  /**
   * Type of component observation (code / type)
   */
  val code: CodeableConcept,
  /**
   * Why the component result is missing
   */
  val dataAbsentReason: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val interpretation: List<CodeableConcept> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  val referenceRange: List<ObservationReferenceRange> = listOf(),
  /**
   * Actual component result
   */
  val valueBoolean: Boolean? = null,
  /**
   * Actual component result
   */
  val valueCodeableConcept: CodeableConcept? = null,
  /**
   * Actual component result
   */
  val valueDateTime: String? = null,
  /**
   * Actual component result
   */
  val valueInteger: Int? = null,
  /**
   * Actual component result
   */
  val valuePeriod: Period? = null,
  /**
   * Actual component result
   */
  val valueQuantity: Quantity? = null,
  /**
   * Actual component result
   */
  val valueRange: Range? = null,
  /**
   * Actual component result
   */
  val valueRatio: Ratio? = null,
  /**
   * Actual component result
   */
  val valueSampledData: SampledData? = null,
  /**
   * Actual component result
   */
  val valueString: String? = null,
  /**
   * Actual component result
   */
  val valueTime: String? = null
) : BackboneElement
