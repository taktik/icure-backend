//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.medication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.ratio.Ratio
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Active or inactive ingredient
 *
 * Identifies a particular constituent of interest in the product.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationIngredient(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Active ingredient indicator
   */
  val isActive: Boolean? = null,
  /**
   * The actual ingredient or content
   */
  val itemCodeableConcept: CodeableConcept,
  /**
   * The actual ingredient or content
   */
  val itemReference: Reference,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Quantity of ingredient present
   */
  val strength: Ratio? = null
) : BackboneElement
