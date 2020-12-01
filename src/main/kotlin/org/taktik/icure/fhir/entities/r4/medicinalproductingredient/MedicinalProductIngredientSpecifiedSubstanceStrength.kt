//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.medicinalproductingredient

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.ratio.Ratio

/**
 * Quantity of the substance or specified substance present in the manufactured item or
 * pharmaceutical product
 *
 * Quantity of the substance or specified substance present in the manufactured item or
 * pharmaceutical product.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductIngredientSpecifiedSubstanceStrength(
  /**
   * The strength per unitary volume (or mass)
   */
  val concentration: Ratio? = null,
  /**
   * A lower limit for the strength per unitary volume (or mass), for when there is a range. The
   * concentration attribute then becomes the upper limit
   */
  val concentrationLowLimit: Ratio? = null,
  val country: List<CodeableConcept> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * For when strength is measured at a particular point or distance
   */
  val measurementPoint: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The quantity of substance in the unit of presentation, or in the volume (or mass) of the single
   * pharmaceutical product or manufactured item
   */
  val presentation: Ratio,
  /**
   * A lower limit for the quantity of substance in the unit of presentation. For use when there is
   * a range of strengths, this is the lower limit, with the presentation attribute becoming the upper
   * limit
   */
  val presentationLowLimit: Ratio? = null,
  val referenceStrength: List<MedicinalProductIngredientSpecifiedSubstanceStrengthReferenceStrength>
      = listOf()
) : BackboneElement
