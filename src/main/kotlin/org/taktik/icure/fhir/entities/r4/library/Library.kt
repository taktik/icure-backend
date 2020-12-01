//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.library

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.attachment.Attachment
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.contactdetail.ContactDetail
import org.taktik.icure.fhir.entities.r4.datarequirement.DataRequirement
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.parameterdefinition.ParameterDefinition
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference
import org.taktik.icure.fhir.entities.r4.relatedartifact.RelatedArtifact
import org.taktik.icure.fhir.entities.r4.usagecontext.UsageContext

/**
 * Represents a library of quality improvement components
 *
 * The Library resource is a general-purpose container for knowledge asset definitions. It can be
 * used to describe and expose existing knowledge assets such as logic libraries and information model
 * descriptions, as well as to describe a collection of knowledge assets.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Library(
  /**
   * When the library was approved by publisher
   */
  val approvalDate: String? = null,
  val author: List<ContactDetail> = listOf(),
  val contact: List<ContactDetail> = listOf(),
  override val contained: List<Resource> = listOf(),
  val content: List<Attachment> = listOf(),
  /**
   * Use and/or publishing restrictions
   */
  val copyright: String? = null,
  val dataRequirement: List<DataRequirement> = listOf(),
  /**
   * Date last changed
   */
  val date: String? = null,
  /**
   * Natural language description of the library
   */
  val description: String? = null,
  val editor: List<ContactDetail> = listOf(),
  /**
   * When the library is expected to be used
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
  val jurisdiction: List<CodeableConcept> = listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * When the library was last reviewed
   */
  val lastReviewDate: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name for this library (computer friendly)
   */
  val name: String? = null,
  val parameter: List<ParameterDefinition> = listOf(),
  /**
   * Name of the publisher (organization or individual)
   */
  val publisher: String? = null,
  /**
   * Why this library is defined
   */
  val purpose: String? = null,
  val relatedArtifact: List<RelatedArtifact> = listOf(),
  val reviewer: List<ContactDetail> = listOf(),
  /**
   * draft | active | retired | unknown
   */
  val status: String? = null,
  /**
   * Type of individual the library content is focused on
   */
  val subjectCodeableConcept: CodeableConcept? = null,
  /**
   * Type of individual the library content is focused on
   */
  val subjectReference: Reference? = null,
  /**
   * Subordinate title of the library
   */
  val subtitle: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Name for this library (human friendly)
   */
  val title: String? = null,
  val topic: List<CodeableConcept> = listOf(),
  /**
   * logic-library | model-definition | asset-collection | module-definition
   */
  val type: CodeableConcept,
  /**
   * Canonical identifier for this library, represented as a URI (globally unique)
   */
  val url: String? = null,
  /**
   * Describes the clinical usage of the library
   */
  val usage: String? = null,
  val useContext: List<UsageContext> = listOf(),
  /**
   * Business version of the library
   */
  val version: String? = null
) : DomainResource
