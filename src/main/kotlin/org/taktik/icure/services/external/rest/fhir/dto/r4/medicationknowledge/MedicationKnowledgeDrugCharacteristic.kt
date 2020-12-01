//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicationknowledge

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Specifies descriptive properties of the medicine
 *
 * Specifies descriptive properties of the medicine, such as color, shape, imprints, etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationKnowledgeDrugCharacteristic(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Code specifying the type of characteristic of medication
   */
  val type: CodeableConcept? = null,
  /**
   * Description of the characteristic
   */
  val valueBase64Binary: String? = null,
  /**
   * Description of the characteristic
   */
  val valueCodeableConcept: CodeableConcept? = null,
  /**
   * Description of the characteristic
   */
  val valueQuantity: Quantity? = null,
  /**
   * Description of the characteristic
   */
  val valueString: String? = null
) : BackboneElement
