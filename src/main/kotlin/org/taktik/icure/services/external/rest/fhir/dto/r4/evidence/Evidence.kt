//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.evidence

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.contactdetail.ContactDetail
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference
import org.taktik.icure.services.external.rest.fhir.dto.r4.relatedartifact.RelatedArtifact
import org.taktik.icure.services.external.rest.fhir.dto.r4.usagecontext.UsageContext

/**
 * A research context or question
 *
 * The Evidence resource describes the conditional state (population and any exposures being
 * compared within the population) and outcome (if specified) that the knowledge (evidence, assertion,
 * recommendation) is about.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Evidence(
  /**
   * When the evidence was approved by publisher
   */
  val approvalDate: String? = null,
  val author: List<ContactDetail> = listOf(),
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
   * Natural language description of the evidence
   */
  val description: String? = null,
  val editor: List<ContactDetail> = listOf(),
  /**
   * When the evidence is expected to be used
   */
  val effectivePeriod: Period? = null,
  val endorser: List<ContactDetail> = listOf(),
  /**
   * What population?
   */
  val exposureBackground: Reference,
  val exposureVariant: List<Reference> = listOf(),
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
   * When the evidence was last reviewed
   */
  val lastReviewDate: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name for this evidence (computer friendly)
   */
  val name: String? = null,
  val note: List<Annotation> = listOf(),
  val outcome: List<Reference> = listOf(),
  /**
   * Name of the publisher (organization or individual)
   */
  val publisher: String? = null,
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
   * Subordinate title of the Evidence
   */
  val subtitle: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Name for this evidence (human friendly)
   */
  val title: String? = null,
  val topic: List<CodeableConcept> = listOf(),
  /**
   * Canonical identifier for this evidence, represented as a URI (globally unique)
   */
  val url: String? = null,
  val useContext: List<UsageContext> = listOf(),
  /**
   * Business version of the evidence
   */
  val version: String? = null
) : DomainResource
