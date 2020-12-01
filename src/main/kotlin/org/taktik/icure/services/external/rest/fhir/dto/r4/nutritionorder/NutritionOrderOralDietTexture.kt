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

/**
 * Required  texture modifications
 *
 * Class that describes any texture modifications required for the patient to safely consume various
 * types of solid foods.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class NutritionOrderOralDietTexture(
  override val extension: List<Extension> = listOf(),
  /**
   * Concepts that are used to identify an entity that is ingested for nutritional purposes
   */
  val foodType: CodeableConcept? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Code to indicate how to alter the texture of the foods, e.g. pureed
   */
  val modifier: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement
