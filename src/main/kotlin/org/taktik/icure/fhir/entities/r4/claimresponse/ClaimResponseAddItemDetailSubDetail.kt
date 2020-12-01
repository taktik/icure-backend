//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.claimresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.money.Money

/**
 * Insurer added line items
 *
 * The third-tier service adjudications for payor added services.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ClaimResponseAddItemDetailSubDetail(
  val adjudication: List<ClaimResponseItemAdjudication> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Price scaling factor
   */
  val factor: Float? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val modifier: List<CodeableConcept> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Total item cost
   */
  val net: Money? = null,
  val noteNumber: List<Int> = listOf(),
  /**
   * Billing, service, product, or drug code
   */
  val productOrService: CodeableConcept,
  /**
   * Count of products or services
   */
  val quantity: Quantity? = null,
  /**
   * Fee, charge or cost per item
   */
  val unitPrice: Money? = null
) : BackboneElement
