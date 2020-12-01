//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.questionnaireresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Groups and questions
 *
 * A group or question item from the original questionnaire for which answers are provided.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class QuestionnaireResponseItem(
  val answer: List<QuestionnaireResponseItemAnswer> = listOf(),
  /**
   * ElementDefinition - details for the item
   */
  val definition: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val item: List<QuestionnaireResponseItem> = listOf(),
  /**
   * Pointer to specific item from Questionnaire
   */
  val linkId: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name for group or question text
   */
  val text: String? = null
) : BackboneElement
