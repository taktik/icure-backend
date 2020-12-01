//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.substancespecification

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Structural information
 *
 * Structural information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceSpecificationStructure(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val isotope: List<SubstanceSpecificationStructureIsotope> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Molecular formula
   */
  val molecularFormula: String? = null,
  /**
   * Specified per moiety according to the Hill system, i.e. first C, then H, then alphabetical,
   * each moiety separated by a dot
   */
  val molecularFormulaByMoiety: String? = null,
  /**
   * The molecular weight or weight range (for proteins, polymers or nucleic acids)
   */
  val molecularWeight: SubstanceSpecificationStructureIsotopeMolecularWeight? = null,
  /**
   * Optical activity type
   */
  val opticalActivity: CodeableConcept? = null,
  val representation: List<SubstanceSpecificationStructureRepresentation> = listOf(),
  val source: List<Reference> = listOf(),
  /**
   * Stereochemistry type
   */
  val stereochemistry: CodeableConcept? = null
) : BackboneElement
