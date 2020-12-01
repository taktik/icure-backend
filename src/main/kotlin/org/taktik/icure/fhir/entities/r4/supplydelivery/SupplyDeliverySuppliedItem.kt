//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.supplydelivery

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * The item that is delivered or supplied
 *
 * The item that is being delivered or has been supplied.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SupplyDeliverySuppliedItem(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Medication, Substance, or Device supplied
   */
  val itemCodeableConcept: CodeableConcept? = null,
  /**
   * Medication, Substance, or Device supplied
   */
  val itemReference: Reference? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Amount dispensed
   */
  val quantity: Quantity? = null
) : BackboneElement
