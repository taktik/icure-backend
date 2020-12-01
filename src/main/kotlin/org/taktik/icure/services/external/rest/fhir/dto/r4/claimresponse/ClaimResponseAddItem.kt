//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.claimresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.address.Address
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.money.Money
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Insurer added line items
 *
 * The first-tier service adjudications for payor added product or service lines.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ClaimResponseAddItem(
  val adjudication: List<ClaimResponseItemAdjudication> = listOf(),
  /**
   * Anatomical location
   */
  val bodySite: CodeableConcept? = null,
  val detail: List<ClaimResponseAddItemDetail> = listOf(),
  val detailSequence: List<Int> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Price scaling factor
   */
  val factor: Float? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val itemSequence: List<Int> = listOf(),
  /**
   * Place of service or where product was supplied
   */
  val locationAddress: Address? = null,
  /**
   * Place of service or where product was supplied
   */
  val locationCodeableConcept: CodeableConcept? = null,
  /**
   * Place of service or where product was supplied
   */
  val locationReference: Reference? = null,
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
  val programCode: List<CodeableConcept> = listOf(),
  val provider: List<Reference> = listOf(),
  /**
   * Count of products or services
   */
  val quantity: Quantity? = null,
  /**
   * Date or dates of service or product delivery
   */
  val servicedDate: String? = null,
  /**
   * Date or dates of service or product delivery
   */
  val servicedPeriod: Period? = null,
  val subSite: List<CodeableConcept> = listOf(),
  val subdetailSequence: List<Int> = listOf(),
  /**
   * Fee, charge or cost per item
   */
  val unitPrice: Money? = null
) : BackboneElement
