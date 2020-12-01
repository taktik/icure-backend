//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.documentmanifest

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
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * A list that defines a set of documents
 *
 * A collection of documents compiled for a purpose together with metadata that applies to the
 * collection.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DocumentManifest(
  val author: List<Reference> = listOf(),
  override val contained: List<Resource> = listOf(),
  val content: List<Reference> = listOf(),
  /**
   * When this document manifest created
   */
  val created: String? = null,
  /**
   * Human-readable description (title)
   */
  val description: String? = null,
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
   * Unique Identifier for the set of documents
   */
  val masterIdentifier: Identifier? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val recipient: List<Reference> = listOf(),
  val related: List<DocumentManifestRelated> = listOf(),
  /**
   * The source system/application/software
   */
  val source: String? = null,
  /**
   * current | superseded | entered-in-error
   */
  val status: String? = null,
  /**
   * The subject of the set of documents
   */
  val subject: Reference? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Kind of document set
   */
  val type: CodeableConcept? = null
) : DomainResource
