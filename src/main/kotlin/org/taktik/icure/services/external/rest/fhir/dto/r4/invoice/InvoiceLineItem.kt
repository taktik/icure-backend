//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.invoice

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Line items of this Invoice
 *
 * Each line item represents one charge for goods and services rendered. Details such as date, code
 * and amount are found in the referenced ChargeItem resource.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class InvoiceLineItem(
  /**
   * Reference to ChargeItem containing details of this line item or an inline billing code
   */
  val chargeItemCodeableConcept: CodeableConcept,
  /**
   * Reference to ChargeItem containing details of this line item or an inline billing code
   */
  val chargeItemReference: Reference,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val priceComponent: List<InvoiceLineItemPriceComponent> = listOf(),
  /**
   * Sequence number of line item
   */
  val sequence: Int? = null
) : BackboneElement
