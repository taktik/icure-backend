//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.documentreference

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * A reference to a document
 *
 * A reference to a document of any kind for any purpose. Provides metadata about the document so
 * that the document can be discovered and managed. The scope of a document is any seralized object
 * with a mime-type, so includes formal patient centric documents (CDA), cliical notes, scanned paper,
 * and non-patient specific documents like policy text.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DocumentReference(
  /**
   * Who/what authenticated the document
   */
  val authenticator: Reference? = null,
  val author: List<Reference> = listOf(),
  val category: List<CodeableConcept> = listOf(),
  override val contained: List<Resource> = listOf(),
  val content: List<DocumentReferenceContent> = listOf(),
  /**
   * Clinical context of document
   */
  val context: DocumentReferenceContext? = null,
  /**
   * Organization which maintains the document
   */
  val custodian: Reference? = null,
  /**
   * When this document reference was created
   */
  val date: String? = null,
  /**
   * Human-readable description
   */
  val description: String? = null,
  /**
   * preliminary | final | amended | entered-in-error
   */
  val docStatus: String? = null,
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
   * Master Version Specific Identifier
   */
  val masterIdentifier: Identifier? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val relatesTo: List<DocumentReferenceRelatesTo> = listOf(),
  val securityLabel: List<CodeableConcept> = listOf(),
  /**
   * current | superseded | entered-in-error
   */
  val status: String? = null,
  /**
   * Who/what is the subject of the document
   */
  val subject: Reference? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Kind of document (LOINC if possible)
   */
  val type: CodeableConcept? = null
) : DomainResource
