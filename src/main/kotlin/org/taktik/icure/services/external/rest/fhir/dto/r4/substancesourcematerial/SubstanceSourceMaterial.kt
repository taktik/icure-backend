//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.substancesourcematerial

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

/**
 * Source material shall capture information on the taxonomic and anatomical origins as well as the
 * fraction of a material that can result in or can be modified to form a substance. This set of data
 * elements shall be used to define polymer substances isolated from biological matrices. Taxonomic and
 * anatomical origins shall be described using a controlled vocabulary as required. This information is
 * captured for naturally derived polymers ( . starch) and structurally diverse substances. For
 * Organisms belonging to the Kingdom Plantae the Substance level defines the fresh material of a
 * single species or infraspecies, the Herbal Drug and the Herbal preparation. For Herbal preparations,
 * the fraction information will be captured at the Substance information level and additional
 * information for herbal extracts will be captured at the Specified Substance Group 1 information
 * level. See for further explanation the Substance Class: Structurally Diverse and the herbal annex
 *
 * Source material shall capture information on the taxonomic and anatomical origins as well as the
 * fraction of a material that can result in or can be modified to form a substance. This set of data
 * elements shall be used to define polymer substances isolated from biological matrices. Taxonomic and
 * anatomical origins shall be described using a controlled vocabulary as required. This information is
 * captured for naturally derived polymers ( . starch) and structurally diverse substances. For
 * Organisms belonging to the Kingdom Plantae the Substance level defines the fresh material of a
 * single species or infraspecies, the Herbal Drug and the Herbal preparation. For Herbal preparations,
 * the fraction information will be captured at the Substance information level and additional
 * information for herbal extracts will be captured at the Specified Substance Group 1 information
 * level. See for further explanation the Substance Class: Structurally Diverse and the herbal annex.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceSourceMaterial(
  override val contained: List<Resource> = listOf(),
  val countryOfOrigin: List<CodeableConcept> = listOf(),
  /**
   * Stage of life for animals, plants, insects and microorganisms. This information shall be
   * provided only when the substance is significantly different in these stages (e.g. foetal bovine
   * serum)
   */
  val developmentStage: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  val fractionDescription: List<SubstanceSourceMaterialFractionDescription> = listOf(),
  val geographicalLocation: List<String> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
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
  /**
   * This subclause describes the organism which the substance is derived from. For vaccines, the
   * parent organism shall be specified based on these subclause elements. As an example, full taxonomy
   * will be described for the Substance Name: ., Leaf
   */
  val organism: SubstanceSourceMaterialOrganism? = null,
  /**
   * The unique identifier associated with the source material parent organism shall be specified
   */
  val organismId: Identifier? = null,
  /**
   * The organism accepted Scientific name shall be provided based on the organism taxonomy
   */
  val organismName: String? = null,
  val parentSubstanceId: List<Identifier> = listOf(),
  val parentSubstanceName: List<String> = listOf(),
  val partDescription: List<SubstanceSourceMaterialPartDescription> = listOf(),
  /**
   * General high level classification of the source material specific to the origin of the material
   */
  val sourceMaterialClass: CodeableConcept? = null,
  /**
   * The state of the source material when extracted
   */
  val sourceMaterialState: CodeableConcept? = null,
  /**
   * The type of the source material shall be specified based on a controlled vocabulary. For
   * vaccines, this subclause refers to the class of infectious agent
   */
  val sourceMaterialType: CodeableConcept? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
