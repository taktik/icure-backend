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
package org.taktik.icure.fhir.entities.r4.riskassessment

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.range.Range

/**
 * Outcome predicted
 *
 * Describes the expected outcome for the subject.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class RiskAssessmentPrediction(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Possible outcome for the subject
   */
  val outcome: CodeableConcept? = null,
  /**
   * Likelihood of specified outcome
   */
  val probabilityDecimal: Float? = null,
  /**
   * Likelihood of specified outcome
   */
  val probabilityRange: Range? = null,
  /**
   * Likelihood of specified outcome as a qualitative value
   */
  val qualitativeRisk: CodeableConcept? = null,
  /**
   * Explanation of prediction
   */
  val rationale: String? = null,
  /**
   * Relative likelihood
   */
  val relativeRisk: Float? = null,
  /**
   * Timeframe or age range
   */
  val whenPeriod: Period? = null,
  /**
   * Timeframe or age range
   */
  val whenRange: Range? = null
) : BackboneElement
