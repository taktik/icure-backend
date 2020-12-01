//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.substancespecification

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
 * The detailed description of a substance, typically at a level beyond what is used for prescribing
 *
 * The detailed description of a substance, typically at a level beyond what is used for
 * prescribing.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceSpecification(
  val code: List<SubstanceSpecificationString> = listOf(),
  /**
   * Textual comment about this record of a substance
   */
  val comment: String? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Textual description of the substance
   */
  val description: String? = null,
  /**
   * If the substance applies to only human or veterinary use
   */
  val domain: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * Identifier by which this substance is known
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
  val moiety: List<SubstanceSpecificationMoiety> = listOf(),
  val molecularWeight: List<SubstanceSpecificationStructureIsotopeMolecularWeight> = listOf(),
  val name: List<SubstanceSpecificationName> = listOf(),
  /**
   * Data items specific to nucleic acids
   */
  val nucleicAcid: Reference? = null,
  /**
   * Data items specific to polymers
   */
  val polymer: Reference? = null,
  val property: List<SubstanceSpecificationProperty> = listOf(),
  /**
   * Data items specific to proteins
   */
  val protein: Reference? = null,
  /**
   * General information detailing this substance
   */
  val referenceInformation: Reference? = null,
  val relationship: List<SubstanceSpecificationRelationship> = listOf(),
  val source: List<Reference> = listOf(),
  /**
   * Material or taxonomic/anatomical source for the substance
   */
  val sourceMaterial: Reference? = null,
  /**
   * Status of substance within the catalogue e.g. approved
   */
  val status: CodeableConcept? = null,
  /**
   * Structural information
   */
  val structure: SubstanceSpecificationStructure? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * High level categorization, e.g. polymer or nucleic acid
   */
  val type: CodeableConcept? = null
) : DomainResource
