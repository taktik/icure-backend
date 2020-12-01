//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.explanationofbenefit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.money.Money

/**
 * Payment Details
 *
 * Payment details for the adjudication of the claim.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ExplanationOfBenefitPayment(
  /**
   * Payment adjustment for non-claim issues
   */
  val adjustment: Money? = null,
  /**
   * Explanation for the variance
   */
  val adjustmentReason: CodeableConcept? = null,
  /**
   * Payable amount after adjustment
   */
  val amount: Money? = null,
  /**
   * Expected date of payment
   */
  val date: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Business identifier for the payment
   */
  val identifier: Identifier? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Partial or complete payment
   */
  val type: CodeableConcept? = null
) : BackboneElement
