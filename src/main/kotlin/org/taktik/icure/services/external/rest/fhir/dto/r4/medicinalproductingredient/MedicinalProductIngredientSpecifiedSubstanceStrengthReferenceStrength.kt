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
import org.taktik.icure.services.external.rest.fhir.dto.r4.ratio.Ratio

/**
 * Strength expressed in terms of a reference substance
 *
 * Strength expressed in terms of a reference substance.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductIngredientSpecifiedSubstanceStrengthReferenceStrength(
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
   * Strength expressed in terms of a reference substance
   */
  val strength: Ratio,
  /**
   * Strength expressed in terms of a reference substance
   */
  val strengthLowLimit: Ratio? = null,
  /**
   * Relevant reference substance
   */
  val substance: CodeableConcept? = null
) : BackboneElement
