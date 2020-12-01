//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.substancesourcematerial

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * This subclause describes the organism which the substance is derived from. For vaccines, the
 * parent organism shall be specified based on these subclause elements. As an example, full taxonomy
 * will be described for the Substance Name: ., Leaf
 *
 * This subclause describes the organism which the substance is derived from. For vaccines, the
 * parent organism shall be specified based on these subclause elements. As an example, full taxonomy
 * will be described for the Substance Name: ., Leaf.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceSourceMaterialOrganism(
  val author: List<SubstanceSourceMaterialOrganismAuthor> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * The family of an organism shall be specified
   */
  val family: CodeableConcept? = null,
  /**
   * The genus of an organism shall be specified; refers to the Latin epithet of the genus element
   * of the plant/animal scientific name; it is present in names for genera, species and infraspecies
   */
  val genus: CodeableConcept? = null,
  /**
   * 4.9.13.8.1 Hybrid species maternal organism ID (Optional)
   */
  val hybrid: SubstanceSourceMaterialOrganismHybrid? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * The intraspecific description of an organism shall be specified based on a controlled
   * vocabulary. For Influenza Vaccine, the intraspecific description shall contain the syntax of the
   * antigen in line with the WHO convention
   */
  val intraspecificDescription: String? = null,
  /**
   * The Intraspecific type of an organism shall be specified
   */
  val intraspecificType: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * 4.9.13.7.1 Kingdom (Conditional)
   */
  val organismGeneral: SubstanceSourceMaterialOrganismOrganismGeneral? = null,
  /**
   * The species of an organism shall be specified; refers to the Latin epithet of the species of
   * the plant/animal; it is present in names for species and infraspecies
   */
  val species: CodeableConcept? = null
) : BackboneElement
