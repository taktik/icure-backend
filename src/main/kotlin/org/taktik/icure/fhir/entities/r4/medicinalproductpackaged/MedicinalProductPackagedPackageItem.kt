//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.medicinalproductpackaged

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.prodcharacteristic.ProdCharacteristic
import org.taktik.icure.fhir.entities.r4.productshelflife.ProductShelfLife
import org.taktik.icure.fhir.entities.r4.reference.Reference

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
