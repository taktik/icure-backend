//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.requestgroup

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.age.Age
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.duration.Duration
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.range.Range
import org.taktik.icure.fhir.entities.r4.reference.Reference
import org.taktik.icure.fhir.entities.r4.relatedartifact.RelatedArtifact
import org.taktik.icure.fhir.entities.r4.timing.Timing

/**
 * Proposed actions, if any
 *
 * The actions, if any, produced by the evaluation of the artifact.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class RequestGroupAction(
  val action: List<RequestGroupAction> = listOf(),
  /**
   * single | multiple
   */
  val cardinalityBehavior: String? = null,
  val code: List<CodeableConcept> = listOf(),
  val condition: List<RequestGroupActionCondition> = listOf(),
  /**
   * Short description of the action
   */
  val description: String? = null,
  val documentation: List<RelatedArtifact> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * visual-group | logical-group | sentence-group
   */
  val groupingBehavior: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val participant: List<Reference> = listOf(),
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
  val relatedAction: List<RequestGroupActionRelatedAction> = listOf(),
  /**
   * must | could | must-unless-documented
   */
  val requiredBehavior: String? = null,
  /**
   * The target of the action
   */
  val resource: Reference? = null,
  /**
   * any | all | all-or-none | exactly-one | at-most-one | one-or-more
   */
  val selectionBehavior: String? = null,
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
   * create | update | remove | fire-event
   */
  val type: CodeableConcept? = null
) : BackboneElement
