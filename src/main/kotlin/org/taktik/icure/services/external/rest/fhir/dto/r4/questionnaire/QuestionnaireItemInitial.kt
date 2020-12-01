//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.questionnaire

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.attachment.Attachment
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.coding.Coding
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Initial value(s) when item is first rendered
 *
 * One or more values that should be pre-populated in the answer when initially rendering the
 * questionnaire for user input.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class QuestionnaireItemInitial(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Actual value for initializing the question
   */
  val valueAttachment: Attachment,
  /**
   * Actual value for initializing the question
   */
  val valueBoolean: Boolean? = null,
  /**
   * Actual value for initializing the question
   */
  val valueCoding: Coding,
  /**
   * Actual value for initializing the question
   */
  val valueDate: String? = null,
  /**
   * Actual value for initializing the question
   */
  val valueDateTime: String? = null,
  /**
   * Actual value for initializing the question
   */
  val valueDecimal: Float? = null,
  /**
   * Actual value for initializing the question
   */
  val valueInteger: Int? = null,
  /**
   * Actual value for initializing the question
   */
  val valueQuantity: Quantity,
  /**
   * Actual value for initializing the question
   */
  val valueReference: Reference,
  /**
   * Actual value for initializing the question
   */
  val valueString: String? = null,
  /**
   * Actual value for initializing the question
   */
  val valueTime: String? = null,
  /**
   * Actual value for initializing the question
   */
  val valueUri: String? = null
) : BackboneElement
