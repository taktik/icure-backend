//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.nutritionorder

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.timing.Timing

/**
 * Oral diet components
 *
 * Diet given orally in contrast to enteral (tube) feeding.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class NutritionOrderOralDiet(
  override val extension: List<Extension> = listOf(),
  val fluidConsistencyType: List<CodeableConcept> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Instructions or additional information about the oral diet
   */
  val instruction: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val nutrient: List<NutritionOrderOralDietNutrient> = listOf(),
  val schedule: List<Timing> = listOf(),
  val texture: List<NutritionOrderOralDietTexture> = listOf(),
  val type: List<CodeableConcept> = listOf()
) : BackboneElement
