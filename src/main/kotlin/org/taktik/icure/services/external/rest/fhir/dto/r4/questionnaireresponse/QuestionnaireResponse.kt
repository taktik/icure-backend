//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.questionnaireresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * A structured set of questions and their answers
 *
 * A structured set of questions and their answers. The questions are ordered and grouped into
 * coherent subsets, corresponding to the structure of the grouping of the questionnaire being
 * responded to.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class QuestionnaireResponse(
  /**
   * Person who received and recorded the answers
   */
  val author: Reference? = null,
  /**
   * Date the answers were gathered
   */
  val authored: String? = null,
  val basedOn: List<Reference> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Encounter created as part of
   */
  val encounter: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * Unique id for this set of answers
   */
  val identifier: Identifier? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val item: List<QuestionnaireResponseItem> = listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val partOf: List<Reference> = listOf(),
  /**
   * Form being answered
   */
  val questionnaire: String? = null,
  /**
   * The person who answered the questions
   */
  val source: Reference? = null,
  /**
   * in-progress | completed | amended | entered-in-error | stopped
   */
  val status: String? = null,
  /**
   * The subject of the questions
   */
  val subject: Reference? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
