//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicinalproductmanufactured

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.prodcharacteristic.ProdCharacteristic
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * The manufactured item as contained in the packaged medicinal product
 *
 * The manufactured item as contained in the packaged medicinal product.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductManufactured(
  override val contained: List<Resource> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val ingredient: List<Reference> = listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Dose form as manufactured and before any transformation into the pharmaceutical product
   */
  val manufacturedDoseForm: CodeableConcept,
  val manufacturer: List<Reference> = listOf(),
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val otherCharacteristics: List<CodeableConcept> = listOf(),
  /**
   * Dimensions, color etc.
   */
  val physicalCharacteristics: ProdCharacteristic? = null,
  /**
   * The quantity or "count number" of the manufactured item
   */
  val quantity: Quantity,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * The “real world” units in which the quantity of the manufactured item is described
   */
  val unitOfPresentation: CodeableConcept? = null
) : DomainResource
