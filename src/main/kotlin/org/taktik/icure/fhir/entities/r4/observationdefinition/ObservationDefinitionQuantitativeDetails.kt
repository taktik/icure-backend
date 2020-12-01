//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.observationdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Characteristics of quantitative results
 *
 * Characteristics for quantitative results of this observation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ObservationDefinitionQuantitativeDetails(
  /**
   * SI to Customary unit conversion factor
   */
  val conversionFactor: Float? = null,
  /**
   * Customary unit for quantitative results
   */
  val customaryUnit: CodeableConcept? = null,
  /**
   * Decimal precision of observation quantitative results
   */
  val decimalPrecision: Int? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * SI unit for quantitative results
   */
  val unit: CodeableConcept? = null
) : BackboneElement
