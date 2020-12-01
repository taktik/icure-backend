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
package org.taktik.icure.services.external.rest.fhir.dto.r4.substancesourcematerial

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Many complex materials are fractions of parts of plants, animals, or minerals. Fraction elements
 * are often necessary to define both Substances and Specified Group 1 Substances. For substances
 * derived from Plants, fraction information will be captured at the Substance information level ( .
 * Oils, Juices and Exudates). Additional information for Extracts, such as extraction solvent
 * composition, will be captured at the Specified Substance Group 1 information level. For
 * plasma-derived products fraction information will be captured at the Substance and the Specified
 * Substance Group 1 levels
 *
 * Many complex materials are fractions of parts of plants, animals, or minerals. Fraction elements
 * are often necessary to define both Substances and Specified Group 1 Substances. For substances
 * derived from Plants, fraction information will be captured at the Substance information level ( .
 * Oils, Juices and Exudates). Additional information for Extracts, such as extraction solvent
 * composition, will be captured at the Specified Substance Group 1 information level. For
 * plasma-derived products fraction information will be captured at the Substance and the Specified
 * Substance Group 1 levels.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceSourceMaterialFractionDescription(
  override val extension: List<Extension> = listOf(),
  /**
   * This element is capturing information about the fraction of a plant part, or human plasma for
   * fractionation
   */
  val fraction: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * The specific type of the material constituting the component. For Herbal preparations the
   * particulars of the extracts (liquid/dry) is described in Specified Substance Group 1
   */
  val materialType: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement
