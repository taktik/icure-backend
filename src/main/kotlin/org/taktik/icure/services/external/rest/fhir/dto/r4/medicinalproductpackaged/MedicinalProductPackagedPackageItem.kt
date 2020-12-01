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
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicinalproductpackaged

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.prodcharacteristic.ProdCharacteristic
import org.taktik.icure.services.external.rest.fhir.dto.r4.productshelflife.ProductShelfLife
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * A packaging item, as a contained for medicine, possibly with other packaging items within
 *
 * A packaging item, as a contained for medicine, possibly with other packaging items within.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductPackagedPackageItem(
  val alternateMaterial: List<CodeableConcept> = listOf(),
  val device: List<Reference> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  val manufacturedItem: List<Reference> = listOf(),
  val manufacturer: List<Reference> = listOf(),
  val material: List<CodeableConcept> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  val otherCharacteristics: List<CodeableConcept> = listOf(),
  val packageItem: List<MedicinalProductPackagedPackageItem> = listOf(),
  /**
   * Dimensions, color etc.
   */
  val physicalCharacteristics: ProdCharacteristic? = null,
  /**
   * The quantity of this package in the medicinal product, at the current level of packaging. The
   * outermost is always 1
   */
  val quantity: Quantity,
  val shelfLifeStorage: List<ProductShelfLife> = listOf(),
  /**
   * The physical type of the container of the medicine
   */
  val type: CodeableConcept
) : BackboneElement
