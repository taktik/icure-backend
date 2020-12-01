//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.specimen

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.annotation.Annotation
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Sample for analysis
 *
 * A sample to be used for analysis.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Specimen(
  /**
   * Identifier assigned by the lab
   */
  val accessionIdentifier: Identifier? = null,
  /**
   * Collection details
   */
  val collection: SpecimenCollection? = null,
  val condition: List<CodeableConcept> = listOf(),
  override val contained: List<Resource> = listOf(),
  val container: List<SpecimenContainer> = listOf(),
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
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  val parent: List<Reference> = listOf(),
  val processing: List<SpecimenProcessing> = listOf(),
  /**
   * The time when specimen was received for processing
   */
  val receivedTime: String? = null,
  val request: List<Reference> = listOf(),
  /**
   * available | unavailable | unsatisfactory | entered-in-error
   */
  val status: String? = null,
  /**
   * Where the specimen came from. This may be from patient(s), from a location (e.g., the source of
   * an environmental sample), or a sampling of a substance or a device
   */
  val subject: Reference? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Kind of material that forms the specimen
   */
  val type: CodeableConcept? = null
) : DomainResource
