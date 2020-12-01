//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.questionnaireresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.attachment.Attachment
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * The response(s) to the question
 *
 * The respondent's answer(s) to the question.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class QuestionnaireResponseItemAnswer(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val item: List<QuestionnaireResponseItem> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Single-valued answer to the question
   */
  val valueAttachment: Attachment? = null,
  /**
   * Single-valued answer to the question
   */
  val valueBoolean: Boolean? = null,
  /**
   * Single-valued answer to the question
   */
  val valueCoding: Coding? = null,
  /**
   * Single-valued answer to the question
   */
  val valueDate: String? = null,
  /**
   * Single-valued answer to the question
   */
  val valueDateTime: String? = null,
  /**
   * Single-valued answer to the question
   */
  val valueDecimal: Float? = null,
  /**
   * Single-valued answer to the question
   */
  val valueInteger: Int? = null,
  /**
   * Single-valued answer to the question
   */
  val valueQuantity: Quantity? = null,
  /**
   * Single-valued answer to the question
   */
  val valueReference: Reference? = null,
  /**
   * Single-valued answer to the question
   */
  val valueString: String? = null,
  /**
   * Single-valued answer to the question
   */
  val valueTime: String? = null,
  /**
   * Single-valued answer to the question
   */
  val valueUri: String? = null
) : BackboneElement
