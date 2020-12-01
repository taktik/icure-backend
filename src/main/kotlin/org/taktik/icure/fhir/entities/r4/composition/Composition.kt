//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.composition

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
 * A set of resources composed into a single coherent clinical statement with clinical attestation
 *
 * A set of healthcare-related information that is assembled together into a single logical package
 * that provides a single coherent statement of meaning, establishes its own context and that has
 * clinical attestation with regard to who is making the statement. A Composition defines the structure
 * and narrative content necessary for a document. However, a Composition alone does not constitute a
 * document. Rather, the Composition must be the first entry in a Bundle where Bundle.type=document,
 * and any other resources referenced from Composition must be included as subsequent entries in the
 * Bundle (for example Patient, Practitioner, Encounter, etc.).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Composition(
  val attester: List<CompositionAttester> = listOf(),
  val author: List<Reference> = listOf(),
  val category: List<CodeableConcept> = listOf(),
  /**
   * As defined by affinity domain
   */
  val confidentiality: String? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Organization which maintains the composition
   */
  val custodian: Reference? = null,
  /**
   * Composition editing time
   */
  val date: String? = null,
  /**
   * Context of the Composition
   */
  val encounter: Reference? = null,
  val event: List<CompositionEvent> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * Version-independent identifier for the Composition
   */
  val identifier: Identifier? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val relatesTo: List<CompositionRelatesTo> = listOf(),
  val section: List<CompositionSection> = listOf(),
  /**
   * preliminary | final | amended | entered-in-error
   */
  val status: String? = null,
  /**
   * Who and/or what the composition is about
   */
  val subject: Reference? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Human Readable name/title
   */
  val title: String? = null,
  /**
   * Kind of composition (LOINC if possible)
   */
  val type: CodeableConcept
) : DomainResource
