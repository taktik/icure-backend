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
package org.taktik.icure.fhir.entities.r4.nutritionorder

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Enteral formula components
 *
 * Feeding provided through the gastrointestinal tract via a tube, catheter, or stoma that delivers
 * nutrition distal to the oral cavity.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class NutritionOrderEnteralFormula(
  /**
   * Product or brand name of the modular additive
   */
  val additiveProductName: String? = null,
  /**
   * Type of modular component to add to the feeding
   */
  val additiveType: CodeableConcept? = null,
  val administration: List<NutritionOrderEnteralFormulaAdministration> = listOf(),
  /**
   * Formula feeding instructions expressed as text
   */
  val administrationInstruction: String? = null,
  /**
   * Product or brand name of the enteral or infant formula
   */
  val baseFormulaProductName: String? = null,
  /**
   * Type of enteral or infant formula
   */
  val baseFormulaType: CodeableConcept? = null,
  /**
   * Amount of energy per specified volume that is required
   */
  val caloricDensity: Quantity? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Upper limit on formula volume per unit of time
   */
  val maxVolumeToDeliver: Quantity? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * How the formula should enter the patient's gastrointestinal tract
   */
  val routeofAdministration: CodeableConcept? = null
) : BackboneElement
