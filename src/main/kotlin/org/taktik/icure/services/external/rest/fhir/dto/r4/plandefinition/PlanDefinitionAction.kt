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
package org.taktik.icure.services.external.rest.fhir.dto.r4.plandefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.age.Age
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.datarequirement.DataRequirement
import org.taktik.icure.services.external.rest.fhir.dto.r4.duration.Duration
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference
import org.taktik.icure.services.external.rest.fhir.dto.r4.relatedartifact.RelatedArtifact
import org.taktik.icure.services.external.rest.fhir.dto.r4.timing.Timing
import org.taktik.icure.services.external.rest.fhir.dto.r4.triggerdefinition.TriggerDefinition

/**
 * Action defined by the plan
 *
 * An action or group of actions to be taken as part of the plan.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PlanDefinitionAction(
  val action: List<PlanDefinitionAction> = listOf(),
  /**
   * single | multiple
   */
  val cardinalityBehavior: String? = null,
  val code: List<CodeableConcept> = listOf(),
  val condition: List<PlanDefinitionActionCondition> = listOf(),
  /**
   * Description of the activity to be performed
   */
  val definitionCanonical: String? = null,
  /**
   * Description of the activity to be performed
   */
  val definitionUri: String? = null,
  /**
   * Brief description of the action
   */
  val description: String? = null,
  val documentation: List<RelatedArtifact> = listOf(),
  val dynamicValue: List<PlanDefinitionActionDynamicValue> = listOf(),
  override val extension: List<Extension> = listOf(),
  val goalId: List<String> = listOf(),
  /**
   * visual-group | logical-group | sentence-group
   */
  val groupingBehavior: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val input: List<DataRequirement> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  val output: List<DataRequirement> = listOf(),
  val participant: List<PlanDefinitionActionParticipant> = listOf(),
  /**
   * yes | no
   */
  val precheckBehavior: String? = null,
  /**
   * User-visible prefix for the action (e.g. 1. or A.)
   */
  val prefix: String? = null,
  /**
   * routine | urgent | asap | stat
   */
  val priority: String? = null,
  val reason: List<CodeableConcept> = listOf(),
  val relatedAction: List<PlanDefinitionActionRelatedAction> = listOf(),
  /**
   * must | could | must-unless-documented
   */
  val requiredBehavior: String? = null,
  /**
   * any | all | all-or-none | exactly-one | at-most-one | one-or-more
   */
  val selectionBehavior: String? = null,
  /**
   * Type of individual the action is focused on
   */
  val subjectCodeableConcept: CodeableConcept? = null,
  /**
   * Type of individual the action is focused on
   */
  val subjectReference: Reference? = null,
  /**
   * Static text equivalent of the action, used if the dynamic aspects cannot be interpreted by the
   * receiving system
   */
  val textEquivalent: String? = null,
  /**
   * When the action should take place
   */
  val timingAge: Age? = null,
  /**
   * When the action should take place
   */
  val timingDateTime: String? = null,
  /**
   * When the action should take place
   */
  val timingDuration: Duration? = null,
  /**
   * When the action should take place
   */
  val timingPeriod: Period? = null,
  /**
   * When the action should take place
   */
  val timingRange: Range? = null,
  /**
   * When the action should take place
   */
  val timingTiming: Timing? = null,
  /**
   * User-visible title
   */
  val title: String? = null,
  /**
   * Transform to apply the template
   */
  val transform: String? = null,
  val trigger: List<TriggerDefinition> = listOf(),
  /**
   * create | update | remove | fire-event
   */
  val type: CodeableConcept? = null
) : BackboneElement
