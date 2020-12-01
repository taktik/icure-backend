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
package org.taktik.icure.services.external.rest.fhir.dto.r4.goal

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.duration.Duration
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range
import org.taktik.icure.services.external.rest.fhir.dto.r4.ratio.Ratio

/**
 * Target outcome for the goal
 *
 * Indicates what should be done by when.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class GoalTarget(
  /**
   * The target value to be achieved
   */
  val detailBoolean: Boolean? = null,
  /**
   * The target value to be achieved
   */
  val detailCodeableConcept: CodeableConcept? = null,
  /**
   * The target value to be achieved
   */
  val detailInteger: Int? = null,
  /**
   * The target value to be achieved
   */
  val detailQuantity: Quantity? = null,
  /**
   * The target value to be achieved
   */
  val detailRange: Range? = null,
  /**
   * The target value to be achieved
   */
  val detailRatio: Ratio? = null,
  /**
   * The target value to be achieved
   */
  val detailString: String? = null,
  /**
   * Reach goal on or before
   */
  val dueDate: String? = null,
  /**
   * Reach goal on or before
   */
  val dueDuration: Duration? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * The parameter whose value is being tracked
   */
  val measure: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement
