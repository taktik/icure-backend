//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.researchdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.contactdetail.ContactDetail
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference
import org.taktik.icure.fhir.entities.r4.relatedartifact.RelatedArtifact
import org.taktik.icure.fhir.entities.r4.usagecontext.UsageContext

/**
 * A research context or question
 *
 * The ResearchDefinition resource describes the conditional state (population and any exposures
 * being compared within the population) and outcome (if specified) that the knowledge (evidence,
 * assertion, recommendation) is about.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ResearchDefinition(
  /**
   * When the research definition was approved by publisher
   */
  val approvalDate: String? = null,
  val author: List<ContactDetail> = listOf(),
  val comment: List<String> = listOf(),
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
   * Natural language description of the research definition
   */
  val description: String? = null,
  val editor: List<ContactDetail> = listOf(),
  /**
   * When the research definition is expected to be used
   */
  val effectivePeriod: Period? = null,
  val endorser: List<ContactDetail> = listOf(),
  /**
   * For testing purposes, not real usage
   */
  val experimental: Boolean? = null,
  /**
   * What exposure?
   */
  val exposure: Reference? = null,
  /**
   * What alternative exposure state?
   */
  val exposureAlternative: Reference? = null,
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
  val jurisdiction: List<CodeableConcept> = listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * When the research definition was last reviewed
   */
  val lastReviewDate: String? = null,
  val library: List<String> = listOf(),
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name for this research definition (computer friendly)
   */
  val name: String? = null,
  /**
   * What outcome?
   */
  val outcome: Reference? = null,
  /**
   * What population?
   */
  val population: Reference,
  /**
   * Name of the publisher (organization or individual)
   */
  val publisher: String? = null,
  /**
   * Why this research definition is defined
   */
  val purpose: String? = null,
  val relatedArtifact: List<RelatedArtifact> = listOf(),
  val reviewer: List<ContactDetail> = listOf(),
  /**
   * Title for use in informal contexts
   */
  val shortTitle: String? = null,
  /**
   * draft | active | retired | unknown
   */
  val status: String? = null,
  /**
   * E.g. Patient, Practitioner, RelatedPerson, Organization, Location, Device
   */
  val subjectCodeableConcept: CodeableConcept? = null,
  /**
   * E.g. Patient, Practitioner, RelatedPerson, Organization, Location, Device
   */
  val subjectReference: Reference? = null,
  /**
   * Subordinate title of the ResearchDefinition
   */
  val subtitle: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Name for this research definition (human friendly)
   */
  val title: String? = null,
  val topic: List<CodeableConcept> = listOf(),
  /**
   * Canonical identifier for this research definition, represented as a URI (globally unique)
   */
  val url: String? = null,
  /**
   * Describes the clinical usage of the ResearchDefinition
   */
  val usage: String? = null,
  val useContext: List<UsageContext> = listOf(),
  /**
   * Business version of the research definition
   */
  val version: String? = null
) : DomainResource
