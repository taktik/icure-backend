//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.detectedissue

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Clinical issue with action
 *
 * Indicates an actual or potential clinical issue with or between one or more active or proposed
 * clinical actions for a patient; e.g. Drug-drug interaction, Ineffective treatment frequency,
 * Procedure-condition conflict, etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DetectedIssue(
  /**
   * The provider or device that identified the issue
   */
  val author: Reference? = null,
  /**
   * Issue Category, e.g. drug-drug, duplicate therapy, etc.
   */
  val code: CodeableConcept? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Description and context
   */
  val detail: String? = null,
  val evidence: List<DetectedIssueEvidence> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * When identified
   */
  val identifiedDateTime: String? = null,
  /**
   * When identified
   */
  val identifiedPeriod: Period? = null,
  val identifier: List<Identifier> = listOf(),
  val implicated: List<Reference> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  val mitigation: List<DetectedIssueMitigation> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Associated patient
   */
  val patient: Reference? = null,
  /**
   * Authority for issue
   */
  val reference: String? = null,
  /**
   * high | moderate | low
   */
  val severity: String? = null,
  /**
   * registered | preliminary | final | amended +
   */
  val status: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
