//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.effectevidencesynthesis

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
 * A quantified estimate of effect based on a body of evidence
 *
 * The EffectEvidenceSynthesis resource describes the difference in an outcome between exposures
 * states in a population where the effect estimate is derived from a combination of research studies.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class EffectEvidenceSynthesis(
  /**
   * When the effect evidence synthesis was approved by publisher
   */
  val approvalDate: String? = null,
  val author: List<ContactDetail> = listOf(),
  val certainty: List<EffectEvidenceSynthesisCertainty> = listOf(),
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
   * Natural language description of the effect evidence synthesis
   */
  val description: String? = null,
  val editor: List<ContactDetail> = listOf(),
  val effectEstimate: List<EffectEvidenceSynthesisEffectEstimate> = listOf(),
  /**
   * When the effect evidence synthesis is expected to be used
   */
  val effectivePeriod: Period? = null,
  val endorser: List<ContactDetail> = listOf(),
  /**
   * What exposure?
   */
  val exposure: Reference,
  /**
   * What comparison exposure?
   */
  val exposureAlternative: Reference,
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
   * When the effect evidence synthesis was last reviewed
   */
  val lastReviewDate: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name for this effect evidence synthesis (computer friendly)
   */
  val name: String? = null,
  val note: List<Annotation> = listOf(),
  /**
   * What outcome?
   */
  val outcome: Reference,
  /**
   * What population?
   */
  val population: Reference,
  /**
   * Name of the publisher (organization or individual)
   */
  val publisher: String? = null,
  val relatedArtifact: List<RelatedArtifact> = listOf(),
  val resultsByExposure: List<EffectEvidenceSynthesisResultsByExposure> = listOf(),
  val reviewer: List<ContactDetail> = listOf(),
  /**
   * What sample size was involved?
   */
  val sampleSize: EffectEvidenceSynthesisSampleSize? = null,
  /**
   * draft | active | retired | unknown
   */
  val status: String? = null,
  /**
   * Type of study
   */
  val studyType: CodeableConcept? = null,
  /**
   * Type of synthesis
   */
  val synthesisType: CodeableConcept? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Name for this effect evidence synthesis (human friendly)
   */
  val title: String? = null,
  val topic: List<CodeableConcept> = listOf(),
  /**
   * Canonical identifier for this effect evidence synthesis, represented as a URI (globally unique)
   */
  val url: String? = null,
  val useContext: List<UsageContext> = listOf(),
  /**
   * Business version of the effect evidence synthesis
   */
  val version: String? = null
) : DomainResource
