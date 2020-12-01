/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.substancespecification

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

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
