//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.paymentreconciliation

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.money.Money
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Settlement particulars
 *
 * Distribution of the payment amount for a previously acknowledged payable.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PaymentReconciliationDetail(
  /**
   * Amount allocated to this payable
   */
  val amount: Money? = null,
  /**
   * Date of commitment to pay
   */
  val date: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Business identifier of the payment detail
   */
  val identifier: Identifier? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Recipient of the payment
   */
  val payee: Reference? = null,
  /**
   * Business identifier of the prior payment detail
   */
  val predecessor: Identifier? = null,
  /**
   * Request giving rise to the payment
   */
  val request: Reference? = null,
  /**
   * Response committing to a payment
   */
  val response: Reference? = null,
  /**
   * Contact for the response
   */
  val responsible: Reference? = null,
  /**
   * Submitter of the request
   */
  val submitter: Reference? = null,
  /**
   * Category of payment
   */
  val type: CodeableConcept
) : BackboneElement
