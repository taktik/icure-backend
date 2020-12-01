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
package org.taktik.icure.fhir.entities.r4.questionnaire

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Only allow data when
 *
 * A constraint indicating that this item should only be enabled (displayed/allow answers to be
 * captured) when the specified condition is true.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class QuestionnaireItemEnableWhen(
  /**
   * Value for question comparison based on operator
   */
  val answerBoolean: Boolean? = null,
  /**
   * Value for question comparison based on operator
   */
  val answerCoding: Coding,
  /**
   * Value for question comparison based on operator
   */
  val answerDate: String? = null,
  /**
   * Value for question comparison based on operator
   */
  val answerDateTime: String? = null,
  /**
   * Value for question comparison based on operator
   */
  val answerDecimal: Float? = null,
  /**
   * Value for question comparison based on operator
   */
  val answerInteger: Int? = null,
  /**
   * Value for question comparison based on operator
   */
  val answerQuantity: Quantity,
  /**
   * Value for question comparison based on operator
   */
  val answerReference: Reference,
  /**
   * Value for question comparison based on operator
   */
  val answerString: String? = null,
  /**
   * Value for question comparison based on operator
   */
  val answerTime: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * exists | = | != | > | < | >= | <=
   */
  val operator: String? = null,
  /**
   * Question that determines whether item is enabled
   */
  val question: String? = null
) : BackboneElement
