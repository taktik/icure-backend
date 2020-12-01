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
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Questions and sections within the Questionnaire
 *
 * A particular question, question grouping or display text that is part of the questionnaire.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class QuestionnaireItem(
  val answerOption: List<QuestionnaireItemAnswerOption> = listOf(),
  /**
   * Valueset containing permitted answers
   */
  val answerValueSet: String? = null,
  val code: List<Coding> = listOf(),
  /**
   * ElementDefinition - details for the item
   */
  val definition: String? = null,
  /**
   * all | any
   */
  val enableBehavior: String? = null,
  val enableWhen: List<QuestionnaireItemEnableWhen> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val initial: List<QuestionnaireItemInitial> = listOf(),
  val item: List<QuestionnaireItem> = listOf(),
  /**
   * Unique id for item in questionnaire
   */
  val linkId: String? = null,
  /**
   * No more than this many characters
   */
  val maxLength: Int? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * E.g. "1(a)", "2.5.3"
   */
  val prefix: String? = null,
  /**
   * Don't allow human editing
   */
  val readOnly: Boolean? = null,
  /**
   * Whether the item may repeat
   */
  val repeats: Boolean? = null,
  /**
   * Whether the item must be included in data results
   */
  val required: Boolean? = null,
  /**
   * Primary text for the item
   */
  val text: String? = null,
  /**
   * group | display | boolean | decimal | integer | date | dateTime +
   */
  val type: String? = null
) : BackboneElement
