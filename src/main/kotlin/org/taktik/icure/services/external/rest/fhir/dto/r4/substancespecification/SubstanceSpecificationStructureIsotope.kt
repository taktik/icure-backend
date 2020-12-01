//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.substancespecification

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier

/**
 * Applicable for single substances that contain a radionuclide or a non-natural isotopic ratio
 *
 * Applicable for single substances that contain a radionuclide or a non-natural isotopic ratio.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceSpecificationStructureIsotope(
  override val extension: List<Extension> = listOf(),
  /**
   * Half life - for a non-natural nuclide
   */
  val halfLife: Quantity? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Substance identifier for each non-natural or radioisotope
   */
  val identifier: Identifier? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The molecular weight or weight range (for proteins, polymers or nucleic acids)
   */
  val molecularWeight: SubstanceSpecificationStructureIsotopeMolecularWeight? = null,
  /**
   * Substance name for each non-natural or radioisotope
   */
  val name: CodeableConcept? = null,
  /**
   * The type of isotopic substitution present in a single substance
   */
  val substitution: CodeableConcept? = null
) : BackboneElement
