//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.explanationofbenefit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.money.Money
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Additional items
 *
 * Second-tier of goods and services.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ExplanationOfBenefitItemDetail(
  val adjudication: List<ExplanationOfBenefitItemAdjudication> = listOf(),
  /**
   * Benefit classification
   */
  val category: CodeableConcept? = null,
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
  val programCode: List<CodeableConcept> = listOf(),
  /**
   * Count of products or services
   */
  val quantity: Quantity? = null,
  /**
   * Revenue or cost center code
   */
  val revenue: CodeableConcept? = null,
  /**
   * Product or service provided
   */
  val sequence: Int? = null,
  val subDetail: List<ExplanationOfBenefitItemDetailSubDetail> = listOf(),
  val udi: List<Reference> = listOf(),
  /**
   * Fee, charge or cost per item
   */
  val unitPrice: Money? = null
) : BackboneElement
