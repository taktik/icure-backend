//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.activitydefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.age.Age
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.contactdetail.ContactDetail
import org.taktik.icure.fhir.entities.r4.dosage.Dosage
import org.taktik.icure.fhir.entities.r4.duration.Duration
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.range.Range
import org.taktik.icure.fhir.entities.r4.reference.Reference
import org.taktik.icure.fhir.entities.r4.relatedartifact.RelatedArtifact
import org.taktik.icure.fhir.entities.r4.timing.Timing
import org.taktik.icure.fhir.entities.r4.usagecontext.UsageContext

/**
 * The definition of a specific activity to be taken, independent of any particular patient or
 * context
 *
 * This resource allows for the definition of some activity to be performed, independent of a
 * particular patient, practitioner, or other performance context.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ActivityDefinition(
  /**
   * When the activity definition was approved by publisher
   */
  val approvalDate: String? = null,
  val author: List<ContactDetail> = listOf(),
  val bodySite: List<CodeableConcept> = listOf(),
  /**
   * Detail type of activity
   */
  val code: CodeableConcept? = null,
  val contact: List<ContactDetail> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Use and/or publishing restrictions
   */
  val copyright: String? = null,
  /**
   * Date last changed
   */
  val date: String? = null,
  /**
   * Natural language description of the activity definition
   */
  val description: String? = null,
  /**
   * True if the activity should not be performed
   */
  val doNotPerform: Boolean? = null,
  val dosage: List<Dosage> = listOf(),
  val dynamicValue: List<ActivityDefinitionDynamicValue> = listOf(),
  val editor: List<ContactDetail> = listOf(),
  /**
   * When the activity definition is expected to be used
   */
  val effectivePeriod: Period? = null,
  val endorser: List<ContactDetail> = listOf(),
  /**
   * For testing purposes, not real usage
   */
  val experimental: Boolean? = null,
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
   * proposal | plan | directive | order | original-order | reflex-order | filler-order |
   * instance-order | option
   */
  val intent: String? = null,
  val jurisdiction: List<CodeableConcept> = listOf(),
  /**
   * Kind of resource
   */
  val kind: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * When the activity definition was last reviewed
   */
  val lastReviewDate: String? = null,
  val library: List<String> = listOf(),
  /**
   * Where it should happen
   */
  val location: Reference? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name for this activity definition (computer friendly)
   */
  val name: String? = null,
  val observationRequirement: List<Reference> = listOf(),
  val observationResultRequirement: List<Reference> = listOf(),
  val participant: List<ActivityDefinitionParticipant> = listOf(),
  /**
   * routine | urgent | asap | stat
   */
  val priority: String? = null,
  /**
   * What's administered/supplied
   */
  val productCodeableConcept: CodeableConcept? = null,
  /**
   * What's administered/supplied
   */
  val productReference: Reference? = null,
  /**
   * What profile the resource needs to conform to
   */
  val profile: String? = null,
  /**
   * Name of the publisher (organization or individual)
   */
  val publisher: String? = null,
  /**
   * Why this activity definition is defined
   */
  val purpose: String? = null,
  /**
   * How much is administered/consumed/supplied
   */
  val quantity: Quantity? = null,
  val relatedArtifact: List<RelatedArtifact> = listOf(),
  val reviewer: List<ContactDetail> = listOf(),
  val specimenRequirement: List<Reference> = listOf(),
  /**
   * draft | active | retired | unknown
   */
  val status: String? = null,
  /**
   * Type of individual the activity definition is intended for
   */
  val subjectCodeableConcept: CodeableConcept? = null,
  /**
   * Type of individual the activity definition is intended for
   */
  val subjectReference: Reference? = null,
  /**
   * Subordinate title of the activity definition
   */
  val subtitle: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * When activity is to occur
   */
  val timingAge: Age? = null,
  /**
   * When activity is to occur
   */
  val timingDateTime: String? = null,
  /**
   * When activity is to occur
   */
  val timingDuration: Duration? = null,
  /**
   * When activity is to occur
   */
  val timingPeriod: Period? = null,
  /**
   * When activity is to occur
   */
  val timingRange: Range? = null,
  /**
   * When activity is to occur
   */
  val timingTiming: Timing? = null,
  /**
   * Name for this activity definition (human friendly)
   */
  val title: String? = null,
  val topic: List<CodeableConcept> = listOf(),
  /**
   * Transform to apply the template
   */
  val transform: String? = null,
  /**
   * Canonical identifier for this activity definition, represented as a URI (globally unique)
   */
  val url: String? = null,
  /**
   * Describes the clinical usage of the activity definition
   */
  val usage: String? = null,
  val useContext: List<UsageContext> = listOf(),
  /**
   * Business version of the activity definition
   */
  val version: String? = null
) : DomainResource
