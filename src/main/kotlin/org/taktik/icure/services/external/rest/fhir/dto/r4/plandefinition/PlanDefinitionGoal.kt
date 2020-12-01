//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.plandefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.relatedartifact.RelatedArtifact

/**
 * What the plan is trying to accomplish
 *
 * Goals that describe what the activities within the plan are intended to achieve. For example,
 * weight loss, restoring an activity of daily living, obtaining herd immunity via immunization,
 * meeting a process improvement objective, etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PlanDefinitionGoal(
  val addresses: List<CodeableConcept> = listOf(),
  /**
   * E.g. Treatment, dietary, behavioral
   */
  val category: CodeableConcept? = null,
  /**
   * Code or text describing the goal
   */
  val description: CodeableConcept,
  val documentation: List<RelatedArtifact> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * high-priority | medium-priority | low-priority
   */
  val priority: CodeableConcept? = null,
  /**
   * When goal pursuit begins
   */
  val start: CodeableConcept? = null,
  val target: List<PlanDefinitionGoalTarget> = listOf()
) : BackboneElement
