//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.goal

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Describes the intended objective(s) for a patient, group or organization
 *
 * Describes the intended objective(s) for a patient, group or organization care, for example,
 * weight loss, restoring an activity of daily living, obtaining herd immunity via immunization,
 * meeting a process improvement objective, etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Goal(
  /**
   * in-progress | improving | worsening | no-change | achieved | sustaining | not-achieved |
   * no-progress | not-attainable
   */
  val achievementStatus: CodeableConcept? = null,
  val addresses: List<Reference> = listOf(),
  val category: List<CodeableConcept> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Code or text describing goal
   */
  val description: CodeableConcept,
  /**
   * Who's responsible for creating Goal?
   */
  val expressedBy: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * proposed | planned | accepted | active | on-hold | completed | cancelled | entered-in-error |
   * rejected
   */
  val lifecycleStatus: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  val outcomeCode: List<CodeableConcept> = listOf(),
  val outcomeReference: List<Reference> = listOf(),
  /**
   * high-priority | medium-priority | low-priority
   */
  val priority: CodeableConcept? = null,
  /**
   * When goal pursuit begins
   */
  val startCodeableConcept: CodeableConcept? = null,
  /**
   * When goal pursuit begins
   */
  val startDate: String? = null,
  /**
   * When goal status took effect
   */
  val statusDate: String? = null,
  /**
   * Reason for current status
   */
  val statusReason: String? = null,
  /**
   * Who this goal is intended for
   */
  val subject: Reference,
  val target: List<GoalTarget> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
