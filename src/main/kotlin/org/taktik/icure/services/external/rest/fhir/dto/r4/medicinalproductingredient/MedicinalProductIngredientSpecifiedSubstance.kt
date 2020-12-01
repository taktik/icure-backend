//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicinalproductingredient

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * A specified substance that comprises this ingredient
 *
 * A specified substance that comprises this ingredient.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductIngredientSpecifiedSubstance(
  /**
   * The specified substance
   */
  val code: CodeableConcept,
  /**
   * Confidentiality level of the specified substance as the ingredient
   */
  val confidentiality: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * The group of specified substance, e.g. group 1 to 4
   */
  val group: CodeableConcept,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val strength: List<MedicinalProductIngredientSpecifiedSubstanceStrength> = listOf()
) : BackboneElement
