//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.devicerequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.range.Range

/**
 * Device details
 *
 * Specific parameters for the ordered item.  For example, the prism value for lenses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DeviceRequestParameter(
  /**
   * Device detail
   */
  val code: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Value of detail
   */
  val valueBoolean: Boolean? = null,
  /**
   * Value of detail
   */
  val valueCodeableConcept: CodeableConcept? = null,
  /**
   * Value of detail
   */
  val valueQuantity: Quantity? = null,
  /**
   * Value of detail
   */
  val valueRange: Range? = null
) : BackboneElement
